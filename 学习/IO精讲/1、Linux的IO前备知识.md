## 1、VFS(Virtual File System) 虚拟文件系统 

为各类文件系统提供了一个统一的操作界面和应用编程接口。

## 2、FD(File Descriptor) 文件描述符

内核（kernel）利用文件描述符（file descriptor）来访问文件。文件描述符是[非负整数](https://baike.baidu.com/item/非负整数/2951833)。打开现存文件或新建文件时，内核会返回一个文件描述符。读写文件也需要使用文件描述符来指定待读写的文件。

### 2.1 Linux内核维护了3个数据结构：

- 进程级的文件描述符表
- 系统级的打开文件描述符表
- 文件系统的i-node表

一个 Linux 进程启动后，会在内核空间中创建一个 PCB 控制块，PCB 内部有一个**文件描述符表（File descriptor table）**，记录着当前进程所有可用的文件描述符，也即当前进程所有打开的文件。

进程级的描述符表的每一行 记录了单个进程所使用的文件描述符的相关信息，进程之间相互独立，一个进程使用了文件描述符3，另一个进程也可以用3。除了进程级的文件描述符表，系统还需要维护另外两张表：打开文件表、i-node 表。这两张表存储了每个打开文件的打开文件句柄（open file handle）。一个打开文件句柄存储了与一个打开文件相关的全部信息。

**同一个进程的不同文件描述符可以指向同一个文件，不同进程的相等的文件描述符 可能指向不同的文件， 不同的进程见不同的描述符 可能 指向同一个文件**

![Linux文件描述符表示意图](G:\myStudy\img\io\fd.png)



### 2.2 一切皆文件

```txt
文件开头及对应类型如下：
- 普通文件(可执行、图片、文本)
d 目录
b 块设备
l 连接
c 字符设备
s socket
p pipeline
....                                                                                       
```

## 3、PageCache(页缓存)

page cache的大小为一页，通常为4K。在[linux](https://baike.baidu.com/item/linux)读写文件时，它用于[缓存](https://baike.baidu.com/item/缓存/100710)文件的逻辑内容，从而加快对磁盘上映像和数据的访问。

### 读PageCahe

在linux系统中，**两个app打开同一个文件，只会有加载一次文件**，后面的app读取时，会命中pagecache，读到文件内容。

PageCache可以只缓存一个文件部分的内容，不需要把整个文件都缓存进来。

如果读取时，PageCache中没有（或没有加载到），也就是产生**缺页**，会 发出 中断 从磁盘读取。

### 写PageCache

内核发起一个写请求时（例如进程发起write()请求），同样是直接往cache中写入，内核会将被写入的page标记为**dirty**，并将其加入**dirty list**中。内核会周期性地将dirty list中的page写回到磁盘上，从而使磁盘上的数据和内存中缓存的数据一致。

由此，得出没有绝对的数据安全，pagecache中的数据如果没有刷到磁盘中，就会导致数据丢失。mysql和redis(一般每s一次)都是采用手动刷盘(write.flush())。



## 4、 linux文件操作命令分析

### stat   [file]  查看文件元数据信息

​	Inode：可以看作文件的唯一id（索引id）

​    Links 硬连接：即有多少文件名指向这个inode。（类似对象引用，多个引用指向该对象）

```cmd
[root@localhost ~]# stat anaconda-ks.cfg 
  文件："anaconda-ks.cfg"
  大小：1241      	块：8          IO 块：4096   普通文件
设备：fd00h/64768d	Inode：33574978    硬链接：1
权限：(0600/-rw-------)  Uid：(    0/    root)   Gid：(    0/    root)
环境：system_u:object_r:admin_home_t:s0
最近访问：2020-10-30 20:23:37.115646128 +0800
最近更改：2020-10-30 20:23:37.117646224 +0800
最近改动：2020-10-30 20:23:37.117646224 +0800
创建时间：-
```

### lsof -p 进程号 ：查看指定进程打开的文件列表

list open files

```cmd
[root@localhost ~]# lsof -p $$    # $$ 指当前进程pid
COMMAND  PID USER   FD   TYPE DEVICE  SIZE/OFF     NODE NAME
bash    4378 root  cwd    DIR  253,0       194 33574977 /root
bash    4378 root  rtd    DIR  253,0       273       64 /
bash    4378 root  txt    REG  253,0    964536 50332886 /usr/bin/bash
bash    4378 root  mem    REG  253,0 106172832 50332950 /usr/lib/locale/locale-archive
bash    4378 root  mem    REG  253,0     61560   998105 /usr/lib64/libnss_files-2.17.so
bash    4378 root  mem    REG  253,0   2156240    38976 /usr/lib64/libc-2.17.so
bash    4378 root  mem    REG  253,0     19248    38983 /usr/lib64/libdl-2.17.so
bash    4378 root  mem    REG  253,0    174576    44632 /usr/lib64/libtinfo.so.5.9
bash    4378 root  mem    REG  253,0    163312    35088 /usr/lib64/ld-2.17.so
bash    4378 root  mem    REG  253,0     26970 16797896 /usr/lib64/gconv/gconv-modules.cache
bash    4378 root    0u   CHR  136,1       0t0        4 /dev/pts/1   
bash    4378 root    1u   CHR  136,1       0t0        4 /dev/pts/1  
bash    4378 root    2u   CHR  136,1       0t0        4 /dev/pts/1  
bash    4378 root  255u   CHR  136,1       0t0        4 /dev/pts/1
```

```cmd
[root@localhost ~]# exec 8< anaconda-ks.cfg   # 创建描述符 8 、< 输入、 > 输出
[root@localhost ~]# cd /proc/$$/fd            # 查看 fd 对应的设备
[root@localhost ~]# ll
总用量 0
lrwx------. 1 root root 64 3月  17 20:35 0 -> /dev/pts/1
lrwx------. 1 root root 64 3月  17 20:35 1 -> /dev/pts/1
lrwx------. 1 root root 64 3月  17 20:35 2 -> /dev/pts/1
lrwx------. 1 root root 64 3月  17 21:26 255 -> /dev/pts/1
lr-x------. 1 root root 64 3月  17 21:30 8 -> /root/anaconda-ks.cfg
[root@localhost fd]# lsof -op $$
COMMAND  PID USER   FD   TYPE DEVICE OFFSET(偏移量) NODE NAME
# ...
bash    4378 root    0u   CHR  136,1    0t0        4 /dev/pts/1            # 标准输入(所有程序都有)
bash    4378 root    1u   CHR  136,1    0t0        4 /dev/pts/1            # 标准输出(所有程序都有)
bash    4378 root    2u   CHR  136,1    0t0        4 /dev/pts/1            # 标准错误(所有程序都有)
bash    4378 root    8r   REG  253,0    0t0 33574978 /root/anaconda-ks.cfg  # 8文件描述符，r是输入
bash    4378 root  255u   CHR  136,1    0t0        4 /dev/pts/1
```

```cmd
[root@localhost fd]# exec 5<> /dev/tcp/www.baidu.com/80   # 执行 tcp连接 
[root@localhost fd]# ll
总用量 0
lrwx------. 1 root root 64 3月  17 20:35 0 -> /dev/pts/1
lrwx------. 1 root root 64 3月  17 20:35 1 -> /dev/pts/1
lrwx------. 1 root root 64 3月  17 20:35 2 -> /dev/pts/1
lrwx------. 1 root root 64 3月  17 21:26 255 -> /dev/pts/1
lrwx------. 1 root root 64 3月  17 20:35 5 -> socket:[47652]
lr-x------. 1 root root 64 3月  17 21:30 8 -> /root/anaconda-ks.cfg
[root@localhost fd]# lsof -op $$
COMMAND  PID USER   FD   TYPE DEVICE OFFSET     NODE NAME
...
bash    4378 root    0u   CHR  136,1    0t0        4 /dev/pts/1
bash    4378 root    1u   CHR  136,1    0t0        4 /dev/pts/1
bash    4378 root    2u   CHR  136,1    0t0        4 /dev/pts/1
bash    4378 root    5u  IPv4  47652    0t0      TCP localhost.localdomain:41402->14.215.177.39:http (ESTABLISHED)  # socket连接
bash    4378 root    8r   REG  253,0    0t0 33574978 /root/anaconda-ks.cfg
bash    4378 root  255u   CHR  136,1    0t0        4 /dev/pts/1
```

​    

### 管道 |

```cmd
[root@localhost /]# head a.txt   # 查看文本前10行
[root@localhost /]# head -5 a.txt   # 查看文本前5行
[root@localhost /]# tail a.txt   # 查看文本倒数10行
[root@localhost /]# tail -5 a.txt   # 查看文本倒数5行
[root@localhost /]# head -8 a.txt | tail -1   # 查看文本第8行
```

###  strace -ff -o out  [执行程序]  $1

打印程序执行的进程系统调用信息

```cmd
 strace -ff -o out java com/bjmashibing/system/io/OSFileIO $1
```



### 查看文本，快速搜索

```cmd
vi [file]
:set nu
/[搜索的内容]
```

