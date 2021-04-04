## 1、单线程单多路复用器

```java
public class SingleThreadSingleSelector {

    private static Selector selector;

    public static void main(String[] args) throws Exception {
        // 创建服务端
        ServerSocketChannel server = ServerSocketChannel.open();
        server.configureBlocking(false);
        server.bind(new InetSocketAddress(9090));
        // 多路复用器
        selector = Selector.open();
        // 注册到多路复用器， 监听连接事件
        server.register(selector, SelectionKey.OP_ACCEPT);

        while (true) {
            int num = selector.select();
            if (num > 0) {
                Set<SelectionKey> keys = selector.selectedKeys();
                Iterator<SelectionKey> it = keys.iterator();
                while (it.hasNext()) {
                    SelectionKey key = it.next();
                    it.remove();
                    handleKey(key);
                }
            }
        }
    }

    private static void handleKey(SelectionKey key) throws IOException {
        if (key.isAcceptable()) {
            System.out.println("有客户端连接事件...");
            ServerSocketChannel server = (ServerSocketChannel) key.channel();
            SocketChannel client = server.accept();
            client.configureBlocking(false);
            ByteBuffer byteBuffer = ByteBuffer.allocate(2048);
            client.register(selector, SelectionKey.OP_READ, byteBuffer);
        } else if (key.isReadable()) {
            System.out.println("有客户端读事件...");
            ByteBuffer byteBuffer = (ByteBuffer) key.attachment();
            SocketChannel client = (SocketChannel) key.channel();
            byteBuffer.clear();
            int read = 0;
            while (true) {
                read = client.read(byteBuffer);
                if (read > 0) {
                    byteBuffer.flip();
                    while (byteBuffer.hasRemaining()) {
                        client.write(byteBuffer);
                    }
                } else if (read == 0) {
                    break;
                } else {
                    client.close();
                    break;
                }
            }
        }
    }
}

```

### 缺点：

随着连接的数量增加，线性处理，后面的事件需要等到前面的事件处理完成后才处理，如果有个读事件要处理很长时间，那么后面的事件都要一直等待。



## 2、多线程单多路复用器

```java
public class MutiThreadSingleSelector {

    private static Selector selector;

    public static void main(String[] args) throws Exception {
        ServerSocketChannel server = ServerSocketChannel.open();
        server.configureBlocking(false);
        server.bind(new InetSocketAddress(9090));

        selector = Selector.open();
        server.register(selector, SelectionKey.OP_ACCEPT);

        while (true) {
            int num = selector.select();
            if (num > 0) {
                Set<SelectionKey> selectionKeys = selector.selectedKeys();
                Iterator<SelectionKey> it = selectionKeys.iterator();
                while (it.hasNext()) {
                    SelectionKey key = it.next();
                    it.remove();
                    if (key.isAcceptable()) {
                        accept(key);
                    } else if (key.isReadable()) {
//                        key.cancel();
                        asyncRead(key);
                    }
                }
            }
        }
    }

    private static void asyncRead(SelectionKey key) {
        new Thread(() -> {
            try {
                System.out.println("读事件....");
                ByteBuffer byteBuffer = (ByteBuffer) key.attachment();
                SocketChannel client = (SocketChannel) key.channel();
                byteBuffer.clear();
                int read = 0;
                while (true) {
                    read = client.read(byteBuffer);
                    if (read > 0) {
                        byteBuffer.flip();
                        while (byteBuffer.hasRemaining()) {
                            client.write(byteBuffer);
                        }
                    } else if (read == 0) {
                        break;
                    } else {
                        client.close();
                        break;
                    }
                }
            } catch (IOException e) {

            }
        }).start();
    }

    private static void accept(SelectionKey key) {
        try {
            System.out.println("连接事件....");
            ServerSocketChannel server = (ServerSocketChannel) key.channel();
            SocketChannel client = server.accept();
            client.configureBlocking(false);
            ByteBuffer byteBuffer = ByteBuffer.allocateDirect(2048);
            client.register(selector, SelectionKey.OP_READ, byteBuffer);
        } catch (IOException e) {
        }
    }
}
```



