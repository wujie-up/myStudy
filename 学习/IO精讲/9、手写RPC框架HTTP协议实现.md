# 手写RPC框架HTTP协议实现

## 1、HTTP协议实现

### 1.1、HttpProtocol调用协议

```java
public class HttpProtocol implements Protocol {
    @Override
    public Invoker getInvoker(Uri uri, Request request) {
        HttpClient client = HttpClientFacory.createCli(uri);
        return new HttpInvoker(client, request);
    }
}
```



### 1.2、HttpClientFacory ：http连接工厂

```java
public class HttpClientFacory {
    public static HttpClient createCli(Uri uri) {
        String httpUrl = uri.getHost() + ":" + uri.getPort();
        try {
            URL url = new URL("http://" + httpUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.setDoOutput(true);
            connection.setRequestMethod("POST");
            return new HttpClient(connection);
        } catch (Exception e) {
            throw new RuntimeException("http连接建立失败");
        }
    }
}
```



### 1.3、HttpInvoker：http调用执行器

```java
@Slf4j
public class HttpInvoker implements Invoker{
    private HttpClient client;
    private Request request;

    public HttpInvoker(HttpClient client, Request request) {
        this.request = request;
        this.client = client;
    }

    @Override
    public Object invoke()  {
        Object res;
        try {
            res = client.doRequest(request);
        } catch (Exception e) {
           log.info("{}", e);
            throw new RuntimeException();
        }
        return res;
    }
}
```



### 1.4、HttpClient：http连接客户端

```java
public class HttpClient {
    private HttpURLConnection connection;

    public HttpClient(HttpURLConnection connection) {
        this.connection = connection;
    }

    public Object doRequest(Request request) throws Exception {
        Object res;
        byte[] reqBytes = SerializeUtil.obj2Bytes(request);
        try (OutputStream os = connection.getOutputStream()) {
            os.write(reqBytes);
            os.flush();

            Response response = getResponse();
            res = response.getObj();
            if (res instanceof RpcException) {
                RpcException rpcE = (RpcException) res;
                throw rpcE;
            }
        }
        return res;
    }

    private Response getResponse() throws IOException {
        if (connection.getResponseCode() == 200) {
            InputStream is = connection.getInputStream();
            Response resp = (Response) SerializeUtil.inputStream2Obj(is);
            is.close();
            return resp;
        } else {
            throw new RuntimeException("远程服务调用异常");
        }
    }
}
```



### 1.5、HttpServer：服务端

服务端使用Jetty来提供http服务

```java
public class HttpServer {
    public void start(int port) {
        Server server = new Server(port);
        ServletContextHandler handler = new ServletContextHandler(server, "/");
        server.setHandler(handler);
        handler.addServlet(HttpRpcServlet.class, "/*");
        try {
            server.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
```



### 1.6、协议注册中添加

```java
public class InvokeFactory {

    private static Map<String, Protocol> protocolMap = new HashMap<>();

    static {
        // 在这里初始化所有的协议
        protocolMap.put("netty", new NettyProtocol());
        protocolMap.put("http", new HttpProtocol());
    }

    public static Invoker createInvoke(Class clazz, Method method, Object[] args) {
        // 1、获取服务地址
        Uri uri = getRemoteUri(clazz.getName());
        // 2、确定使用的协议, 协议只负责 创建连接渠道
        Protocol protocol = getProtocol(method, clazz);
        // 3、通用的请求封装
        Request request = wrapperRequest(clazz.getName(), method, args);
        // 4、通过协议返回调用Invoker对象，进行调用
        return buildInvoker(protocol, uri, request);
    }

    private static Request wrapperRequest(String serviceName, Method method, Object[] args) {
        Request request =  new Request();
        Content content = Content.builder()
                .serviceName(serviceName)
                .methodName(method.getName())
                .paramTypes(method.getParameterTypes())
                .args(args)
                .build();
        request.setRequestId(UUID.randomUUID().toString());
        request.setContent(content);
        return request;
    }

    private static Invoker buildInvoker(Protocol protocol, Uri uri, Request request) {
        return protocol.getInvoker(uri, request);
    }

    /**
     *  获取远程服务地址
     * @param serviceName
     * @return
     */
    private static Uri getRemoteUri(String serviceName){
        // 1、先尝试从本地获取
        List<Uri> uris = RegistryCenter.getServerUris(serviceName);
        // 2、本地获取不到，从远程获取
        if (CollectionUtils.isEmpty(uris)) {
            synchronized (RegistryCenter.class) {
                if (CollectionUtils.isEmpty(uris)) {
                    uris = getFromRemote();
                    RegistryCenter.registry(serviceName, uris);
                }
            }
        }
        // 3、拿到服务列表后，做负责均衡
        return loadBalance(uris);
    }

    private static Uri loadBalance(List<Uri> uris) {
        // todo 可以设置多种负载均衡策略，此处随机
        Random r = new Random();
        int index = r.nextInt(uris.size());
        return uris.get(index);
    };

    private static List<Uri> getFromRemote() {
        // todo 从注册中心拉取服务
        return new ArrayList<>();
    }


    private static <T> Protocol getProtocol(Method method, Class<T> clazz) {
        Rpc anno = method.getAnnotation(Rpc.class);
        if (null == anno) {
            anno = clazz.getAnnotation(Rpc.class);
        } else {
            throw new RuntimeException();
        }

        String pt = anno.protocol();
        return protocolMap.get(pt);
    }
}
```



