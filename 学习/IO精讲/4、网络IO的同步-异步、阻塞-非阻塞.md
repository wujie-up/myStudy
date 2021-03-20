## 1、使用命令

```cmd
strace -ff -o out java SocketIO $1   # 打印程序执行的进程系统调用信息
tcpdump -nn -i ens33 port 9090     # 监听9090 端口 tcp的通信信息
lsof -p 进程id   # 查看进程开启文件描述符列表
```

### 

## 2、同步与异步、阻塞与非阻塞

同步：调用线程 接收到连接后， 自己完成 读写操作

异步：通过钩子函数，在连接完成时，由内核调用 钩子函数 进行 读写操作

同步阻塞：线程自己 读取，一直等待有效返回结果

同步非阻塞：线程自己 读取，在调用方法时就会返回是否读到 有效结果，再确定下一次什么时候再读取



## 3、BIO阻塞

```java
public class SocketIO {
    public static void main(String[] args) throws Exception {
        ServerSocket server = new ServerSocket(9090,20);
        System.out.println("step1: new ServerSocket(9090) ");

        while (true) {
            Socket client = server.accept();  //阻塞1
            System.out.println("step2:client\t" + client.getPort());

            new Thread(() -> {
                InputStream in = null;
                try {
                    in = client.getInputStream();
                    BufferedReader reader = new BufferedReader(new InputStreamReader(in));
                    while(true){
                        String dataline = reader.readLine(); //阻塞2

                        if(null != dataline){
                        System.out.println(dataline);
                        }else{
                            client.close();
                            break;
                        }
                    }
                    System.out.println("客户端断开");

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }).start();
        }
    }
}
```

### 2.1  jdk1.4版本编译

```cmd
/usr/java/jdk1.4.2/bin/javac  SocketIO.java
```

### 2.2  BIO阻塞的底层信息

```cmd
strace -ff -o out java SocketIO $1
vi out.进程id
set nu
/accept
```

![](G:\myStudy\img\io\io26.png)

程序阻塞在 **accept()** 调用处，底层的 **accept(3,**   

通过 **lsof -p 进程id** 查看进程文件描述符信息， **accept(3,**  中的 **3** 就是 新开的文件描述符(**socket**)，此时处于**LISTEN** 状态

![image-20210320195239514](G:\myStudy\img\io\io27.png)

### 2.3 与客户端建立连接

```cmd
nc 192.168.146.128 9090
```

查看服务端的进程信息，accept不再阻塞，显示客户端连接信息，并开启了**新的文件描述符** **5** 

![image-20210320200047431](G:\myStudy\img\io\io28.png)

![image-20210320200511270](G:\myStudy\img\io\io29.png)

### 2.4  开启新的线程

代码中使用新的线程对新的连接进行处理，查看进程的日志，可以看到 是 通过**clone()**方法 来开启的新（进程）线程 ，**id为 8447**。

**clone 方法中 传递了 主进程的信息，因此可以共享主线程的部分信息**

最后再次调用accept方法阻塞

![image-20210320200903645](G:\myStudy\img\io\io30.png)

### 2.5 总结

![](G:\myStudy\img\io\io31.png)

### 2.6 BIO的弊端

1、accept阻塞，影响后面的连接请求；

2、多次用户态和内核态的切换，accept、clone

3、创建线程也是比较耗时的，而且线程不能无限制的创建（可以使用线程池，但是read会阻塞，影响后续连接处理）



## 4、NIO非阻塞

```java
public class SocketNIO {
    public static void main(String[] args) throws Exception {
        LinkedList<SocketChannel> clients = new LinkedList<>();

        ServerSocketChannel ss = ServerSocketChannel.open(); 
        ss.bind(new InetSocketAddress(9090));
        ss.configureBlocking(false); //  NONBLOCKING!!!   不阻塞
	    while (true) {
            Thread.sleep(1000);
            // 接受客户端的连接
            SocketChannel client = ss.accept(); //不会阻塞？  -1 NULL
            // accept  调用内核了：1，没有客户端连接进来，返回值？在BIO 的时候一直卡着，但是在NIO ，不卡着，返回-1，NULL
            // 如果来客户端的连接，accept 返回的是这个客户端的fd  5，client  object
            // NONBLOCKING 就是代码能往下走了，只不过有不同的情况
            if (client == null) {
               System.out.println("null ...");
            } else {
                client.configureBlocking(false); // socket（服务端的listen socket<连接请求三次握手后，往这里扔，通过accept 得到  连接的socket>，连接socket<连接后的数据读写使用的> ）
                int port = client.socket().getPort();
                System.out.println("client..port: " + port);
                clients.add(client);
            }

            ByteBuffer buffer = ByteBuffer.allocateDirect(4096);  // 可以使用堆外内存

            // 遍历已经链接进来的客户端能不能读写数据
            for (SocketChannel c : clients) {   //串行化！！！！  多线程！！
                int num = c.read(buffer);  // >0  -1  0   //不会阻塞
                if (num > 0) {
                    buffer.flip();
                    byte[] aaa = new byte[buffer.limit()];
                    buffer.get(aaa);

                    String b = new String(aaa);
                    System.out.println(c.socket().getPort() + " : " + b);
                    buffer.clear();
                }
            }
        }
    }
}
```

### 3.1 阻塞与非阻塞

执行程序代码，查看打印信息

![image-20210320205227894](G:\myStudy\img\io\io33.png)

修改代码，采用阻塞模式

```java
ss.configureBlocking(true); 
```

![image-20210320205351995](G:\myStudy\img\io\io34.png)

### 3.2  非阻塞的系统调用 

```cmd
strace -ff -o out java SocketNIO $      # 打印系统调用日志
```

![image-20210320204858756](G:\myStudy\img\io\io32.png)

内核系统调用**accpet(4** 不再阻塞，而是直接返回了 **- 1**

### 3.3 总结

![image-20210320210121410](G:\myStudy\img\io\io35.png)

### 3.4 NIO的弊端

1、如果客户端连接过多，而每次写的客户端很少，while里面会遍历所有的客户端，试图读取数据，这个是很耗时的。