### 缺点：

使用其他线程处理读取，主线程不会等大，继续循环，由于线程执行的时差问题，导致 读取事件key 未被处理完成，主线程会多次读到这个 读事件key，进行了重复的处理。



## 3、多线程多多路复用器多端口

```java

public class MutiThreadMutiSelector {

    private static List<ServerSocketChannel> serverList;
    private static List<Selector> selectorList;
    private static AtomicInteger index = new AtomicInteger(0);

    public static void main(String[] args) throws IOException {
        int num = 3;
        initServers(num);
        register(num);
        start(num);
    }

    private static void start(int num) {
        for (int i = 0; i < num; i++) {
            Selector selector = selectorList.get(i);
            new Thread(() -> doSelect(selector)).start();
        }
    }

    private static void doSelect(Selector selector) {
        while (true) {
            try {
                if (selector.select() > 0) {
                    Set<SelectionKey> keys = selector.selectedKeys();
                    Iterator<SelectionKey> it = keys.iterator();
                    while (it.hasNext()) {
                        SelectionKey key = it.next();
                        it.remove();
                        handle(key);
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private static void handle(SelectionKey key) throws IOException {
        if (key.isAcceptable()) {
            handleAccept(key);
        } else if (key.isReadable()) {
            handleRead(key);
        }
    }

    private static void handleRead(SelectionKey key) throws IOException {
        SocketChannel client = (SocketChannel) key.channel();
        ByteBuffer bf = (ByteBuffer) key.attachment();

        int read = client.read(bf);
        if(read > 0) {
            System.out.println( client.getLocalAddress() + ": " + new String(bf.array()));
            bf.flip();
            while (bf.hasRemaining()) {
                client.write(bf);
            }
            bf.clear();
        } else {
            client.close();
        }
    }

    private static void handleAccept(SelectionKey key) throws IOException {
        ServerSocketChannel server = (ServerSocketChannel) key.channel();
        SocketChannel client = server.accept();
        System.out.println("客户端连接：" + client.getRemoteAddress());
        client.configureBlocking(false);
        Selector selector = key.selector();
        ByteBuffer bf = ByteBuffer.allocate(1024);
        client.register(selector, SelectionKey.OP_READ, bf);
    }

    private static void register(int num) throws IOException {
        selectorList = new ArrayList<>();
        for (int i = 0; i < num; i++) {
            Selector selector = Selector.open();
            ServerSocketChannel server = serverList.get(i);
            server.register(selector, SelectionKey.OP_ACCEPT);
            selectorList.add(selector);
        }
    }

    private static void initServers(int num) throws IOException {
        int initPort = 9090;
        serverList = new ArrayList<>();
        for (int i = 0; i < num; i++) {
            ServerSocketChannel server = ServerSocketChannel.open();
            server.configureBlocking(false);
            server.bind(new InetSocketAddress(initPort++));
            serverList.add(server);
        }
    }
}

```



每个线程都有自己的selector，处理连接和读写事件。



## 4、手写netty

### 4.1 模型设计

![image-20210404204837616](G:\myStudy\img\io\io49.png)



### 4.2 NettyGroup，可以指定group的线程数量

```java
@Data
public class NettyGroup {
    private NettyThread[] threads;
    // 记录work组，是为了在接收到连接后，将SocketChennel传给worker组的线程处理
    private NettyGroup worker;
    // 服务端启动引导类
    private ServerBootStrap bootStrap;

    public NettyGroup(int num) {
        threads = new NettyThread[num];
        for (int i = 0; i < num; i++) {
            threads[i] = new NettyThread(this);
            threads[i].start();
        }
    }
}
```



### 4.3 NettyThread：自定义的Netty处理线程

- handleAccept(SelectionKey key)：只会在BossGroup的线程中会被调用
- handleRead(SelectionKey key)：只会在WorkerGroup的线程中会被调用

