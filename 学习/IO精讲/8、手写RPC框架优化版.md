# 手写Rpc（二）

## 1、框架模型

![](G:\myStudy\img\io\io50.png)

## 2、优化代码

### 2.1 代理类优化，只关心代理对象的创建，将协议的确定步骤  交给 InvokeFactory

```java
@Slf4j
public class InvokeProxy {
    public static <T> T proxy(Class<T> interfaceClazz) {
        Class<?>[] interfaces = {interfaceClazz};

        return (T) Proxy.newProxyInstance(interfaceClazz.getClassLoader(), interfaces,
                (proxy, method, args) -> {
                    Object res;
                    if (needRpc(interfaceClazz, method)) {
                        try {
                            res = remoteInvoke(interfaceClazz, method, args);
                        } catch (Exception e) {
                            log.error("远程调用异常: {}", e);
                            if (e instanceof RpcException) {
                                RpcException rpcEx = (RpcException) e;
                                throw rpcEx.get();
                            } else {
                                // 服务降级
                                res = localInvoke(interfaceClazz, method, args);
                            }
                        }
                    } else {
                        res = localInvoke(interfaceClazz, method, args);
                    }
                    return res;
                });
    }

    private static <T> Object remoteInvoke(Class<T> clazz, Method method, Object[] args) throws Exception {
        Invoker invoker = InvokeFactory.createInvoke(clazz, method, args);
        return invoker.invoke();
    }

    private static <T> Object localInvoke(Class<T> clazz, Method method, Object[] args) {
        // 从本地容器中拿取服务降级的实现
        Object obj = LocalServiceRegistry.getService(clazz.getName());
        Object res = null;
        try {
            res = method.invoke(obj, args);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
        return res;
    }

    private static <T> boolean needRpc(Class<T> clazz, Method method) {
        // 查看方法上是否有 rpc注解
        if (method.isAnnotationPresent(Rpc.class)) {
            return true;
        }
        // 查看类上是否有 rpc注解
        if (clazz.isAnnotationPresent(Rpc.class)) {
            return true;
        }
        return false;
    }
}
```



### 2.2 增加远程调用工厂（简单工厂）

用于生成远程调用对象 Ivoker

```java
public class InvokeFactory {

    private static Map<String, Protocol> protocolMap = new HashMap<>();

    static {
        // 在这里初始化所有的协议
        protocolMap.put("netty", new NettyProtocol());
        // todo protocolMap.put("http", new HttpProtocol());
    }

    public static Invoker createInvoke(Class clazz, Method method, Object[] args) {
        // 1、获取服务地址
        Uri uri = getRemoteUri(clazz.getName());
        // 2、确定使用的协议, 协议只负责 创建连接
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


    /**
     *  获取调用协议
     * @param serviceName
     * @return
     */
    private static <T> Protocol getProtocol(Method method, Class<T> clazz) {
        Protocol protocol = null;
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



### 2.3 调用协议优化

#### 不再提供公共抽象类，感觉没啥用了。协议只关心 连接的创建，返回包装了连接的Invoker对象

```java
public interface Protocol {
    Invoker getInvoker(Uri uri, Request request);
}
```

```java
public class NettyProtocol implements Protocol {

    @Override
    public Invoker getInvoker(Uri uri, Request request) {
        // 使用工厂创建连接客户端
        NettyClient client = NettyClientFactory.createCli(uri);
        return new NettyInvoker(client, request);
    }
}
```



### 2.4、连接工厂优化

考虑到并发问题，将synchronized 锁 换成了 lock锁，这里被调用次数实在太频繁，synchronized 极有可能升级程为 重量级锁（jdk8）。

并且我们打印了一些日志，可以观察锁被获取的次数及获取锁的线程、还有创建连接的次数，当然一个连接肯定是只能初始化一次。

这里再一次连接初始中，有两个重要参数  defaultPoolSize：一个url对应的客户端连接数量。NettyClientGroupSize：netty客户端的group线程数量。

```java
@Slf4j
public class NettyClientFactory {
    private static int defaultPoolSize = 1;
    private static int NettyClientGroupSize = 1;
    
    private static ConcurrentHashMap<String, NettyClient[]> cliMap = new ConcurrentHashMap<>();


    private static Lock lock = new ReentrantLock();

    private static AtomicInteger index = new AtomicInteger(0);

    protected static AtomicInteger lockCount = new AtomicInteger(0);
    protected static AtomicInteger cliCount = new AtomicInteger(0);

    public static NettyClient createCli(Uri uri) {
        String url = uri.getHost() + uri.getPort();
        NettyClient[] clients = cliMap.get(url);

        if (null == clients) {
            try {
                lock.lock();
                log.info("【{}】线程获取了，第【{}】把锁", Thread.currentThread().getName(), lockCount.incrementAndGet());
                if (null == cliMap.get(url)) {
                    log.info("【{}】线程初始化，第【{}】次创建cli", Thread.currentThread().getName(), cliCount.incrementAndGet());
                    clients = new NettyClient[defaultPoolSize];
                    for (int i = 0; i < clients.length; i++) {
                        clients[i] = new NettyClient(NettyClientGroupSize);
                    }
                    cliMap.put(url, clients);
                }
            }  finally {
                lock.unlock();
            }
        }

        int index = NettyClientFactory.index.getAndIncrement();
        NettyClient client = cliMap.get(url)[index % defaultPoolSize];
        if (!client.isAlive()) {
            client.connect(uri);
        }
        return client;
    }
}
```



### 2.5 Invoker 抽象出的调用层（借鉴dubbo）

```java
public interface Invoker {
    Object invoke() throws Exception;
}
```

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

        Response response = future.get();
        Object res = response.getObj();
        if (res instanceof RpcException) {
            RpcException rpcEx = (RpcException) res;
            throw rpcEx;
        }
        return res;
    }
}
```



