## 1、LVS隐藏ip的办法

### 1.1 修改内核协议

kernel parameter:
目标mac地址为全F，交换机触发广播

```shell
 /proc/sys/net/ipv4/conf/[Iface]/
```

**arp_ignore: 定义接收到ARP请求时的响应级别；**
 0：响应任意网卡上接收到的对本机IP地址的arp请求（包括环回网卡上的地址），而不管该目的IP是否在接收网卡上
 1：只响应目的IP地址 为 接收网卡上的本地地址的arp请求
 2：只响应目的IP地址 为 接收网卡上的本地地址的arp请求，并且arp请求的源IP必须和接收网卡同网段

**arp_announce：定义将自己地址向外通告时的通告级别；**
 0：将本地任何接口上的任何地址向外通告；
 1：仅向目标网络通告与其网络匹配的地址；
 2：仅向与本地接口上地址匹配的网络进行通告；



### 1.2 配置环卫（LO）接口

```shell
ifconfig lo:2 [vip] netmask 255.255.255.255
```



## 2、负载均衡调度策略

### 2.1 静态调度

- 轮询
- 加权轮询
- 随机

### 2.2 动态调度

- lc：最少连接（要实现，那么负载均衡需要具备偷窥功能，且要用表记下访问记录）
- wlc：加权最少连接
- sed：最短期望延迟
- nq：never queue
- lblc：基于本地的最少连接
- dh：
- LBLCR：基于本地的带复制功能的最少连接



## 3、LVS配置

```shell
# ipvs内核模块
yum install ipvsadm -y
```

### 3.1 输入配置

```shell
# 管理集群服务
添加：-A -t|u|f service-address [-s scheduler]
-t: TCP协议的集群
-u: UDP协议的集群
service-address:     IP:PORT
-f: FWM: 防火墙标记
service-address: Mark Number
修改：-E
删除：-D -t|u|f service-address

ipvsadm -A -t 192.168.168.130:80 -s rr
```



### 3.2 输出配置

```shell
# 管理集群服务中的RS
添加：-a -t|u|f service-address -r server-address [-g|i|m] [-w weight]
  -t|u|f service-address：事先定义好的某集群服务
  -r server-address: 某RS的地址，在NAT模型中，可使用IP：PORT实现端口映射；
  [-g|i|m]: LVS类型 
  -g: DR
  -i: TUN
  -m: NAT
  [-w weight]: 定义服务器权重
修改：-e
删除：-d -t|u|f service-address -r server-address
# ipvsadm -a -t 172.16.100.1:80 -r 192.168.10.8 –g
# ipvsadm -a -t 172.16.100.1:80 -r 192.168.10.9 -g
查看
  -L|l
  -n: 数字格式显示主机地址和端口
  --stats：统计数据
  --rate: 速率
  --timeout: 显示tcp、tcpfin和udp的会话超时时长
  -:c 显示当前的ipvs连接状况
删除所有集群服务
  -C：清空ipvs规则
保存规则
  -S
# ipvsadm -S > /path/to/somefile
载入此前的规则：
  -R
# ipvsadm -R < /path/form/somefile 
```



## 4、LVS搭建

![image-20210424171051446](G:\myStudy\img\distributed\lvs\16.png)



### 4.1 配置VIP

选取一台虚拟机作为LVS服务器

```shell
ifconfig ens33:2 192.168.146.100/24  # vip
# 卸载虚拟网卡接口（这里不卸载）
ifconfig ens33:2 down
```



### 4.2 修改RealServer协议

选另外两台虚拟机作为 RealServer

```shell
cd /proc/sys/net/ipv4/conf/
cd ens33
echo 1 > arp_ignore
echo 2 > arp_announce
cd ..
cd all # 修改所有接口
echo 1 > arp_ignore
echo 2 > arp_announce
```

![image-20210424172118790](G:\myStudy\img\distributed\lvs\17.png) 



### 4.3 配置环卫子接口

隐藏VIP设置

```shell
ifconfig lo:2 192.168.146.100 netmask 255.255.255.255
```



### 4.4 在Server端安装httpd

类似于tomat服务

```shell
yum install httpd -y
# 启动
service httpd start
```

创建一个主页

```shell
vi /var/www/html/index.html

from 192.168.146.131/132  # 区分不同的服务端
```

```shell
sudo systemctl status firewalld # 查看防火墙状态 centos7
sudo systemctl stop firewalld # 关闭防火墙
```



### 4.5  LVS配置

机器1安装ipvsadm

```shell
yum install ipvsadm -y
```

配置入口

```shell
ipvsadm -A -t 192.168.146.100:80 -s rr
ipvsadm -ln # 查看配置列表
```

配置出口

```shell
ipvsadm -a -t 192.168.146.100:80 -r 192.168.146.131 -g -w 1
ipvsadm -a -t 192.168.146.100:80 -r 192.168.146.132 -g -w 1
```

![](G:\myStudy\img\distributed\lvs\18.png) 



### 4.6 验证

浏览器访问：192.168.146.100



机器1上查看是没有socket连接

机器2、3上查看有socket连接

```
netstat -natp
```



#### 查看负载均衡端偷窥记录表

```shell
ipvsadm -lnc
```

![image-20210424201206006](G:\myStudy\img\distributed\lvs\19.png) 

state：

- FIN_WAIT 连接完成
- SYN_RECV 基本上lvs都记录了，证明lvs没问题，一定是后面的网络层出问题（解决：请求看能不能访问到）