```java
@Data
public class NettyThread extends Thread{
    /**
     * 多路复用器
     */
    private Selector selector;
    private NettyGroup group;
    private BlockingQueue<NettyChannel> tasks = new LinkedBlockingQueue<>();
    /**
     * 用来 存储 通信channel 和 我们自定义channel的联系
     */
    ConcurrentHashMap<Channel, NettyChannel> channelMap = new ConcurrentHashMap<>();
    AtomicInteger index = new AtomicInteger(0);

    public NettyThread(NettyGroup group) {
        try {
            // 传入当前组，是为了在接收到连接后，将SocketChannel传给worker线程处理
            this.group = group;
            selector = Selector.open();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        while (true) {
            try {
                if (selector.select() > 0) { // 如果没有事件发生，这里会阻塞
                    Set<SelectionKey> keys = selector.selectedKeys();
                    Iterator<SelectionKey> it = keys.iterator();
                    while (it.hasNext()) {
                        SelectionKey key = it.next();
                        it.remove();
                        if (key.isAcceptable()) {
                            handleAccept(key);
                        } else if (key.isReadable()) {
                            handleRead(key);
                        }
                    }
                }

                processTasks();
            } catch (Exception e) {

            }
        }

    }


    /**
     * 处理 注册 任务
     */
    private void processTasks() throws Exception {
        while (!tasks.isEmpty()) {
            NettyChannel nettyChannel = tasks.take();
            Channel channel = nettyChannel.channel();
            if (channel instanceof ServerSocketChannel) {
                ServerSocketChannel server = (ServerSocketChannel) channel;
                server.register(selector, SelectionKey.OP_ACCEPT);
            } else if (channel instanceof SocketChannel) {
                SocketChannel client = (SocketChannel) channel;
                ByteBuffer byteBuffer = ByteBuffer.allocate(2048);
                client.register(selector, SelectionKey.OP_READ, byteBuffer);
            }
            // 初始化channel的管道
            pipelineInit(nettyChannel);
        }
    }

    private void pipelineInit(NettyChannel channel) {
        channelMap.putIfAbsent(channel.channel(), channel);
        channel.pipeline().init(channel);
    }

    /**
     * 只有worker线程才会执行此方法
     */
    private void handleRead(SelectionKey key) throws IOException {
        ByteBuffer byteBuffer = (ByteBuffer) key.attachment();
        SocketChannel client = (SocketChannel) key.channel();
        byteBuffer.clear();
        int read = client.read(byteBuffer);
        if (read > 0) {
            // todo 读取到数据后，将数据保存到业务层的协议，然后异步处理，当前线程只做IO读取
            channelMap.get(client).pipeline().afterRead(channelMap.get(client), byteBuffer);
            byteBuffer.clear();
        } else if (read < 0) {
            channelMap.get(client).pipeline().close(channelMap.get(client));
        }
    }

    /**
     * 只有boss线程才会执行此方法
     */
    private void handleAccept(SelectionKey key) throws IOException {
        ServerSocketChannel server = (ServerSocketChannel) key.channel();
        SocketChannel client = server.accept();
        client.configureBlocking(false);
        // 拿到服务端的NettyChannel中的pipeline，执行afterAccept方法
        NettyChannel clientChannel = new NettyChannel(client, new Pipeline(group.getBootStrap().getInitChannelHandler()));
        channelMap.get(server).pipeline().afterAccept(clientChannel);
        // 将client 注册到 worker线程组的 selector上
        NettyThread worker = getWorkerThread(group);
        try {
            // 给worker线程添加任务：1、注册client到selector，2、初始化pipeline
            worker.getTasks().put(clientChannel);
            // 唤醒worker线程去处理上面的任务
            worker.getSelector().wakeup();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private NettyThread getWorkerThread(NettyGroup group) {
        int num = index.getAndIncrement();
        int i = num % group.getWorker().getThreads().length;
        return group.getWorker().getThreads()[i];
    }
}
```



