



# 1、Nacos简介

Nacos 致力于帮助您发现、配置和管理微服务。Nacos 提供了一组简单易用的特性集，帮助您快速实现动态服务发现、服务配置、服务元数据及流量管理。

Nacos 的关键特性包括:

- 服务发现和服务健康监测
- 动态配置服务
- 动态 DNS 服务
- 服务及其元数据管理

**Nacos与其他主流注册中心对比**

<img src="G:\myStudy\img\microService\springcloudalibaba\nacos\1.png" alt="img" style="zoom: 50%;" /> 



## 1.1 Nacos架构

<img src="G:\myStudy\img\microService\springcloudalibaba\nacos\2.png" alt="img" style="zoom: 67%;" /> 

**NamingService**:  命名服务，注册中心核心接口

**ConfigService**：配置服务，配置中心核心接口

OpenAPI文档：https://nacos.io/zh-cn/docs/open-api.html



## 1.2 NacosServer部署

**下载源码编译**

源码下载地址：https://github.com/alibaba/nacos/  

 ```shell
 cd nacos/
 mvn -Prelease-nacos clean install -U            
 ```

**下载安装包**

下载地址：https://github.com/alibaba/Nacos/releases

### 1.2.1 单机模式

官方文档： https://nacos.io/zh-cn/docs/deployment.html

解压，进入nacos目录，单机启动nacos，执行命令

```shell
bin/startup.sh -m standalone
```

修改默认启动方式，将MODE值改为 "standalone"

```shell
vi bin/startup.sh
```

<img src="G:\myStudy\img\microService\springcloudalibaba\nacos\3.png" style="zoom:67%;" />  

访问nocas的管理端：http://192.168.146.128:8848/nacos ，默认的用户名密码是 nocas/nocas

### 1.2.2 集群模式

官网文档： https://nacos.io/zh-cn/docs/cluster-mode-quick-start.html

集群部署架构图

​    <img src="G:\myStudy\img\microService\springcloudalibaba\nacos\4.png" alt="0" style="zoom:67%;" />

1）准备3台linux虚拟机，分别搭建3个nacos服务

2）以其中一台机器为例，进入nacos目录

2.1）修改conf\application.properties的配置，使用备用的linxu虚拟机作为数据源

     ```properties
      #使用外置mysql数据源 
      spring.datasource.platform=mysql
      ### Count of DB:
      db.num=1 
      ### Connect URL of DB: 
      db.url.0=jdbc:mysql://192.168.2.240:3306/nacoscharacterEncoding=utf8&connectTimeout=1000&socketTimeout=3000&autoReconnect=true&useUnicode=true&useSSL=false&serverTimezone=UTC 
      db.user.0=root 
      db.password.0=root          
     ```

2.2)将conf\cluster.conf.example改为cluster.conf，添加节点配置

 ```properties
 # ip:port  
 192.168.146.128:8848
 192.168.146.129:8848
 192.168.146.134:8848 
 ```

3）在备用虚拟机上，创建mysql数据库，sql文件位置：conf\nacos-mysql.sql

<img src="G:\myStudy\img\microService\springcloudalibaba\nacos\5.png" alt="image-20210529213418306" style="zoom:80%;" /> 

4)  修改启动脚本（bin\startup.sh）的jvm参数

​    <img src="G:\myStudy\img\microService\springcloudalibaba\nacos\6.png" alt="0" style="zoom:67%;" />

5)  分别启动机器1，机器2，机器3

```shell
  bin/startup.sh
```

<img src="G:\myStudy\img\microService\springcloudalibaba\nacos\7.png" alt="0" style="zoom: 80%;" /> 

6) 测试

登录 http://192.168.146.128:8848/nacos  ，用户名和密码都是nacos

​    <img src="G:\myStudy\img\microService\springcloudalibaba\nacos\8.png" alt="0" style="zoom:67%;" /> 

7）官方推荐，nginx反向代理

