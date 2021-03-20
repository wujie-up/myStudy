## 1、使用指令

**netstat -natp**  显示网络连接

**lsof -p 进程号** 查看 进程开启文件描述符信息

**netstat -natp**  观察网络连接信息

**tcpdump -nn -i ens33 port 9090**  监听9090 端口 tcp的通信信息

**nc -n 192.168.146.128 9090**  连接远程服务端



## 2、网络IO的重要参数（重要）

```c++
// serverd端socket参数
ReceiveBufferSize ： 接收缓冲区
SoTimeout ： 读数据超时时间（重要）
ReuseAddress ： 是否重用地址
Backlog ： ServerSocket的内核中最大客户端等待连接数（重要）

// client端socket参数
KeepAlive ：是否保存存活（开启后会发送心跳） （重要）
OOBInline ：是否开启试探（优先发送字符试探server是否存活）
ReceiveBufferSize ：接收缓冲区
SendBufferSize ：发送缓冲区
ReuseAddress ：是否重用地址
TcpNoDelay ： 禁用延迟发送（立即发送数据）
SoLinger(bolean on, int linger) ：连接关闭速度
SoTimeout ： 读数据超时时间 （重要）
```



## 3、tcp的连接过程（重要）

```java
public class SocketIOPropertites {
    //server socket listen property:
    private static final int RECEIVE_BUFFER = 10;
    private static final int SO_TIMEOUT = 0;
    private static final boolean REUSE_ADDR = false;
    private static final int BACK_LOG = 2;
    //client socket listen property on server endpoint:
    private static final boolean CLI_KEEPALIVE = false;
    private static final boolean CLI_OOB = false;
    private static final int CLI_REC_BUF = 20;
    private static final boolean CLI_REUSE_ADDR = false;
    private static final int CLI_SEND_BUF = 20;
    private static final boolean CLI_LINGER = true;
    private static final int CLI_LINGER_N = 0;
    private static final int CLI_TIMEOUT = 0;
    private static final boolean CLI_NO_DELAY = false;


    public static void main(String[] args) {

        ServerSocket server = null;
        try {
            server = new ServerSocket();
            server.bind(new InetSocketAddress(9090), BACK_LOG);
            server.setReceiveBufferSize(RECEIVE_BUFFER);
            server.setReuseAddress(REUSE_ADDR);
            server.setSoTimeout(SO_TIMEOUT);

        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("server up use 9090!");
        try {
            while (true) {
                System.in.read();  

                Socket client = server.accept();  
                System.out.println("client port: " + client.getPort());

                client.setKeepAlive(CLI_KEEPALIVE);
                client.setOOBInline(CLI_OOB);
                client.setReceiveBufferSize(CLI_REC_BUF);
                client.setReuseAddress(CLI_REUSE_ADDR);
                client.setSendBufferSize(CLI_SEND_BUF);
                client.setSoLinger(CLI_LINGER, CLI_LINGER_N);
                client.setSoTimeout(CLI_TIMEOUT);
                client.setTcpNoDelay(CLI_NO_DELAY);

                new Thread(
                        () -> {
                            try {
                                InputStream in = client.getInputStream();
                                BufferedReader reader = new BufferedReader(new InputStreamReader(in));
                                char[] data = new char[1024];
                                while (true) {
                                    int num = reader.read(data);
                                    if (num > 0) {
                                        System.out.println("client read some data is :" + num + " val :" + new String(data, 0, num));
                                    } else if (num == 0) {
                                        System.out.println("client readed nothing!");
                                        continue;
                                    } else {
                                        System.out.println("client readed -1...");
                                        System.in.read();
                                        client.close();
                                        break;
                                    }
                                }

                            } catch (IOException e) {
                                e.printStackTrace();
                            }

                        }
                ).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                server.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}

```

1、执行测试代码 并打印 进程信息：

```cmd
strace -ff -o out java SocketIOPropertites 0 $1
```

2、通过抓包命令 监听9090 端口 tcp的通信信息

```cmd
tcpdump -nn -i ens33 port 9090
```

3、通过**netstat -natp**  观察网络连接信息，此时代码执行到**system.in.read()**

![image-20210318212931516](G:\myStudy\img\io\io9.png)

4、通过 **lsof -p 进程号** 查看 进程开启文件描述符信息， 此时 **fd(6)** 处于**LISTEN** 状态

