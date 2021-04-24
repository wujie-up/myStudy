## 1、高可用分析

### 1.1 现有lvs存在的问题

-  **LVS宕机导致服务整体下线， 单点故障**
- **realServer服务宕机引起 部分用户访问不成功**

### 1.2 解决分析

- 单点故障解决：一变多
- 主备：主机挂掉后，备机转正提供服务
  - 主主：多个主机同时提供服务

- server挂掉了，怎么确定？
  - 访问一下，验证的是应用层的http协议，返回的状态码校验 200 -> 正常

#### 主备实现分析：

- 备机怎么知道主机的状态？**方向性**
  - 备机定时查询主机的状态；
  - 主机定期往备机发送心跳；
- 主机宕机后，哪个备机转正？**效率性**
  - 每个备机可以设置好权重，权重大的优先转正
- 主机恢复后，是否要主备切换？**效率性，经济性**
  - 如果主备直接不存在或只有少量的数据拷贝，可以考虑主机恢复时进行切换，并且主机性能可以由于备机性能，备机只在事故时提供短暂服务。
  - 主备切换时，需要从当前主节点同步大量数据，则不考虑进行切换，主备机器的性能要求一致。

#### 实现HA的工具需要具备：

- 监控主备，主机通告自己还活着，备机要监听主机状态，主机挂了，备机们立刻选出新的主机
- 要配置 vip 添加ipvs，有配置文件
- 对下游Server有健康检查，从服务列表中剔除宕机服务，并且还能自动恢复（定期访问宕机节点，返回正常则加入服务列表）



## 2、keepalived实现HA

**keepalive是通用的实现高可用的工具。**

![image-20210424205330449](G:\myStudy\img\distributed\lvs\20.png)



## 3、实操

#### 3.1 清除机器1 之前的配置

```shell
# lvs配置清除
ipvsadm -C 
# lo端口下掉, 后面通过配置文件来实现
ifconfig ens33:2 down
```

#### 3.2 机器4 安装ipvsadm

```SHELL
yum install ipvsadm -y
```

#### 3.3 机器 1、4 安装keepAlived

```shell
yum install keepalived  -y
```

#### 3.4 修改keepalived配置文件

```shell
cd  /etc/keepalived/
cp keepalived.conf keepalived.conf.bak
vi keepalived.conf
```

```shell
global_defs {
   notification_email {
     acassen@firewall.loc
     failover@firewall.loc
     sysadmin@firewall.loc
   }
   notification_email_from Alexandre.Cassen@firewall.loc
   smtp_server 192.168.200.1
   smtp_connect_timeout 30
   router_id LVS_DEVEL
   vrrp_skip_check_adv_addr
   vrrp_strict
   vrrp_garp_interval 0
   vrrp_gna_interval 0
}
# vrrp：虚拟路由冗余协议！
vrrp_instance VI_1 {
    state MASTER  # 备机 BACKUP
    interface ens33
    virtual_router_id 51
    priority 100 # 权重：备机 80 50 30 递减
    advert_int 1
    authentication {
        auth_type PASS
        auth_pass 1111
    }
    virtual_ipaddress {
        192.168.146.100/24 dev ens33 label ens33:2  # vip  可以设置多个
    }
}

# virtual_server 对应lvs配置
virtual_server 192.168.146.100 80 {
    delay_loop 6
    lb_algo rr
    lb_kind DR   # DR模型
    nat_mask 255.255.255.0
    persistence_timeout 0  # 持久化超时（在超时时间内，优先选择上次访问的realServer，避免再次3次握手）
    protocol TCP

    real_server 192.168.146.131 80 {
        weight 1
        HTTP_GET {     # htpp协议 ssl协议（SSL_GET）
            url {      # 健康检查配置
              path /
              status_code 200  # 正常返回状态码
            }
            connect_timeout 3
            nb_get_retry 3
            delay_before_retry 3
        }
    }

    real_server 192.168.146.132 80 {
        weight 1
        HTTP_GET {
            url {
              path /
              status_code 200
            }
            connect_timeout 3
            nb_get_retry 3
            delay_before_retry 3
        }
    }
}
```

#### 3.5 配置备机

```shell
# 拷贝文件 到 目标服务器 的当前路径下
scp  ./keepalived.conf  root@192.168.146.133:`pwd`
```

修改配置

```shell
vrrp_instance VI_1 {
    state BACKUP
    interface ens33
    virtual_router_id 51
    priority 80
    advert_int 1
    authentication {
        auth_type PASS
        auth_pass 1111
    }
    virtual_ipaddress {
        192.168.146.100/24 dev ens33 label ens33:2
    }
}

```

#### 3.6 启动keepalived

```shell
service keepalived start

# 查看ifconfig配置是否生效
ifconfig
```

**主机**

![](G:\myStudy\img\distributed\lvs\22.png) 

![image-20210424224943357](G:\myStudy\img\distributed\lvs\24.png) 

**备机**

![image-20210424214154499](G:\myStudy\img\distributed\lvs\23.png) 

![image-20210424225007651](G:\myStudy\img\distributed\lvs\25.png) 

#### 3.6 无法访问vip问题

```shell
global_defs {
   notification_email {
     acassen@firewall.loc
     failover@firewall.loc
     sysadmin@firewall.loc
   }
   notification_email_from Alexandre.Cassen@firewall.loc
   smtp_server 192.168.200.1
   smtp_connect_timeout 30
   router_id LVS_DEVEL
   vrrp_skip_check_adv_addr
   vrrp_strict     # 这个会导致防火墙对vip产生drop，需要删除
   vrrp_garp_interval 0
   vrrp_gna_interval 0
}
```

```shell
pkill keepalived #彻底关闭keepalived
service keepalived start # 重启
```

#### 3.7 模拟主机下线

```shell
ifconfig ens33 down # down掉主机虚拟网卡主接口
```

#### 查看备机状态

**备机自动转正**

```shell
ifconfig # 查看接口变化，备机vip接口自动配置
ipvsadm -lnc # 查看是否有偷窥行为
```

![image-20210424225144381](G:\myStudy\img\distributed\lvs\26.png) 

#### 主机重新上线

**主机重新上线后，发生了主备切换**

```shell
ifconfig ens33 up
ifconfig # 查看主机接口
```

![image-20210424225445839](G:\myStudy\img\distributed\lvs\27.png) 

```shell
# 查看备机 备机又回到备胎状态
ifconfig
```

![image-20210424225526009](G:\myStudy\img\distributed\lvs\28.png) 

#### 3.8 模拟Server端下线

```shell
service httpd stop # 停掉一台服务的httpd
```

#### 查看lvs机器的状态

```shell
ipvsadm -ln
```

**不可用服务已经被剔除**

![image-20210424225906219](G:\myStudy\img\distributed\lvs\29.png) 

#### 3.9 查看帮助文档

```shell
man 5 keepalived.conf

/ virtual_ipaddress
```

![image-20210424212105917](G:\myStudy\img\distributed\lvs\21.png) 