```xml
http {

	 upstream nacoscluster {
	     server 192.168.146.128:8848;
	     server 192.168.146.129:8848;
	     server 192.168.146.134:8848;
	 }

    server {
        listen 8847;
        server_name localhost;
        
        location /nacos/ {
           proxy_pass http://nacoscluster/nacos/;
        }
    }

}
```

访问： http://localhost:8847/nacos

## 1.3  prometheus+grafana监控Nacos

https://nacos.io/zh-cn/docs/monitor-guide.html

Nacos 0.8.0版本完善了监控系统，支持通过暴露metrics数据接入第三方监控系统监控Nacos运行状态。

1、配置application.properties文件，暴露metrics数据

```
management.endpoints.web.exposure.include=*
```

测试： http://localhost:8848/nacos/actuator/prometheus

​    <img src="G:\myStudy\img\microService\springcloudalibaba\nacos\11.png" alt="0" style="zoom:67%;" /> 

2、prometheus采集Nacos metrics数据

根据官方教程下载prometheus，修改配置

```
scrape_configs:
  # The job name is added as a label `job=<job_name>` to any timeseries scraped from this config.
  - job_name: 'prometheus'

    metrics_path: '/nacos/actuator/prometheus'
    static_configs:
    - targets: ['localhost:8848']
```

启动prometheus服务

```
prometheus.exe --config.file=prometheus.yml 
```

测试：http://localhost:9090/graph

​    <img src="G:\myStudy\img\microService\springcloudalibaba\nacos\12.png" alt="0" style="zoom: 50%;" />

3、 grafana展示metrics数据

安装：https://grafana.com/docs/grafana/latest/installation/windows/

测试： http://localhost:3000/ 

登录：admin/admin

配置prometheus数据源 

<img src="G:\myStudy\img\microService\springcloudalibaba\nacos\26.png" alt="image-20210529231507377" style="zoom:67%;" /> 

​    导入Nacos grafana监控[模版](https://github.com/nacos-group/nacos-template/blob/master/nacos-grafana.json)，在gitee上下载

<img src="G:\myStudy\img\microService\springcloudalibaba\nacos\13.png" alt="0" style="zoom: 50%;" />  

# 2. Nacos注册中心

## 2.1 注册中心演变及其设计思想

​    <img src="G:\myStudy\img\microService\springcloudalibaba\nacos\14.png" alt="0" style="zoom:50%;" /> 

## 2.2 Nacos注册中心架构

​    <img src="G:\myStudy\img\microService\springcloudalibaba\nacos\15.png" alt="0" style="zoom: 67%;" />

## 2.3 核心功能

**服务注册**：Nacos Client会通过发送REST请求的方式向Nacos Server注册自己的服务，提供自身的元数据，比如ip地址、端口等信息。Nacos Server接收到注册请求后，就会把这些元数据信息存储在一个双层的内存Map中。

**服务心跳**：在服务注册后，Nacos Client会维护一个定时心跳来持续通知Nacos Server，说明服务一直处于可用状态，防止被剔除。默认5s发送一次心跳。

**服务同步**：Nacos Server集群之间会互相同步服务实例，用来保证服务信息的一致性。 

**服务发现**：服务消费者（Nacos Client）在调用服务提供者的服务时，会发送一个REST请求给Nacos Server，获取上面注册的服务清单，并且缓存在Nacos Client本地，同时会在Nacos Client本地开启一个定时任务定时拉取服务端最新的注册表信息更新到本地缓存

**服务健康检查**：Nacos Server会开启一个定时任务用来检查注册服务实例的健康情况，对于超过15s没有收到客户端心跳的实例会将它的healthy属性置为false(客户端服务发现时不会发现)，如果某个实例超过30秒没有收到心跳，直接剔除该实例(被剔除的实例如果恢复发送心跳则会重新注册)

## 2.4 服务注册表结构

​    <img src="G:\myStudy\img\microService\springcloudalibaba\nacos\16.png" alt="0" style="zoom:67%;" />

## 2.4 服务领域模型

​    <img src="G:\myStudy\img\microService\springcloudalibaba\nacos\17.png" alt="0" style="zoom:80%;" />

## 2.6 服务实例数据

​    <img src="G:\myStudy\img\microService\springcloudalibaba\nacos\18.png" alt="0" style="zoom:67%;" />

# **3.** Spring Cloud Alibaba Nacos快速开始

## 3.1 Spring Cloud Alibaba版本选型

​    <img src="G:\myStudy\img\microService\springcloudalibaba\nacos\19.png" alt="0" style="zoom:67%;" /> 

​    <img src="G:\myStudy\img\microService\springcloudalibaba\nacos\20.png" alt="0" style="zoom:80%;" />

## 3.2 搭建Nacos-client服务

1）引入依赖

父Pom中支持spring cloud&spring cloud alibaba, 引入依赖

```xml
<dependencyManagement>
    <dependencies>
        <!--引入springcloud的版本-->
        <dependency>
            <groupId>org.springframework.cloud</groupId>
            <artifactId>spring-cloud-dependencies</artifactId>
            <version>Hoxton.SR3</version>
            <type>pom</type>
            <scope>import</scope>
        </dependency>

        <dependency>
            <groupId>com.alibaba.cloud</groupId>
            <artifactId>spring-cloud-alibaba-dependencies</artifactId>
            <version>2.2.1.RELEASE</version>
            <type>pom</type>
            <scope>import</scope>
        </dependency>
    </dependencies>
<dependencyManagement/>
```

当前项目pom中引入依赖

```xml
<dependency>
    <groupId>com.alibaba.cloud</groupId>
    <artifactId>spring-cloud-starter-alibaba-nacos-discovery</artifactId>
<dependency/>
```

​         

2) application.properties中配置

