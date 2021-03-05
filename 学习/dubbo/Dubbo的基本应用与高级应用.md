# Dubbo的基本应用与高级应用



官方文档：https://dubbo.apache.org/zh/docs/v2.7/user/

## 负载均衡

在消费端和服务端都配置了负载均衡策略，以消费端为准。

### 负载均衡策略

### Random LoadBalance

- **随机**，按权重设置随机概率。**（默认）**
- 在一个截面上碰撞的概率高，但调用量越大分布越均匀，而且按概率使用权重后也比较均匀，有利于动态调整提供者权重。

### RoundRobin LoadBalance

- **轮询**，按公约后的权重设置轮询比率。
- 存在慢的提供者累积请求的问题，比如：第二台机器很慢，但没挂，当请求调到第二台时就卡在那，久而久之，所有请求都卡在调到第二台上。

### LeastActive LoadBalance

- **最少活跃调用数**，相同活跃数的随机，活跃数指调用前后计数差。

- 使慢的提供者收到更少请求，因为越慢的提供者的调用前后计数差会越大。

  ```txt
  1. 消费者会缓存所调用服务的所有提供者，比如记为p1、p2、p3三个服务提供者，每个提供者内都有一个属性记为active，默认位0
  2. 消费者在调用次服务时，如果负载均衡策略是leastactive
  3. 消费者端会判断缓存的所有服务提供者的active，选择最小的，如果都相同，则随机
  4. 选出某一个服务提供者后，假设位p2，Dubbo就会对p2.active + 1
  5. 然后真正发出请求调用该服务
  6. 消费端收到响应结果后，对p2.active - 1
  7. 这样就完成了对某个服务提供者当前活跃调用数进行了统计，并且并不影响服务调用的性能
  ```

### ConsistentHash LoadBalance[ ](https://dubbo.apache.org/zh/docs/v2.7/user/examples/loadbalance/#consistenthash-loadbalance)

- **一致性 Hash**，相同参数的请求总是发到同一提供者。
- 当某一台提供者挂时，原本发往该提供者的请求，基于虚拟节点，平摊到其它提供者，不会引起剧烈变动。
- 算法参见：http://en.wikipedia.org/wiki/Consistent_hashing
- 缺省只对第一个参数 Hash，如果要修改，请配置 `<dubbo:parameter key="hash.arguments" value="0,1" />`
- 缺省用 160 份虚拟节点，如果要修改，请配置 `<dubbo:parameter key="hash.nodes" value="320" />`

### 参数配置

*服务端服务级别*

```xml
<dubbo:service interface="..." loadbalance="roundrobin" />
```

*服务端方法级别*

```xml
<dubbo:service interface="...">
    <dubbo:method name="..." loadbalance="roundrobin"/>
</dubbo:service>
```

*客户端服务级别*

```xml
<dubbo:reference interface="..." loadbalance="roundrobin" />
```

*客户端方法级别*

```xml
<dubbo:reference interface="...">
    <dubbo:method name="..." loadbalance="roundrobin"/>
</dubbo:reference>
```



## 服务超时

在服务提供者和服务消费者上都可以配置服务超时时间，这两者是不一样的。

消费者调用一个服务，分为三步：

1. 消费者发送请求（网络传输）
2. 服务端执行服务
3. 服务端返回响应（网络传输）

如果在服务端和消费端只在**其中一方**配置了timeout，那么没有歧义，表示消费端**调用** 服务的超时时间**，消费端如果超过时间还没有收到响应结果，则消费端会抛**超时异常**，**但**服务端不会抛异常**，服务端在执行服务后，会检查**执行该服务**的时间，如果超过timeout，则会打印一个**超时日志**。服务会正常的执行完。

如果在服务端和消费端各配了一个timeout，那就比较复杂了，假设

1. 服务执行为5s
2. 消费端timeout=3s
3. 服务端timeout=6s

那么消费端调用服务时，消费端会收到超时异常（因为消费端超时了），服务端一切正常（服务端没有超时）。



## 集群容错

![image-20210228205754404](G:\myStudy\img\dubbo\1.png)

### 集群容错模式

### Failover Cluster 默认

失败自动切换，当出现失败，重试其它服务器。通常用于读操作，但重试会带来更长延迟。可通过 `retries="2"` 来设置重试次数(**不含第一次**)。

```xml
<!-- 服务端服务级别  -->
<dubbo:service retries="2" />
<!-- 客户端服务级别  -->
<dubbo:reference retries="2" />
<!-- 客户端方法级别  -->
<dubbo:reference>
    <dubbo:method name="findFoo" retries="2" />
</dubbo:reference>
```

### Failfast Cluster

快速失败，只发起一次调用，失败立即报错。通常用于非幂等性的写操作，比如新增记录。

### Failsafe Cluster

失败安全，出现异常时，直接忽略。通常用于写入审计日志等操作。

### Failback Cluster