```cmd
COMMAND   PID USER   FD   TYPE             DEVICE  SIZE/OFF     NODE NAME
java    11360 root    0u   CHR              136,1       0t0        4 /dev/pts/1
java    11360 root    1u   CHR              136,1       0t0        4 /dev/pts/1
java    11360 root    2u   CHR              136,1       0t0        4 /dev/pts/1
java    11360 root    3r   REG              253,0  67053209 50551951 /usr/local/jdk1.8.0_271/jre/lib/rt.jar
java    11360 root    4u  unix 0xffff9f2d34e13b80       0t0   164333 socket
java    11360 root    5u  IPv4              47652       0t0      TCP localhost.localdomain:41402->14.215.177.39:http (ESTABLISHED)
java    11360 root    6u  IPv6             164335       0t0      TCP *:websm (LISTEN)
```

5、在另外的客户端执行连接命令

```c++
nc -n 192.168.146.128 9090
```

6、抓包捕捉到3次握手信息

![](G:\myStudy\img\io\io10.png)

7、再查看 网络连接信息

​	可以看出虽然服务端没有调用 accept() 方法，客户端依然可以建立连接，但是**没有分配 进程，存在于内核中。**

![image-20210318220351119](G:\myStudy\img\io\io11.png)

8、继续再客户端发送数据

![image-20210318220909414](G:\myStudy\img\io\io13.png)

9、查看 转包信息 和 网络连接信息

服务端收到信息并且进行ack

网络信息显示：Recv-Q（接收队列）中有9个字节数据（8个数字和一个换行符）

![image-20210318221115357](G:\myStudy\img\io\io14.png)

![image-20210318221207432](G:\myStudy\img\io\io15.png)

10、服务端输入空格，接收到client发送到内核中的数据

![](G:\myStudy\img\io\io16.png)

连接分配了进程**11521/java**

![image-20210318221635608](G:\myStudy\img\io\io17.png)

得到新的文件描述符 **fd(7)**

![image-20210318222023648](G:\myStudy\img\io\io12.png)



## 4、tcp连接测试总结

![image-20210318223150816](G:\myStudy\img\io\io18.png)

![image-20210318234243439](G:\myStudy\img\io\io18-1.png)

## 5、tcp参数测试

### 5.1 BACK_LOG = 2   只允许有 2个作为备用连接。

1、重新编译执行代码

```cmd
java SocketIOPropertites 0
```

2、开启抓包

```cmd
tcpdump -nn -i ens33 port 9090
```

3、先启动3个客户端

![image-20210318224834454](G:\myStudy\img\io\io19.png)

3个客户端都进行了3次握手建立了连接

![image-20210318224918423](G:\myStudy\img\io\io20.png)

4、启动第四个客户端

![image-20210318225123809](G:\myStudy\img\io\io21.png)

接收到连接，状态外SYN_RECV，并没有完成3次握手，连接被拒绝了。



### 5.2 keepalive

通过抓包，可以看到服务端和客户端隔段时间 就会发送空的数据包

![image-20210318225604385](G:\myStudy\img\io\io22.png)



### 5.3 tcp/ip内核数据丢失

1、开启服务端

2、客户端连接后，疯狂发送数据，观察服务端 进程文件描述符

当Recv-Q达到1152后，无法继续增长

![image-20210318230859279](G:\myStudy\img\io\io23.png)

### 5.4 TcpNoDelay  禁用延迟发送

1、将TcpNoDelay设置为true

```java
public class SocketClient {

    public static void main(String[] args) {

        try {
            Socket client = new Socket("192.168.146.128",9090);

            client.setSendBufferSize(20);
            client.setTcpNoDelay(true);
            client.setOOBInline(true);
            OutputStream out = client.getOutputStream();

            InputStream in = System.in;
            BufferedReader reader = new BufferedReader(new InputStreamReader(in));

            while(true){
                String line = reader.readLine();
                if(line != null ){
                    byte[] bb = line.getBytes();
                    for (byte b : bb) {
                        out.write(b);
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
```

2、输入发送数据

```cmd
dsfdsfdsfedsgdfdsfds
gdsfdsfdgdsgfdsgdsf
dsgdsgdsfdsgdsgfdsgds
dgsgdsgdsgdsgdsgdsg
```

3、服务端接收情况

![](G:\myStudy\img\io\io24.png)

4、开启延迟发送后

![](G:\myStudy\img\io\io25.png)