### 1.7、修改服务注解协议

```java
@Rpc(protocol = "http") // 改为http协议
public interface UserService {
    User get(int id);
}
```



### 1.8、测试代码

```java
@Slf4j
public class RpcTest {

    AtomicInteger count = new AtomicInteger(0);

    @Test
    public void initServer() throws IOException {
        RemoteServiceRegistry.registry(UserService.class.getName(), new UserServiceImpl());

//        NettyServer nettyServer = new NettyServer(1, 3);
//        nettyServer.start(9090);

        HttpServer httpServer = new HttpServer();
        httpServer.start(9090);
        System.in.read();
    }

    @Test
    public void cliInvoke() {
        LocalServiceRegistry.registry(UserService.class.getName(), new UserServiceFailImpl());
        int num = 20;
        CountDownLatch downLatch = new CountDownLatch(num);
        AtomicInteger count = new AtomicInteger(num);
        for (int i = 0; i < num; i++) {
            new Thread(
                    () -> {
                        UserService userService = InvokeProxy.proxy(UserService.class);
                        User user = userService.get(22);
                        log.info("第{}次完成调用，结果:{}", count.incrementAndGet(), user.getName());
                        downLatch.countDown();
                    }
            ).start();
        }
        try {
            downLatch.await();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
```



## 2、Netty实现HTTP协议

### 2.1 Netty服务端

只需要替换ChannelHandler即可

```java
public class NettyServer {

    private int bossSize;
    private int workerSize;

    public NettyServer(int bossSize, int workerSize) {
        this.bossSize = bossSize;
        this.workerSize = workerSize;
    }

    public void start(int port) {
        NioEventLoopGroup bossGroup = new NioEventLoopGroup(bossSize);
        NioEventLoopGroup workerGroup = new NioEventLoopGroup(workerSize);

        ServerBootstrap bootStrap = new ServerBootstrap();
        bootStrap.group(bossGroup, workerGroup)
                .channel(NioServerSocketChannel.class)
                .option(ChannelOption.SO_BACKLOG, 1024)
                .option(ChannelOption.RCVBUF_ALLOCATOR, new FixedRecvByteBufAllocator(65535)) // 解决不能发送字节不超过1024问题
                .childHandler(new ChannelInitializer<Channel>() {
                    @Override
                    protected void initChannel(Channel ch) throws Exception {
                        ChannelPipeline p = ch.pipeline();
//                        p.addLast(new NettyRpcDecoder());
//                        p.addLast(new NettyServerHanlder());
                        // 使用http协议
                        p.addLast(new HttpServerCodec());
                        p.addLast(new HttpObjectAggregator(65535));
                        p.addLast(new HttpServerHandler());
                    }
                });

        try {
            ChannelFuture cf = bootStrap.bind(port).sync();
            cf.addListener(new ChannelFutureListener() {
                @Override
                public void operationComplete(ChannelFuture cf) throws Exception {
                    if (cf.isSuccess()) {
                        System.out.println("服务启动成功......");
                    } else {
                        System.out.println("服务启动失败......");
                    }
                }
            });
            cf.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
```

### 处理请求的handler

```java
public class HttpServerHandler extends ChannelInboundHandlerAdapter {
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        FullHttpRequest req = (FullHttpRequest) msg;
        ByteBuf bf = req.content();
        byte[] bytes = new byte[bf.readableBytes()];
        bf.readBytes(bytes);
        Request request = (Request) SerializeUtil.bytes2Obj(bytes);
        Object res =  invoke(request.getContent());

        Response resp = new Response();
        resp.setRequestId(request.getRequestId());
        resp.setObj(res);

        byte[] resBytes = SerializeUtil.obj2Bytes(resp);
        ByteBuf byteBuf = PooledByteBufAllocator.DEFAULT.directBuffer(resBytes.length);
        byteBuf.writeBytes(resBytes);
        DefaultFullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK, byteBuf);
        // 要在请求头中写明长度
        response.headers().add("content-length", resBytes.length);
        ctx.writeAndFlush(response);
    }

    private Object invoke(Content content) {
        Object res;
        String serviceName = content.getServiceName();
        String methodName = content.getMethodName();
        Class[] paramTypes = content.getParamTypes();
        Object[] args = content.getArgs();

        Object obj = RemoteServiceRegistry.getService(serviceName);
        final Method method;
        try {
            method = obj.getClass().getMethod(methodName, paramTypes);
            res = method.invoke(obj, args);
        } catch (Exception e) {
            res = new RpcException(e);
        }
        return res;
    }
}
```



### 2.2 Netty客户端