失败自动恢复，后台记录失败请求，定时重发。通常用于消息通知操作。

### Forking Cluster

并行调用多个服务器，只要一个成功即返回。通常用于实时性要求较高的读操作，但需要浪费更多服务资源。可通过 `forks="2"` 来设置最大并行数。

### Broadcast Cluster

广播调用所有提供者，逐个调用，任意一台报错则报错。通常用于通知所有提供者更新缓存或日志等本地资源信息。

现在广播调用中，可以通过 broadcast.fail.percent 配置节点调用失败的比例，当达到这个比例后，BroadcastClusterInvoker 将不再调用其他节点，直接抛出异常。 broadcast.fail.percent 取值在 0～100 范围内。默认情况下当全部调用失败后，才会抛出异常。 broadcast.fail.percent 只是控制的当失败后是否继续调用其他节点，并不改变结果(任意一台报错则报错)。broadcast.fail.percent 参数 在 dubbo2.7.10 及以上版本生效。

````java
@reference(cluster = "broadcast", parameters = {"broadcast.fail.percent", "20"})
````



## 服务降级

官网地址：http://dubbo.apache.org/zh/docs/v2.7/user/examples/service-downgrade/

服务降级表示：服务消费者在调用某个服务提供者时，如果该服务提供者报错了，所采取的措施。

集群容错和服务降级的区别在于：

1. 集群容错是整个集群范围内的容错
2. 服务降级是单个服务提供者的自身容错



## 本地存根

官网地址：http://dubbo.apache.org/zh/docs/v2.7/user/examples/local-stub/

本地存根，名字很抽象，但实际上不难理解，本地存根就是一段逻辑，这段逻辑是在服务消费端执行的，这段逻辑一般都是由服务提供者提供，服务提供者可以利用这种机制在服务消费者远程调用服务提供者之前或之后再做一些其他事情，比如结果缓存，请求参数验证等等。



## 本地伪装

官网地址：http://dubbo.apache.org/zh/docs/v2.7/user/examples/local-mock/

本地伪装就是Mock，Dubbo中Mock的功能相对于本地存根更简单一点，Mock其实就是Dubbo中的服务容错的解决方案。



## 参数回调

官网地址：http://dubbo.apache.org/zh/docs/v2.7/user/examples/callback-parameter/

官网上的Demo其实太复杂，可以看课上的Demo更为简单。

首先，如果当前服务支持参数回调，意思就是：对于某个服务接口中的某个方法，如果想支持消费者在调用这个方法时能设置回调逻辑，那么该方法就需要提供一个入参用来表示回调逻辑。

因为Dubbo协议是基于长连接的，所以消费端在两次调用同一个方法时想指定不同的回调逻辑，那么就需要在调用时在指定一定key进行区分。

**![image.png](G:\myStudy\img\dubbo\2.png)**



## 异步调用

官网地址：http://dubbo.apache.org/zh/docs/v2.7/user/examples/async-call/

理解起来比较容易，主要要理解[CompletableFuture](https://docs.oracle.com/javase/8/docs/api/java/util/concurrent/CompletableFuture.html)，如果不理解，就直接把它理解为Future

其他异步调用方式：https://mp.weixin.qq.com/s/U3eyBUy6HBVy-xRw3LGbRQ



## 泛化调用

官网地址：http://dubbo.apache.org/zh/docs/v2.7/user/examples/generic-reference/

泛化调用可以用来做服务测试。

在Dubbo中，如某个服务想要支持泛化调用，就可以将该服务的generic属性设置为true，那对于服务消费者来说，就可以不用依赖该服务的接口，直接利用GenericService接口来进行服务调用。



## 泛化服务

官网地址：http://dubbo.apache.org/zh/docs/v2.7/user/examples/generic-service/

实现了GenericService接口的就是泛化服务



## Dubbo中的REST

官网地址：http://dubbo.apache.org/zh/docs/v2.7/user/rest/

注意Dubbo的REST也是Dubbo所支持的一种**协议**。

当我们用Dubbo提供了一个服务后，如果消费者没有使用Dubbo也想调用服务，那么这个时候我们就可以让我们的服务支持REST协议，这样消费者就可以通过REST形式调用我们的服务了。

注意：如果某个服务只有REST协议可用，那么该服务必须用@Path注解定义访问路径



## 管理台

github地址：https://github.com/apache/dubbo-admin



## 动态配置

官网地址：http://dubbo.apache.org/zh/docs/v2.7/user/examples/config-rule/

注意动态配置修改的是服务**参数**，并不能修改服务的协议、IP、PORT、VERSION、GROUP，因为这5个信息是服务的标识信息，是服务的身份证号，是不能修改的。



## 服务路由

官网地址：http://dubbo.apache.org/zh/docs/v2.7/user/examples/routing-rule/



### 什么是蓝绿发布、灰度发布

https://zhuanlan.zhihu.com/p/42671353

