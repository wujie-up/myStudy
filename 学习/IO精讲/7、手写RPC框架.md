# 手写Rpc

## 1、框架模型

![](G:\myStudy\img\io\io50.png)

## 2、开工

### 2.1 代理对象大概框架

要像调用本地方法一样调用远程方法，那么我们需要对本地调用的方法进行动态代理。

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
        Object res = null;
        // 1、确定使用的协议
        Protocol protocol = getProtocol(method, clazz);

        return protocol.invoke(clazz, method, args);
    }

    private static <T> Protocol getProtocol(Method method, Class<T> clazz) {
        Protocol protocol = null;
        Rpc anno = method.getAnnotation(Rpc.class);
        if (null == anno) {
            anno = clazz.getAnnotation(Rpc.class);
        } else {
            throw new RuntimeException();
        }

        String pt = anno.protocol();
        if (pt.equals("netty")) {
            protocol = new NettyProtocol();
        } else if ("http".equals(pt)) {
            // todo
            // protocol = new HttpProtocol();
        }
        return protocol;
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



- 1、异常处理：像本地方法调用一样，我们将远程方法调用时抛出的异常封装到结果里面，然后再本地抛出
- 2、服务降级：远程无法调用时，调用本地指定的降级方法。
- 3、远程调用：使用什么调用协议？这里采用Netty，当然也可以用http（tomcat）等。需要封装一个调用协议层。
- 4、本地容器：由于没有集成Spring，提供简单的Map将本地Service注册到其中。
- 5、是否远程调用：方法调用前需要校验是否含有自定义的注解@Rpc。



### 2.2 自定义异常

```java
public class RpcException extends Exception {
    private Exception e;

    public RpcException(Exception e) {
        this.e = e;
    }
    
    public Exception get() {
        return e;
    }
}
```



### 2.3 自定义注解

```java
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.METHOD})
public @interface Rpc {
   /**
     * 默认采用netty协议
     */
    String protocol() default "netty"; 
}
```



### 2.4 远程服务容器 和 本地服务容器

​	因为写在了一个包里面，所以为了区分，写两个容器。

```java
public class LocalServiceRegistry {
    private static ConcurrentHashMap<String, Object> serviceMap = new ConcurrentHashMap<>();

    public static void registry(String serviceName, Object obj) {
        serviceMap.putIfAbsent(serviceName, obj);
    }

    public static Object getService(String serviceName) {
        return serviceMap.get(serviceName);
    }
}
```

```java
public class RemoteServiceRegistry {
    private static ConcurrentHashMap<String, Object> serviceMap = new ConcurrentHashMap<>();

    public static void registry(String serviceName, Object obj) {
        serviceMap.putIfAbsent(serviceName, obj);
    }

    public static Object getService(String serviceName) {
        return serviceMap.get(serviceName);
    }
}
```



### 2.5 调用协议

#### 2.5.1 我们可以抽象出一个协议接口，并且只提供一个invoke方法。

```java
public interface Protocol {
    Object invoke(Class clazz, Method method, Object[] args);
}
```

#### 2.5.2 再提供一个抽象的公共协议类，提供公有的方法

```java
public abstract class CommonProtocol implements Protocol{

    protected abstract Object doInvoke(Request request, Uri uri) throws Exception;

    @Override
    public Object invoke(Class clazz, Method method, Object[] args) throws Exception {
        Uri uri = getRemoteUri(clazz.getName());
        Request request = buildRequest(clazz, method, args);
        return doInvoke(request, uri);
    }

    private Request buildRequest(Class clazz, Method method, Object[] args) {
        Request request =  new Request();
        Content content = Content.builder()
                .serviceName(clazz.getName())
                .methodName(method.getName())
                .paramTypes(method.getParameterTypes())
                .args(args)
                .build();
        request.setRequestId(UUID.randomUUID().toString());
        request.setContent(content);
        return request;
    }

    /**
     *  获取远程服务地址
     * @param serviceName
     * @return
     */
    private Uri getRemoteUri(String serviceName){
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

    private Uri loadBalance(List<Uri> uris) {
        // todo 可以设置多种负载均衡策略，此处随机
        Random r = new Random();
        int index = r.nextInt(uris.size());
        return uris.get(index);
    };

    private List<Uri> getFromRemote() {
        // todo 从注册中心拉取服务
        return new ArrayList<>();
    }
}
```

#### 请求体

```java
@Data
public class Request implements Serializable{
    /**
     * 请求的id
     */
    private String requestId;
    private Content content;
}
```

```java
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Content implements Serializable{
    private String serviceName;
    private String methodName;
    private Class<?>[] paramTypes;
    private Object[] args;
}
```

#### 响应体

```java
@Data
public class Response implements Serializable{
    private String requestId;
    private Object obj;
}
```

#### 服务列表实体

```java
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Uri {
    private String host;
    private int port;
}
```

#### 本地注册表

```java
/**
 * 本地注册表，需要定时从注册中心拉取服务列表
 */
public class RegistryCenter {
    private static ConcurrentHashMap<String, List<Uri>> localServiceUris = new ConcurrentHashMap<>();

    static {
        // 模拟直接注册一个
        localServiceUris.putIfAbsent(UserService.class.getName(), new ArrayList<Uri>(){{
            add(new Uri("127.0.0.1", 9090));
        }});
    }

    public static List<Uri> getServerUris(String serviceName) {
        return localServiceUris.get(serviceName);
    }

    public static void registry(String serviceName, List<Uri> serverUris) {
        localServiceUris.putIfAbsent(serviceName, serverUris);
    }
}
```



#### 2.5.3 netty调用协议实现

```java
public class NettyProtocol extends CommonProtocol {

    @Override
    protected Object doInvoke(Request request, Uri uri) throws Exception {
        final NioSocketChannel channel = NettyClientFactory.getCli(uri);

        String requestId = UUID.randomUUID().toString();
        request.setRequestId(requestId);
        CompletableFuture<Response> future = new CompletableFuture<>();
        ResponseMappingCallBack.addCallBack(requestId, future);

        byte[] bytes = SerializeUtil.obj2Bytes(request);
        int len = bytes.length;
        ByteBuf byteBuf = PooledByteBufAllocator.DEFAULT.directBuffer(4 + bytes.length);
        byteBuf.writeInt(len);
        byteBuf.writeBytes(bytes);

        channel.writeAndFlush(byteBuf);
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



#### 连接工厂

```java
public class NettyClientFactory {
    private static int defaultPoolSize = 5;
    private static ConcurrentHashMap<Uri, NettyCliPool> cliMap = new ConcurrentHashMap<>();

    public static NioSocketChannel getCli(Uri uri) {
        NettyCliPool cliPool = cliMap.get(uri);
        if (null == cliPool) {
            cliPool = initPool(uri);
        }
        Random r = new Random();
        int i = r.nextInt(defaultPoolSize);
        if (null == cliPool.channels[i] || !cliPool.channels[i].isActive()) {
            synchronized (cliPool.lock[i]) {
                if (null == cliPool.channels[i]) {
                    cliPool.channels[i] = createCli(uri);
                }
            }
        }
        return cliPool.channels[i];
    }

    private static NettyCliPool initPool(Uri uri) {
        synchronized (cliMap) {
            if (null == cliMap.get(uri)) {
                NettyCliPool cliPool = new NettyCliPool(defaultPoolSize);
                cliMap.putIfAbsent(uri, cliPool);
            }
        }
        return cliMap.get(uri);
    }

    private static NioSocketChannel createCli(Uri uri) {
        NettyClient client = new NettyClient();
        return client.create(uri);
    }

    private static class NettyCliPool {
        private volatile NioSocketChannel[] channels;
        private Object[] lock;

        public NettyCliPool(int size) {
            channels = new NioSocketChannel[size];
            lock = new Object[size];
            for (int i = 0; i < size; i++) {
                lock[i] = new Object();
            }
        }
    }
}
```

#### netty客户端

```java
public class NettyClient {

    private int defaultGroupSize = 1;

    public NioSocketChannel create(Uri uri) {
        NioEventLoopGroup group = new NioEventLoopGroup(defaultGroupSize);
        Bootstrap bootstrap = new Bootstrap();
        bootstrap.group(group)
                .channel(NioSocketChannel.class)
                .option(ChannelOption.RCVBUF_ALLOCATOR, new FixedRecvByteBufAllocator(65535)) // 解决发送字节不能超过1024问题
                .handler(new ChannelInitializer<NioSocketChannel>() {
                    @Override
                    protected void initChannel(NioSocketChannel channel) throws Exception {
                        ChannelPipeline p = channel.pipeline();
                        p.addLast(new NettyRpcDecoder());
                        p.addLast(new NettyCliHandler());
                    }
                });

        Channel channel = bootstrap.connect(uri.getHost(), uri.getPort()).channel();
        return (NioSocketChannel)channel;
    }
}
```

#### 自定义的解码器和响应处理器

```java
public class NettyRpcDecoder extends MessageToMessageDecoder<ByteBuf> {
    @Override
    protected void decode(ChannelHandlerContext channelHandlerContext, ByteBuf byteBuf, List<Object> list) throws Exception {
        while (byteBuf.readableBytes() >= 4) {
            // 得到数据的长度, 解决粘包拆包问题
            int len = byteBuf.getInt(byteBuf.readerIndex());
            if (byteBuf.readableBytes() >= len) {
                len = byteBuf.readInt();
                byte[] bytes = new byte[len];
                byteBuf.readBytes(bytes);
                Object resp =  SerializeUtil.bytes2Obj(bytes);
                list.add(resp);
            } else {
                break;
            }
        }
    }
}
```

```java
public class NettyCliHandler extends ChannelInboundHandlerAdapter {
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        // 收到服务端响应
        System.out.println("收到服务端响应:" + msg);
        Response response = (Response) msg;
        // 回调，唤醒之前的方法调用线程
        ResponseMappingCallBack.callBack(response);
    }
}
```

#### 响应回调

netty通信，在发起服务点请求后，无法像http一样直接返回，而是通过Client端的handler.read()方法获取到服务端返回数据，所以我们需要一个requstId标记本次请求，并与一个CompletableFuture关联，调用future.get()阻塞当前调用，在Client端的handler收到数据并处理后，根据requstId找到对应的future对象，将结果塞到对应的future里面，之后这里的future就会被唤醒，并返回结果。

```java
public class ResponseMappingCallBack {
    private static ConcurrentHashMap<String, CompletableFuture<Response>> mapping = new ConcurrentHashMap<>();