```properties
server.port=8002
#微服务名称
spring.application.name=service-user
#配置 Nacos server 的地址
```

更多配置：https://github.com/alibaba/spring-cloud-alibaba/wiki/Nacos-discovery

​    <img src="G:\myStudy\img\microService\springcloudalibaba\nacos\21.png" alt="0" style="zoom:67%;" /> 

3）启动springboot应用，nacos管理端界面查看是否成功注册

​    <img src="G:\myStudy\img\microService\springcloudalibaba\nacos\22.png" alt="0" style="zoom:67%;" />

4）测试

使用RestTemplate进行服务调用，可以使用微服务名称 （spring.application.name）

```
 String url = "http://service-order/order/findOrderByUserId/"+id;
```

注意：需要添加@LoadBalanced注解

```java
@Bean
@LoadBalanced
public RestTemplate restTemplate() {
    return new RestTemplate();
}
```

​       

# 4、Nacos源码编译

源码下载地址：   https://github.com/alibaba/nacos/

版本： Nacos 1.4.1

**1）启动nacos**

进入console模块，找到启动类 com.alibaba.nacos.Nacos，执行main方法

​    <img src="G:\myStudy\img\microService\springcloudalibaba\nacos\24.png" alt="0" style="zoom:67%;" />

#### 找不到符号`com.alibaba.nacos.consistency.entity`

这个包目录是由`protobuf`在编译时自动生成，您可以通过`mvn compile`来自动生成他们。如果您使用的是IDEA，也可以使用IDEA的protobuf插件。

**2) 配置启动参数** 

单机模式执行需要指定nacos.standalone=true

```
 -Dnacos.standalone=true -Dnacos.home=D:\code\java_yuanma\nacos-1.4.1 
```

创建nacos_config数据库（distribution/conf/nacos-mysql.sql）

在application.properties中开启mysql配置

```properties
### If use MySQL as datasource:
 spring.datasource.platform=mysql

### Count of DB:
db.num=1

### Connect URL of DB:
 db.url.0=jdbc:mysql://127.0.0.1:3306/nacos?characterEncoding=utf8&connectTimeout=1000&socketTimeout=3000&autoReconnect=true&useUnicode=true&useSSL=false&serverTimezone=UTC
 db.user.0=root
db.password.0=root
```

3）进入[http://localhost:8848/nacos](http://localhost:8848/nacos/#/login)，用户名和密码默认nacos

​    <img src="G:\myStudy\img\microService\springcloudalibaba\nacos\25.png" alt="0" style="zoom: 50%;" />