```java
public class NettyClient {
    private Channel channel;

    private Bootstrap bootstrap;
    /**
     * 用来标识当前客户端是否已经连接，必须volatile修饰
     */
    private volatile boolean connected;

    private final Object lock = new Object();

    public NettyClient(int groupSize) {
        this.connected = false;
        init(groupSize);
    }

    public void init(int groupSize) {
        NioEventLoopGroup group = new NioEventLoopGroup(groupSize);
        bootstrap = new Bootstrap();
        bootstrap.group(group)
                .channel(NioSocketChannel.class)
                .option(ChannelOption.SO_KEEPALIVE, true)
                .option(ChannelOption.RCVBUF_ALLOCATOR, new FixedRecvByteBufAllocator(65535)) // 解决不能发送字节不超过1024问题
                .handler(new ChannelInitializer<NioSocketChannel>() {
                    @Override
                    protected void initChannel(NioSocketChannel channel) throws Exception {
                        ChannelPipeline p = channel.pipeline();
//                        p.addLast(new NettyRpcDecoder());
//                        p.addLast(new NettyCliHandler());
                        p.addLast(new HttpClientCodec());
                        p.addLast(new HttpObjectAggregator(65535));
                        p.addLast(new HttpCliHandler());
                    }
                });
    }

    public void connect(Uri uri) {
        if (!connected) {
            synchronized (lock) {
                if (!connected) {
                    try {
                        this.channel = bootstrap.connect(uri.getHost(), uri.getPort()).sync().channel();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    connected = true;
                }
            }
        }
    }

    public void send(Object obj) {
        this.channel.writeAndFlush(obj);
    }

    public boolean isAlive() {
        // 必须满足两个情况从，才算存活
        return connected && this.channel.isActive();
    }
}
```



### 处理响应handler

```java
public class HttpCliHandler extends ChannelInboundHandlerAdapter {
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        FullHttpResponse response = (FullHttpResponse) msg;
        ByteBuf byteBuf = response.content();
        byte[] bytes = new byte[byteBuf.readableBytes()];
        byteBuf.readBytes(bytes);
        Response resp = (Response) SerializeUtil.bytes2Obj(bytes);
        ResponseMappingCallBack.callBack(resp);
    }
}
```



### 2.3 调用执行器

```java
public class NettyInvoker implements Invoker{

    private NettyClient client;
    private Request request;

    public NettyInvoker(NettyClient client, Request request) {
        this.client = client;
        this.request = request;
    }

    @Override
    public Object invoke() throws Exception{

//        CompletableFuture<Response> future = nettyInvoke(); // netty协议调用
        CompletableFuture<Response> future = httpInvoke();  // http协议调用

        Response response = future.get();
        Object res = response.getObj();
        if (res instanceof RpcException) {
            RpcException rpcEx = (RpcException) res;
            throw rpcEx;
        }
        return res;
    }

    private CompletableFuture<Response> httpInvoke() {
        CompletableFuture<Response> future = new CompletableFuture<>();
        ResponseMappingCallBack.addCallBack(request.getRequestId(), future);

        byte[] resBytes = SerializeUtil.obj2Bytes(request);
        ByteBuf byteBuf = PooledByteBufAllocator.DEFAULT.directBuffer(resBytes.length);
        byteBuf.writeBytes(resBytes);
        DefaultFullHttpRequest request =
                new DefaultFullHttpRequest(HttpVersion.HTTP_1_1, HttpMethod.POST, "/", byteBuf);
        request.headers().add("content-length", resBytes.length);
        client.send(request);
        return future;
    }

    private CompletableFuture<Response> nettyInvoke() {
        String requestId = request.getRequestId();
        CompletableFuture<Response> future = new CompletableFuture<>();
        ResponseMappingCallBack.addCallBack(requestId, future);

        byte[] data = SerializeUtil.obj2Bytes(request);
        Header header = new Header();
        header.setDataLen(data.length);
        byte[] head = SerializeUtil.obj2Bytes(header);


        ByteBuf byteBuf = PooledByteBufAllocator.DEFAULT.directBuffer(head.length + data.length);
        byteBuf.writeBytes(head);
        byteBuf.writeBytes(data);

        client.send(byteBuf);
        return future;
    }
}
```



### 2.4 测试

```java
@Slf4j
public class RpcTest {

    AtomicInteger count = new AtomicInteger(0);

    @Test
    public void initServer() throws IOException {
        RemoteServiceRegistry.registry(UserService.class.getName(), new UserServiceImpl());

        NettyServer nettyServer = new NettyServer(1, 3);
        nettyServer.start(9090);

//        HttpServer httpServer = new HttpServer();
//        httpServer.start(9090);
        System.in.read();
    }

    @Test
    public void cliInvoke() {
        LocalServiceRegistry.registry(UserService.class.getName(), new UserServiceFailImpl());
        int num = 20;
        CountDownLatch downLatch = new CountDownLatch(num);
        AtomicInteger count = new AtomicInteger(0);
        for (int i = 0; i < num; i++) {
            new Thread(
                    () -> {
                        UserService userService = InvokeProxy.proxy(UserService.class);
                        User user = userService.get(22);
                        log.info("第{}次完成调用，结果:{}", count.incrementAndGet(), user.getName());
                        downLatch.countDown();
                    }
            ).start();
        }
        try {
            downLatch.await();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
```