    public static void addCallBack(String requestId, CompletableFuture<Response> future) {
        mapping.putIfAbsent(requestId, future);
    }

    public static void callBack(String requestId, Response resp) {
        CompletableFuture<Response> future = mapping.get(requestId);
        mapping.remove(requestId);
        future.complete(resp);
    }
}
```

#### 序列化工具类

```java
public class SerializeUtil {

    public static Object bytes2Obj(byte[] bytes) {
        Object obj = null;
        try (ByteArrayInputStream bis = new ByteArrayInputStream(bytes);
             ObjectInputStream ois = new ObjectInputStream(bis)) {
             obj = ois.readObject();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return obj;
    }

    public static Object inputStream2Obj(InputStream is) {
        Object obj = null;
        try (ObjectInputStream ois = new ObjectInputStream(is)) {
            obj = ois.readObject();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return obj;
    }

    public static byte[] obj2Bytes(Object obj) {
        byte[] bytes = null;
        try (ByteArrayOutputStream bos = new ByteArrayOutputStream();
             ObjectOutputStream oos = new ObjectOutputStream(bos)) {
            oos.writeObject(obj);
            bytes = bos.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return bytes;
    }
}
```



### 2.6 服务端NettyServer

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
                .option(ChannelOption.RCVBUF_ALLOCATOR, new FixedRecvByteBufAllocator(65535)) // 解决发送字节不能超过1024问题
                .childHandler(new ChannelInitializer<Channel>() {
                    @Override
                    protected void initChannel(Channel ch) throws Exception {
                        ChannelPipeline p = ch.pipeline();
                        p.addLast(new NettyRpcDecoder());
                        p.addLast(new NettyServerHanlder());
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

```java
public class NettyServerHanlder extends ChannelInboundHandlerAdapter {
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        System.out.println("收到客户端消息: " + msg);

        Request request = (Request) msg;
        Object res = invoke(request.getContent());
        Response resp = new Response();
        resp.setRequestId(request.getRequestId());
        resp.setObj(res);

        byte[] bytes = SerializeUtil.obj2Bytes(resp);
        ByteBuf byteBuf = PooledByteBufAllocator.DEFAULT.directBuffer(4 + bytes.length);
        byteBuf.writeInt(bytes.length);
        byteBuf.writeBytes(bytes);

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



### 2.7 模拟服务

```java
@Rpc(protocol = "netty")
public interface UserService {
    User get(int id);
}
```

#### 2.7.1 远程提供服务

```java
public class UserServiceImpl implements UserService {
    @Override
    public User get(int id) {
        return new User(id, 18, "remoterUser");
    }
}
```

#### 2.7.2 本地降级服务

```java
public class UserServiceFailImpl implements UserService {
    @Override
    public User get(int id) {
        return new User(id, 18, "failUser");
    }
}
```

#### 2.7.3 实体类

```java
@Data
@AllArgsConstructor
@NoArgsConstructor
public class User implements Serializable {
    private int id;
    private int age;
    private String name;
}
```



### 2.8 测试

```java
public class RpcTest {
    @Test
    public void initServer() throws IOException {
        RemoteServiceRegistry.registry(UserService.class.getName(), new UserServiceImpl());

        NettyServer nettyServer = new NettyServer(1, 3);
        nettyServer.start(9090);
    }

    @Test
    public void cliInvoke() {
        LocalServiceRegistry.registry(UserService.class.getName(), new UserServiceFailImpl());

        UserService userService = InvokeProxy.proxy(UserService.class);
        User user = userService.get(22);
        System.out.println(user);
    }
}
```