### 4.4 NettyChannel：实现对Channel的封装

```java
/**
 * 自己封装的Channel，里面包装了用于通信的Channel 和自定义的pipeline
 */
public class NettyChannel {
    Channel channel;
    Pipeline pipeline;

    public NettyChannel(Channel channel, Pipeline pipeline) {
        this.channel = channel;
        this.pipeline = pipeline;
    }

    public void write(String msg) {
        SocketChannel client = (SocketChannel) channel;
        ByteBuffer bf = ByteBuffer.wrap(msg.getBytes(StandardCharsets.UTF_8));
        try {
            client.write(bf);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String getRemoteAddress() {
        SocketChannel client = (SocketChannel) channel;
        String address = "";
        try {
            address = client.getRemoteAddress().toString();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return address;
    }

    public Pipeline pipeline() {
        return this.pipeline;
    }

    public Channel channel() {
        return this.channel;
    }

    public void close() {
        try {
            channel.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
```



### 4.5 Pipeline：处理数据的管道

```java
public class Pipeline {
    ChannelContext head;
    ChannelContext tail;

    public Pipeline(ChannelHandler handler) {
        head = tail = new ChannelContext(handler);
    }

    public synchronized void addFirst(ChannelHandler channelHandler) {
        ChannelContext context = new ChannelContext(channelHandler);
        if (null == head) {
            head = tail = context;
        } else {
            context.next = head;
            head = context;
        }
    }

    public synchronized void addLast(ChannelHandler channelHandler) {
        ChannelContext context = new ChannelContext(channelHandler);
        if (null == head) {
            head = tail = context;
        } else {
            tail.next = context;
            tail = context;
        }
    }

    /**
     * 在接收到客户端 发送数据 后被调用
     */
    public void afterRead(NettyChannel channel, ByteBuffer msg) {
        ChannelContext context = head;
        while (null != context) {
            context.getChannelHandler().afterRead(channel, msg);
            context = context.next;
        }
    }
    /**
     * 在接收到客户端连接 后被调用
     */
    public void afterAccept(NettyChannel channel) {
        ChannelContext context = head;
        while (null != context) {
            context.getChannelHandler().afterAccept(channel);
            context = context.next;
        }
    }
    /**
     * 在到客户端断开连接 后被调用
     */
    public void close(NettyChannel channel) {
        ChannelContext context = head;
        while (null != context) {
            context.getChannelHandler().close(channel);
            context = context.next;
        }
    }
   /**
     * 在客户端连接时 被调用，进行管道的初始化，主要是 将自定义的ChannelHandler 添加到每个NettyChannel对应的Pipiline中
     */
    public void init(NettyChannel channel) {
        ChannelInitHandler handler = (ChannelInitHandler) head.channelHandler;
        handler.init(channel);
        head = head.next;
    }
}
```



### 4.6 ChannelContext：管道中的每个节点(处理站)

```java
@Data
public class ChannelContext {
    ChannelHandler channelHandler;
    ChannelContext next;

    public ChannelContext(ChannelHandler channelHandler) {
        this.channelHandler = channelHandler;
    }
}
```



### 4.7 ChannelHandler：接口

```java
public interface ChannelHandler {

    void afterAccept(NettyChannel c);

    void afterRead(NettyChannel c, ByteBuffer msg);

    void close(NettyChannel c);
}
```

### ChannelInitHandler：默认用于初始化的ChannelHandler

```java
/**
 * 共享的，用于初始化的 channelHandler，将我们自定义的 handler 添加到pipeline中
 * @Shared 可以像netty 一样标注为共享的
 */
public abstract class ChannelInitHandler implements ChannelHandler{
    public abstract void init(NettyChannel c);

    @Override
    public void afterAccept(NettyChannel c) {
    }

    @Override
    public void afterRead(NettyChannel c, ByteBuffer msg) {
    }

    @Override
    public void close(NettyChannel c) {
    }
}
```