### 2.6 netty客户端优化

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
                        p.addLast(new NettyRpcDecoder());
                        p.addLast(new NettyCliHandler());
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



### 2.7 解码器修正

这里要注意，上个版本把 ByteToMessageDecoder 搞错了，导致在并发访问的时候会出现反序列化报错问题

```java
public class NettyRpcDecoder extends ByteToMessageDecoder {
	/**
      * 传输协议头大小，是固定值
      */
    private final int HEADER_SIZE = 68;

    @Override
    protected void decode(ChannelHandlerContext channelHandlerContext, ByteBuf byteBuf, List<Object> list) throws Exception {
        while (byteBuf.readableBytes() >= HEADER_SIZE) {
            byte[] bytes = new byte[HEADER_SIZE];
            byteBuf.getBytes(byteBuf.readerIndex(),bytes);
            Header header = (Header) SerializeUtil.bytes2Obj(bytes);

            if (byteBuf.readableBytes() >= header.getDataLen()) {
                byteBuf.readBytes(HEADER_SIZE);
                byte[] data = new byte[header.getDataLen()];
                byteBuf.readBytes(data);
                Object obj = SerializeUtil.bytes2Obj(data);
                list.add(obj);
            } else {
                break;
            }
        }
    }
}
```



### 2.8  传输协议优化

面向对象编程，将 粘包拆包 指定的长度，提到Header内里面，当然这里还可以添加其他的属性

```java
@Data
public class Header implements Serializable {
    int dataLen;
}

```



### 2.9  服务端Handler优化

```java
public class NettyServerHanlder extends ChannelInboundHandlerAdapter {
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        Request request = (Request) msg;
        Object res = invoke(request.getContent());
        Response resp = new Response();
        resp.setRequestId(request.getRequestId());
        resp.setObj(res);

        byte[] data = SerializeUtil.obj2Bytes(resp);

        Header header = new Header();
        header.setDataLen(data.length);
        byte[] head = SerializeUtil.obj2Bytes(header);

        ByteBuf byteBuf = PooledByteBufAllocator.DEFAULT.directBuffer(head.length + data.length);
        byteBuf.writeBytes(head);
        byteBuf.writeBytes(data);

        ctx.channel().writeAndFlush(byteBuf).sync();
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



### 2.10 并发 测试

```java
@Slf4j
public class RpcTest {

    AtomicInteger count = new AtomicInteger(0);

    @Test
    public void initServer() throws IOException {
        RemoteServiceRegistry.registry(UserService.class.getName(), new UserServiceImpl());

        NettyServer nettyServer = new NettyServer(1, 3);
        nettyServer.start(9090);
    }

    @Test
    public void cliInvoke() {
        LocalServiceRegistry.registry(UserService.class.getName(), new UserServiceFailImpl());
        for (int i = 0; i < 20; i++) {
            new Thread(
                    () -> {
                        UserService userService = InvokeProxy.proxy(UserService.class);
                        User user = userService.get(22);
                        log.info("第{}次完成调用， 结果:{}", count.incrementAndGet(), user.getName());
                    }
            ).start();
        }

        try {
            System.in.read();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
```

#### 测试结果

可以看出并发下，lock锁之前的判空毫无意义，所以使用lock替换synchonized还是很有必要的。

```java
【Thread-17】线程获取了，第【1】把锁
【Thread-17】线程初始化，第【1】次创建cli // 只有一次进入创建客户端连接
【Thread-19】线程获取了，第【2】把锁
【Thread-10】线程获取了，第【3】把锁
【Thread-7】线程获取了，第【4】把锁
【Thread-11】线程获取了，第【5】把锁
【Thread-0】线程获取了，第【6】把锁
【Thread-15】线程获取了，第【7】把锁
【Thread-1】线程获取了，第【8】把锁
【Thread-18】线程获取了，第【9】把锁
【Thread-2】线程获取了，第【10】把锁
【Thread-5】线程获取了，第【11】把锁
【Thread-8】线程获取了，第【12】把锁
【Thread-4】线程获取了，第【13】把锁
【Thread-12】线程获取了，第【14】把锁
【Thread-9】线程获取了，第【15】把锁
【Thread-16】线程获取了，第【16】把锁
【Thread-3】线程获取了，第【17】把锁
【Thread-14】线程获取了，第【18】把锁
【Thread-13】线程获取了，第【19】把锁
【Thread-6】线程获取了，第【20】把锁
第1次完成调用，结果:remoterUser
第2次完成调用，结果:remoterUser
第3次完成调用，结果:remoterUser
第4次完成调用，结果:remoterUser
第5次完成调用，结果:remoterUser
第6次完成调用，结果:remoterUser
第7次完成调用，结果:remoterUser
第8次完成调用，结果:remoterUser
第9次完成调用，结果:remoterUser
第10次完成调用，结果:remoterUser
第11次完成调用，结果:remoterUser
第13次完成调用，结果:remoterUser
第14次完成调用，结果:remoterUser
第15次完成调用，结果:remoterUser
第18次完成调用，结果:remoterUser
第20次完成调用，结果:remoterUser
第12次完成调用，结果:remoterUser
第19次完成调用，结果:remoterUser
第17次完成调用，结果:remoterUser
第16次完成调用，结果:remoterUser
```

