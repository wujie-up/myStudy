## 1、进程读取磁盘的过程

![](G:\myStudy\img\io\io.png)



## 2、程序运行文件加载

不会加载所有的文件，执行过程中，用到后面的内容，此时pagecache没有，则产生缺页 异常，从磁盘加载。

不同程序读取同一个文件，读取时会有自己的seek偏移量，如果 同时修改文件，则需要持有文件锁。

![image-20210317222345527](G:\myStudy\img\io\io2.png)

### pcstat 查看文件在操作系统中的缓存情况

![在这里插入图片描述](G:\myStudy\img\io\io3.png)



```c
// linux dirty相关系统参数 
vm.dirty_background_ratio = 0           // 文件系统缓存脏页数量达到系统内存百分之多少 触发 flush
vm.dirty_background_bytes = 1048576     // 文件系统缓存脏页数量达到 多大 触发flush
vm.dirty_ratio = 0                      // 文件系统缓存脏页数量达到系统内存百分之多少 系统不得不开始处理缓存脏页， 会造成其他进程阻塞
vm.dirty_bytes = 1048576
vm.dirty_writeback_centisecs = 5000     // 控制周期回写进程的唤醒时间
vm.dirty_expire_centisecs = 30000       // 控制dirty inode实际回写的等待时间
```



## 3、测试pagecache的flush机制

修改上面的dirty参数，这个参数需要添加到 sysctl.conf 中

```cmd
vi /etc/sysctl.conf
```

```c
vm.dirty_background_ratio = 90 // 文件系统缓存脏页数量达到系统内存90% 才触发 flush
vm.dirty_ratio = 90
```

### 3.1 pagecache丢失数据情况

执行OSFileIO的java代码，传参0，然后通过 pcstat 查看文件缓存情况，关闭虚拟机后，重启发现文件没有写入成功。

### 3.2 pagecache自动触发flush

执行OSFileIO的java代码，传参1，这里**BufferIO写入效率更快**，然后通过 pcstat 查看文件缓存情况，等待一段时间后，dirty到达触发flush的阈值。

### 3.3 pagecache淘汰

修改3.2 生成的文件名位 xxoo.txt，并用pcstat xxoo.txt查看 缓存情况

![image-20210317231116412](G:\myStudy\img\io\io5.png)

执行OSFileIO的java代码，重新写文件，然后通过 pcstat out.txt && pcstat xxoo.txt 观察 新老文件 的缓存情况，发现老文件 的**Cached再逐渐减少**。

![image-20210317231047812](G:\myStudy\img\io\io4.png)



## 4、BufferIo为什么更快

执行测试代码，查看进程运行情况文件

### 4.1 基本写

写一行调用一次write()函数

![image-20210317231932779](G:\myStudy\img\io\io7.png)



### 4.2 buffer写

写完一次缓冲区才调用一次write()函数，减少了system write调用次数 ，节省用户态和内核态切换损耗。

![image-20210317231857600](G:\myStudy\img\io\io6.png)



## 5、直接内存和 mmap

![image-20210317232335344](G:\myStudy\img\io\io8.png)

### 测试代码

```java
public class OSFileIO {
    static byte[] data = "123456789\n".getBytes();
    static String path = "/root/testfileio/out.txt";
    
    public static void main(String[] args) throws Exception {
        switch (args[0]) {
            case "0":
                testBasicFileIO();
                break;
            case "1":
                testBufferedFileIO();
                break;
            case "2":
                testRandomAccessFileWrite();
            case "3":
                whatByteBuffer();
            default:
        }
    }

    // 最基本的file写
    public static void testBasicFileIO() throws Exception {
        File file = new File(path);
        FileOutputStream out = new FileOutputStream(file);
        while (true) {
            out.write(data);
        }
    }

    // 测试buffer文件IO 速度更快
    public static void testBufferedFileIO() throws Exception {
        File file = new File(path);
        BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream(file));
        while (true) {
            Thread.sleep(10);
            out.write(data);
        }
    }
    
    //测试文件NIO
    public static void testRandomAccessFileWrite() throws Exception {
        RandomAccessFile raf = new RandomAccessFile(path, "rw");

        raf.write("hello mashibing\n".getBytes());
        raf.write("hello seanzhou\n".getBytes());
        System.out.println("write------------");
        System.in.read();

        raf.seek(4);
        raf.write("ooxx".getBytes());

        System.out.println("seek---------");
        System.in.read();

        FileChannel rafchannel = raf.getChannel();
        //mmap  堆外  和文件映射的   byte  不是对象
        MappedByteBuffer map = rafchannel.map(FileChannel.MapMode.READ_WRITE, 0, 4096);


        map.put("@@@".getBytes());  //不是系统调用  但是数据会到达 内核的pagecache
        //曾经我们是需要out.write()  这样的系统调用，才能让程序的data 进入内核的pagecache
        //曾经必须有用户态内核态切换
        //mmap的内存映射，依然是内核的pagecache体系所约束的！！！
        //换言之，丢数据
        //你可以去github上找一些 其他C程序员写的jni扩展库，使用linux内核的Direct IO
        //直接IO是忽略linux的pagecache
        //是把pagecache  交给了程序自己开辟一个字节数组当作pagecache，动用代码逻辑来维护一致性/dirty。。。一系列复杂问题

        System.out.println("map--put--------");
        System.in.read();

//        map.force(); //  flush
        raf.seek(0);
        ByteBuffer buffer = ByteBuffer.allocate(8192);
//        ByteBuffer buffer = ByteBuffer.allocateDirect(1024);

        int read = rafchannel.read(buffer);   //buffer.put()
        System.out.println(buffer);
        buffer.flip();
        System.out.println(buffer);

        for (int i = 0; i < buffer.limit(); i++) {
            Thread.sleep(200);
            System.out.print(((char) buffer.get(i)));
        }
    }


    public static void whatByteBuffer() {

//        ByteBuffer buffer = ByteBuffer.allocate(1024);
        ByteBuffer buffer = ByteBuffer.allocateDirect(1024);


        System.out.println("postition: " + buffer.position());
        System.out.println("limit: " + buffer.limit());
        System.out.println("capacity: " + buffer.capacity());
        System.out.println("mark: " + buffer);

        buffer.put("123".getBytes());

        System.out.println("-------------put:123......");
        System.out.println("mark: " + buffer);

        buffer.flip();   //读写交替

        System.out.println("-------------flip......");
        System.out.println("mark: " + buffer);

        buffer.get();

        System.out.println("-------------get......");
        System.out.println("mark: " + buffer);

        buffer.compact();

        System.out.println("-------------compact......");
        System.out.println("mark: " + buffer);

        buffer.clear();

        System.out.println("-------------clear......");
        System.out.println("mark: " + buffer);

    }
}
```