### ChannelDefaultInHandler：默认处理进站事件的ChannelHandler

```java
public abstract class ChannelDefaultInHandler extends ChannelInitHandler {
    @Override
    public void init(NettyChannel c) {
    }

    @Override
    public void afterAccept(NettyChannel c) {
    }

    @Override
    public void afterRead(NettyChannel c, ByteBuffer msg) {
        // 每次clear后pos会重置，数据从头写, 但是原来的数据还是存在bytebuffer的字节数组中
        int position = msg.position();
        byte[] bs = new byte[position];
        int i = 0;
        // 只读取本次发送的有效数据，忽略历史数据
        while (i < position) {
            bs[i] = msg.get(i);
            i++;
        }
        afterRead(c, new String(bs));
    }

    public abstract void afterRead(NettyChannel c, String msg);

    @Override
    public void close(NettyChannel c) {
        c.close();
    }
}
```



### 4.8 服务端启动引导类

```java
public class ServerBootStrap {
    private NettyGroup bossGroup;
    private ChannelHandler initChannelHandler;

    public ServerBootStrap group(NettyGroup bossGroup, NettyGroup workerGroup) {
        this.bossGroup = bossGroup;
        // bossGroup里面保存workGroup的引用，方便后面把 SocketChannel交给 worker线程 进行注册
        this.bossGroup.setWorker(workerGroup);
        // 传递bootStrap是为了在worker线程在 初始化Pipeline时，需要传递 initChannelHandler 过去
        bossGroup.setBootStrap(this);
        workerGroup.setBootStrap(this);
        return this;
    }

    public ChannelHandler getInitChannelHandler() {
        return this.initChannelHandler;
    }

    public ServerBootStrap initHandler(ChannelHandler channelHandler) {
        this.initChannelHandler = channelHandler;
        return this;
    }

    public void bind(InetSocketAddress address) {
        // 绑定到boss线程组
        for (int i = 0; i < bossGroup.getThreads().length; i++) {
            try {
                ServerSocketChannel server = ServerSocketChannel.open();
                server.configureBlocking(false);
                server.bind(address);
                // 给boss线程添加任务：1、注册client到selector，2、初始化pipeline
                NettyThread thread = bossGroup.getThreads()[i];
                thread.getTasks().put(new NettyChannel(server, new Pipeline(initChannelHandler)));
                // 唤醒boss线程处理上面分发的任务
                bossGroup.getThreads()[i].getSelector().wakeup();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
```



### 4.9 自己实现的ChannelHandler

我们只需要继承ChannelDefaultInHandler，就可以对客户端连接，或客户端的消息进行处理。

就像Netty一样，我们只需要关注我们自定义的ChannelHandler来进行业务处理即可。

```java
public class MyHandler extends ChannelDefaultInHandler {
    @Override
    public void afterAccept(NettyChannel c) {
        String remoteAddress = c.getRemoteAddress();
        System.out.println("客户端: " + remoteAddress + "上线了");
    }

    @Override
    public void afterRead(NettyChannel c, String msg) {
        System.out.println("收到客户端消息: " + msg);
        c.write("你好，客户端\n");
    }

    @Override
    public void close(NettyChannel c) {
        System.out.println("客户端:" + c.getRemoteAddress() + "离线了");
        c.close();
    }
}
```



### 4.10 测试代码

```java
public class Main {
    public static void main(String[] args) {
        NettyGroup bossGroup = new NettyGroup(1);
        NettyGroup workerGroup = new NettyGroup(3);
        ServerBootStrap bootStrap = new ServerBootStrap();
        bootStrap.group(bossGroup, workerGroup)
                .initHandler(new ChannelInitHandler() {
                    @Override
                    public void init(NettyChannel c) {
                        // 只需这里可以添加自定义的ChannelHandler即可
                        c.pipeline().addLast(new MyHandler());
                    }
                })
                .bind(new InetSocketAddress(9090));
    }
}
```

