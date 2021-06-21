## 1.什么是Ribbon

目前主流的负载方案分为以下两种：

- 集中式负载均衡，在消费者和服务提供方中间使用独立的代理方式进行负载，有硬件的（比如 F5），也有软件的（比如 Nginx）。
- 客户端根据自己的请求情况做负载均衡，Ribbon 就属于客户端自己做负载均衡。

Spring Cloud Ribbon是基于Netflix Ribbon 实现的一套客户端的负载均衡工具，Ribbon客户端组件提供一系列的完善的配置，如超时，重试等。通过Load Balancer获取到服务提供的所有机器实例，Ribbon会自动基于某种规则(轮询，随机)去调用这些服务。Ribbon也可以实现我们自己的负载均衡算法。

### 1.1 客户端的负载均衡

例如spring cloud中的ribbon，客户端会有一个服务器地址列表，在发送请求前通过负载均衡算法选择一个服务器，然后进行访问，这是客户端负载均衡；即在客户端就进行负载均衡算法分配。

​    <img src="G:\myStudy\img\microService\springcloudalibaba\ribbon\1.png" alt="0" style="zoom:67%;" />

### 1.2 服务端的负载均衡

例如Nginx，通过Nginx进行负载均衡，先发送请求，然后通过负载均衡算法，在多个服务器之间选择一个进行访问；即在服务器端再进行负载均衡算法分配。

​    <img src="G:\myStudy\img\microService\springcloudalibaba\ribbon\2.png" alt="0" style="zoom:67%;" />

### 1.3  常见负载均衡算法

- 随机，通过随机选择服务进行执行，一般这种方式使用较少;
- 轮训，负载均衡默认实现方式，请求来之后排队处理;
- 加权轮训，通过对服务器性能的分型，给高配置，低负载的服务器分配更高的权重，均衡各个服务器的压力;
- 地址Hash，通过客户端请求的地址的HASH值取模映射进行服务器调度。  ip hash
- 最小链接数，即使请求均衡了，压力不一定会均衡，最小连接数法就是根据服务器的情况，比如请求积压数等参数，将请求分配到当前压力最小的服务器上。  最小活跃数

### 1.4 Ribbon模块

| 名 称               | 说  明                                                       |
| ------------------- | ------------------------------------------------------------ |
| ribbon-loadbalancer | 负载均衡模块，可独立使用，也可以和别的模块一起使用。         |
| Ribbon              | 内置的负载均衡算法都实现在其中。                             |
| ribbon-eureka       | 基于 Eureka 封装的模块，能够快速、方便地集成 Eureka。        |
| ribbon-transport    | 基于 Netty 实现多协议的支持，比如 HTTP、Tcp、Udp 等。        |
| ribbon-httpclient   | 基于 Apache HttpClient 封装的 REST 客户端，集成了负载均衡模块，可以直接在项目中使用来调用接口。 |
| ribbon-example      | Ribbon 使用代码示例，通过这些示例能够让你的学习事半功倍。    |
| ribbon-core         | 一些比较核心且具有通用性的代码，客户端 API 的一些配置和其他 API 的定义。 |



## 2、自己实现客户端调用负载均衡

我们在远程调用时使用的是Retemplate组件，Retemplate组件源码中不管get还post都会调用**doExecute()**方法，这里可以继承RestTemplate ，重写doExecute来实现负载均衡。

```java
@Slf4j
public class CustomRestTemplate extends RestTemplate {

    private DiscoveryClient discoveryClient;

    public CustomRestTemplate(DiscoveryClient discoveryClient) {
        this.discoveryClient = discoveryClient;
    }

    @Override
    protected <T> T doExecute(URI url, HttpMethod method,
                              RequestCallback requestCallback,
                              ResponseExtractor<T> responseExtractor) throws RestClientException {
        Assert.notNull(url, "URI is required");
        Assert.notNull(method, "HttpMethod is required");
        ClientHttpResponse response = null;
        log.info("请求的url路径是:{}", url);
        // 把url替换成我们的ip
        url = replaceUrl(url);
        log.info("替换后的url路径是:{}", url);
        try {
            ClientHttpRequest request = createRequest(url, method);
            if (null != requestCallback) {
                requestCallback.doWithRequest(request);
            }
            response = request.execute();
            handleResponse(url, method, response);
            return (responseExtractor != null ? responseExtractor.extractData(response) : null);
        } catch (IOException ex) {
            String resource = url.toString();
            String query = url.getRawQuery();
            resource = (query != null ? resource.substring(0, resource.indexOf('?')) : resource);
            throw new ResourceAccessException("I/O error on " + method.name() +
                    " request for \"" + resource + "\": " + ex.getMessage(), ex);
        } finally {
            if (response != null) {
                response.close();
            }
        }
    }

    /**
     * 方法实现说明:把微服务名称  去注册中心拉取对应IP进行调用
     *
     * @Param: url
     * @return:
     * @Date: 2020/2/21 1:21
     * @Author: WUJIE
     **/
    private URI replaceUrl(URI url) {
        // 1:从URI中解析调用的调用的serviceName=provider
        String serviceName = url.getHost();
        log.info("调用的微服务名称：{}", serviceName);
        // 2:解析我们的请求路径 reqPath= /buy/1
        String path = url.getPath();
        log.info("请求path：{}", path);
        // 通过微服务的名称去nacos服务端获取 对应的实例列表
        List<ServiceInstance> serviceInstanceList = discoveryClient.getInstances(serviceName);
        if (serviceInstanceList.isEmpty()) {
            throw new RuntimeException("没有可用的微服务实例: " + serviceName);
        }
        String serviceIp = chooseService(serviceInstanceList);
        String source = serviceIp + path;
        try {
            return new URI(source);
        } catch (URISyntaxException e) {
            log.error("构建的URI异常：" + source);
        }
        return url;
    }

    /**
     * 选择一个微服务实例进行调用：采用随机
     *
     * @Param: serviceInstanceList
     * @return:
     * @Date: 2020/2/21 1:31
     * @Author: WUJIE
     */
    private String chooseService(List<ServiceInstance> serviceInstanceList) {
        Random random = new Random();
        int i = random.nextInt(serviceInstanceList.size());
        return serviceInstanceList.get(i).getUri().toString();
    }
}
```

添加配置类

```java
@Configuration
public class WebConfig {
    @Bean
    RestTemplate restTemplate(DiscoveryClient client){
        return new CustomRestTemplate(client);
    }
}
```

客户端调用代码

```java
@RestController
@RequestMapping
public class OrderController {

    @Autowired
    RestTemplate restTemplate;

    @GetMapping(value = "/buy/{skuId}")
    public Object buy(@PathVariable String skuId){
        ResponseEntity<String> responseEntity= restTemplate.getForEntity("http://provider/getSkuById/"+skuId, String.class);
        return responseEntity.getBody();
    }
}
```



## 3、Ribbon组件实现负载均衡

编写一个客户端来调用接口

 ```java
 public class RibbonDemo {
     public static void main(String[] args) {
         // 服务列表
         List<Server> serverList = Lists.newArrayList(
                 new Server("localhost", 8020),
                 new Server("localhost", 8021));
         // 构建负载实例
         ILoadBalancer loadBalancer = LoadBalancerBuilder.newBuilder()
                 .buildFixedServerListLoadBalancer(serverList);
         // 调用 5 次来测试效果
         for (int i = 0; i < 5; i++) {
             String result = LoadBalancerCommand.<String>builder()
                     .withLoadBalancer(loadBalancer).build()
                     .submit(new ServerOperation<String>() {
                         @Override
                         public Observable<String> call(Server server) {
                             String addr = "http://" + server.getHost() + ":" +
                                     server.getPort() + "/order/findOrderByUserId/1";
                                 System.out.println(" 调用地址：" + addr);
                             URL url = null;
                             try {
                                 url = new URL(addr);
                                 HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                                 conn.setRequestMethod("GET");
                                 conn.connect();
                                 InputStream in = conn.getInputStream();
                                 byte[] data = new byte[in.available()];
                                 in.read(data);
                                 return Observable.just(new String(data));
                             } catch (Exception e) {
                                 e.printStackTrace();
                             }
                             return null;
                         }
                     }).toBlocking().first();
 
             System.out.println(" 调用结果：" + result);
         }
     }
 }
 ```

上述这个例子主要演示了 Ribbon 如何去做负载操作，调用接口用的最底层的 HttpURLConnection。



## 4. Spring Cloud快速整合Ribbon

**1) 引入依赖**

如果使用nacos作为注册中心，nacos-discovery依赖了ribbon，可以不用再引入ribbon依赖

```xml
<dependency>
	<groupId>org.springframework.cloud</groupId>
	<artifactId>spring-cloud-starter-netflix-ribbon</artifactId>
</dependency>
```

**2) 添加@LoadBalanced注解**

```java
@Configuration
public class RestConfig {
     @Bean
     @LoadBalanced
     public RestTemplate restTemplate() {
         return new RestTemplate();
} 
```

**3) 客户端调用代码**

```java
@Autowired
private RestTemplate restTemplate;

@RequestMapping(value = "/findOrderByUserId/{id}")
public R  findOrderByUserId(@PathVariable("id") Integer id) {
    // RestTemplate调用
    //String url = "http://localhost:8020/order/findOrderByUserId/"+id;
    //模拟ribbon实现
    //String url = getUri("mall-order")+"/order/findOrderByUserId/"+id;
    // 添加@LoadBalanced
    String url = "http://mall-order/order/findOrderByUserId/"+id;
    R result = restTemplate.getForObject(url,R.class);

    return result;
}  
```



## 5、Ribbon内核原理

### 5.1 Ribbon原理

​    <img src="G:\myStudy\img\microService\springcloudalibaba\ribbon\3.png" alt="0" style="zoom:67%;" /> 

**5.1.1 模拟ribbon实现**

```java
@Autowired
private RestTemplate restTemplate;

@RequestMapping(value = "/findOrderByUserId/{id}")
public R  findOrderByUserId(@PathVariable("id") Integer id) {
    // RestTemplate调用
    //String url = "http://localhost:8020/order/findOrderByUserId/"+id;
    //模拟ribbon实现
    String url = getUri("mall-order")+"/order/findOrderByUserId/"+id;
    // 添加@LoadBalanced
    //String url = "http://mall-order/order/findOrderByUserId/"+id;
    R result = restTemplate.getForObject(url,R.class);
    return result;
}

@Autowired
private DiscoveryClient discoveryClient;
public String getUri(String serviceName) {
    List<ServiceInstance> serviceInstances = discoveryClient.getInstances(serviceName);
    if (serviceInstances == null || serviceInstances.isEmpty()) {
        return null;
    }
    int serviceSize = serviceInstances.size();
    //轮询
    int indexServer = incrementAndGetModulo(serviceSize);
    return serviceInstances.get(indexServer).getUri().toString();
}
private AtomicInteger nextIndex = new AtomicInteger(0);
private int incrementAndGetModulo(int modulo) {
    for (;;) {
        int current = nextIndex.get();
        int next = (current + 1) % modulo;
        if (nextIndex.compareAndSet(current, next) && current < modulo){
            return current;
        }
    }
}
```



**5.1.2** **@LoadBalanced 注解原理**

参考源码： LoadBalancerAutoConfiguration

**@LoadBalanced**利用**@Qualifier注解**作为restTemplates注入的筛选条件，筛选出具有负载均衡标识的RestTemplate。

<img src="G:\myStudy\img\microService\springcloudalibaba\ribbon\4.png" alt="image-20210530222306566" style="zoom:80%;" /> 

被@LoadBalanced注解的restTemplate会被定制，添加LoadBalancerInterceptor拦截器。

  <img src="G:\myStudy\img\microService\springcloudalibaba\ribbon\5.png" alt="image-20210530222347573" style="zoom:80%;" />

**5.1.3 Ribbon相关接口**

参考： org.springframework.cloud.netflix.ribbon.RibbonClientConfiguration

**IClientConfig**：Ribbon的客户端配置，默认采用**DefaultClientConfigImpl**实现。

**IRule**：Ribbon的负载均衡策略，默认采用**ZoneAvoidanceRule**实现，该策略能够在多区域环境下选出最佳区域的实例进行访问。

**IPing**：Ribbon的实例检查策略，默认采用**DummyPing**实现，该检查策略是一个特殊的实现，实际上它并不会检查实例是否可用，而是始终返回true，默认认为所有服务实例都是可用的。

**ServerList**：服务实例清单的维护机制，默认采用**ConfigurationBasedServerList**实现。

**ServerListFilter**：服务实例清单过滤机制，默认采**ZonePreferenceServerListFilter**，该策略能够优先过滤出与请求方处于同区域的服务实例。   

**ILoadBalancer**：负载均衡器，默认采用**ZoneAwareLoadBalancer**实现，它具备了区域感知的能力。

​    <img src="G:\myStudy\img\microService\springcloudalibaba\ribbon\6.png" alt="0" style="zoom:80%;" /> 

**5.2 Ribbon负载均衡策略**

​    <img src="G:\myStudy\img\microService\springcloudalibaba\ribbon\7.png" alt="0" style="zoom: 67%;" />

1. **RandomRule**： 随机选择一个Server。
2. **RetryRule**： 对选定的负载均衡策略机上重试机制，在一个配置时间段内当选择Server不成功，则一直尝试使用subRule的方式选择一个可用的server。
3. **RoundRobinRule**： 轮询选择， 轮询index，选择index对应位置的Server。
4. **AvailabilityFilteringRule**： 过滤掉一直连接失败的被标记为circuit tripped的后端Server，并过滤掉那些高并发的后端Server或者使用一个AvailabilityPredicate来包含过滤server的逻辑，其实就是检查status里记录的各个Server的运行状态。
5. **BestAvailableRule**： 选择一个最小的并发请求的Server，逐个考察Server，如果Server被tripped了，则跳过。
6. **WeightedResponseTimeRule**： 根据响应时间加权，响应时间越长，权重越小，被选中的可能性越低。
7. **ZoneAvoidanceRule**： 默认的负载均衡策略，即复合判断Server所在区域的性能和Server的可用性选择Server，在没有区域的环境下，类似于轮询(RandomRule)
8. **NacosRule:**  同集群优先调用



## 6、Ribbon的配置详解

### 6.1 修改默认负载均衡策略

**全局配置：**调用其他微服务，一律使用指定的负载均衡算法

```java
@Configuration
public class RibbonConfig {
    /**
     * 全局配置
     */
    @Bean
    public IRule() {
        // 指定使用Nacos提供的负载均衡策略（优先调用同一集群的实例，基于随机权重）
        return new NacosRule();
    }   
```

**局部配置**：调用指定微服务提供的服务时，使用对应的负载均衡算法

**1) 配置类方式**

注意：自定义配置类不能添加@Configuration注解，否则会成为全局的配置

```java
/**
 * 支付服务的负载均衡策略配置
 */
public class PayRibbonLBConfig {
    @Bean
    public IRule roundRobinRule(){
        return new RoundRobinRule();
    }
}

/**
 * 商品服务的负载均衡策略配置
 */
public class ProductRibbonLBConfig {
    @Bean
    public IRule randomRule(){
        return new RandomRule();
    }
}
```

```java
/**
 * 不同服务策略配置
 */
@Configuration
@RibbonClients(value = {
        @RibbonClient(name = "product", configuration = ProductRibbonLBConfig.class),
        @RibbonClient(name = "pay", configuration = PayRibbonLBConfig.class)
})
public class CustomRibbonConfig {
}
```

**2）配置文件方式**

修改application.yml

   ```yaml
   # 不同服务的负载均衡策略配置
   product: # 被调用的微服务名
     ribbon:
       NFLoadBalancerRuleClassName: com.netflix.loadbalancer.RandomRule
   pay: # 被调用的微服务名
     ribbon:
       NFLoadBalancerRuleClassName: com.netflix.loadbalancer.RoundRobinRule
   ```

​       

### 6.2 饥饿加载

在进行服务调用的时候，如果网络情况不好，第一次调用会超时。

Ribbon默认懒加载，意味着只有在发起调用的时候才会创建客户端。

​    <img src="G:\myStudy\img\microService\springcloudalibaba\ribbon\8.png" alt="0" style="zoom: 50%;" /> 

源码对应属性配置类：**RibbonEagerLoadProperties**

```yaml
ribbon:
  eager-load:
    # 开启ribbon饥饿加载
    enabled: true
    # 配置mall-user使用ribbon饥饿加载，多个使用逗号分隔
    clients: mall-order
```



## 7  自定义负载均衡策略

通过实现 IRule 接口可以自定义负载策略，主要的选择服务逻辑在 choose 方法中。

### 7.1 实现基于Nacos权重的负载均衡策略

```java
@Slf4j
public class NacosRandomWithWeightRule extends AbstractLoadBalancerRule {

    @Autowired
    private NacosDiscoveryProperties nacosDiscoveryProperties;

    @Override
    public Server choose(Object key) {
        DynamicServerListLoadBalancer loadBalancer = (DynamicServerListLoadBalancer) getLoadBalancer();
        String serviceName = loadBalancer.getName();
        NamingService namingService = nacosDiscoveryProperties.namingServiceInstance();
        try {
            //nacos基于权重的算法
            Instance instance = namingService.selectOneHealthyInstance(serviceName);
            return new NacosServer(instance);
        } catch (NacosException e) {
            log.error("获取服务实例异常：{}", e.getMessage());
        }
        return null;
    }
    
    @Override
    public void initWithNiwsConfig(IClientConfig clientConfig) {
		//读取配置文件并且初始化,ribbon内部的 几乎用不上
    }
```



**配置自定义的策略**

**1）局部配置：** 

修改application.yml

```yaml
# 被调用的微服务名
mall-order:
  ribbon:
    # 自定义的负载均衡策略（基于随机&权重）
    NFLoadBalancerRuleClassName: com.tuling.mall.ribbondemo.rule.NacosRandomWithWeightRule
```

**2）全局配置**

```java
@Bean
public IRule ribbonRule() {
	return new NacosRandomWithWeightRule();
}
```

**3）局部配置第二种方式**

可以利用@RibbonClient指定微服务及其负载均衡策略。

```java
@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class,
        DruidDataSourceAutoConfigure.class})
//@RibbonClient(name = "mall-order",configuration = RibbonConfig.class)
@RibbonClients(value = {
    @RibbonClient(name = "mall-order",configuration = RibbonConfig.class),
    @RibbonClient(name = "mall-account",configuration = RibbonConfig.class)    
})
public class MallUserRibbonDemoApplication {

    public static void main(String[] args) {
        SpringApplication.run(MallUserRibbonDemoApplication.class, args);
    }
}
```

```java
public class RibbonConfig {
    @Bean
    public IRule ribbonRule() {
        return new NacosRandomWithWeightRule();
    }
}
```



### 7.2 同集群优先权重负载均衡算法

<img src="G:\myStudy\img\microService\springcloudalibaba\ribbon\9.png" alt="img" style="zoom:67%;" /> 

**7.2.1 自定义策略**

```java
@Slf4j
public class TheSameClusterRule extends AbstractLoadBalancerRule {

    @Autowired
    private NacosDiscoveryProperties discoveryProperties;

    @Override
    public void initWithNiwsConfig(IClientConfig iClientConfig) {

    }

    @Override
    public Server choose(Object o) {
        try {
            // 1、获取当前服务所在集群
            String currentClusterName = discoveryProperties.getClusterName();
            // 2、获取负载均衡对象
            BaseLoadBalancer loadBalancer = (BaseLoadBalancer) this.getLoadBalancer();
            // 3、获取当前调用的微服务名称
            String serviceName = loadBalancer.getName();
            // 4、获取nacos clinet的服务注册发现组件的api
            NamingService namingService = discoveryProperties.namingServiceInstance();
            // 5、获取所有的服务实例
            List<Instance> allInstanceList = namingService.getAllInstances(serviceName);
            // 6、找到当前集群的所有服务实例
            List<Instance> currentClusterInstList = new ArrayList<>();
            allInstanceList.forEach(a -> {
                if (a.getClusterName().equalsIgnoreCase(currentClusterName)) {
                    currentClusterInstList.add(a);
                }
            });
            // 7、通过负载均衡策略得到最终调用的实例
            Instance beChoosedInstance;
            if (currentClusterInstList.isEmpty()) {
                // 跨集群随机权重
                beChoosedInstance = WeightBalacer.chooseHostByRandomWeight(allInstanceList);
                log.info("发生跨集群调用--->" +
                                "当前微服务所在集群:{},被调用微服务所在集群:{},Host:{},Port:{}",
                        currentClusterName,beChoosedInstance.getClusterName(),
                        beChoosedInstance.getIp(),beChoosedInstance.getPort());
            } else {
                // 同集群权随机权重
                beChoosedInstance = WeightBalacer.chooseHostByRandomWeight(currentClusterInstList);
                log.info("同集群调用--->" +
                                "当前微服务所在集群:{},被调用微服务所在集群:{},Host:{},Port:{}",
                        currentClusterName,beChoosedInstance.getClusterName(),
                        beChoosedInstance.getIp(),beChoosedInstance.getPort());
            }
            return new NacosServer(beChoosedInstance);
        } catch (NacosException e) {}
        return null;
    }
}
```

```java
public class WeightBalancer extends Balancer {
    public static Instance chooseHostByRandomWeight(List<Instance> hosts) {
        return getHostByRandomWeight(hosts);
    }
}
```

**7.2.2 策略的调用配置**

```java
public class GlobalRibbonLBConfig {
    @Bean
    public IRule customRule(){
//        return new WeightRule(); 基于权重
        return new TheSameClusterRule(); // 同集群优先随机权重
    }
}
```

**7.2.3 配置文件指定集群名称**

```yaml
server:
  port: 8070
spring:
  cloud:
    nacos:
      discovery:
        server-addr: localhost:8848
        cluster-name: NJ-CLUSTER
  application:
    name: consumer
```



### 7.3 同版本优先调用同集群服务

**7.3.1 自定义策略**

```java
@Slf4j
public class TheSameClusterRuleWithVersion extends AbstractLoadBalancerRule {

    @Autowired
    private NacosDiscoveryProperties discoveryProperties;

    @Override
    public void initWithNiwsConfig(IClientConfig iClientConfig) {

    }

    @Override
    public Server choose(Object o) {
        try {
            // 1、获取当前服务集群
            String currentClusterName = discoveryProperties.getClusterName();
            // 2、获取同版本同集群的服务实例
            List<Instance> sameClusterVersionInstList = getTheSameClusterAndTheSameVersionInstList();
            // 3、获取最终的调用服务实例
            Instance beChoosedInstance;
            if (sameClusterVersionInstList.isEmpty()) {
                // 跨集群调用相同的版本
                beChoosedInstance = crossClusterAndSameVersionInvoke();
                log.info("跨集群同版本调用--->" +
                                "当前微服务所在集群:{},被调用微服务所在集群:{},Host:{},Port:{}",
                        currentClusterName,beChoosedInstance.getClusterName(),
                        beChoosedInstance.getIp(),beChoosedInstance.getPort());
            } else {
                // 同集群同版本
                beChoosedInstance = WeightBalacer.chooseHostByRandomWeight(sameClusterVersionInstList);
                log.info("同集群同版本调用--->" +
                                "当前微服务所在集群:{},被调用微服务所在集群:{},Host:{},Port:{}",
                        currentClusterName,beChoosedInstance.getClusterName(),
                        beChoosedInstance.getIp(),beChoosedInstance.getPort());
            }
            return new NacosServer(beChoosedInstance);
        } catch (NacosException e) {}
        return null;
    }

    /**
     * 获取跨集群的同版本服务实例
     */
    private Instance crossClusterAndSameVersionInvoke() throws NacosException {
        List<Instance> allServiceInstList = getTheSameVersionInstList();
        return WeightBalacer.chooseHostByRandomWeight(allServiceInstList);
    }

    /**
     * 获取同版本的所有服务实例列表
     */
    private List<Instance> getTheSameVersionInstList() throws NacosException {
        // 1、获取当前服务的版本号
        String currentVersion = discoveryProperties.getMetadata().get("current-version");
        // 2、获取所有的服务实例列表
        List<Instance> allServiceInstList = getAllServiceInstList();
        // 3、筛选出相同版本服务实例
        List<Instance> theSameVersionInstList = new ArrayList<>();
        allServiceInstList.forEach(a -> {
            if (StringUtils.equalsIgnoreCase(currentVersion, a.getMetadata().get("current-version"))) {
                theSameVersionInstList.add(a);
            }
        });
        return theSameVersionInstList;
    }

    /**
     * 获取同版本同集群的服务实例列表
     */
    private List<Instance> getTheSameClusterAndTheSameVersionInstList() throws NacosException {
        // 1、获取当前服务集群
        String currentClusterName = discoveryProperties.getClusterName();
        // 2、获取当前服务的版本号
        String currentVersion = discoveryProperties.getMetadata().get("current-version");
        // 3、获取所有的服务实例列表
        List<Instance> allServiceInstList = getAllServiceInstList();
        // 4、筛选出相同版本、相同集群的服务实例
        List<Instance> theSameClusterVersionInstList = new ArrayList<>();
        allServiceInstList.forEach(a -> {
            if (StringUtils.equalsIgnoreCase(currentVersion, a.getMetadata().get("current-version"))
                    && StringUtils.equalsIgnoreCase(currentClusterName, a.getClusterName())) {
                theSameClusterVersionInstList.add(a);
            }
        });
        return theSameClusterVersionInstList;
    }

    /**
     * 获取nacos上的被调用服务的所有注册实例列表
     */
    private List<Instance> getAllServiceInstList() throws NacosException {
        // 1、获取一个负载均衡对象
        BaseLoadBalancer baseLoadBalancer = (BaseLoadBalancer) getLoadBalancer();
        // 2、获取当前调用的微服务的名称
        String serviceName = baseLoadBalancer.getName();
        // 3、获取服务注册发现组件api
        NamingService namingService = discoveryProperties.namingServiceInstance();
        return namingService.getAllInstances(serviceName);
    }
}
```

**7.3.2 策略的调用配置**

```java
@Configuration
public class GlobalRibbonLBConfig {
    @Bean
    public IRule customRule(){
//        return new WeightRule(); 基于权重
//        return new TheSameClusterRule();  同集群随机权重
        return new TheSameClusterRuleWithVersion(); // 同版本同集群优先
    }
}
```

**7.3.3 配置文件**

**metadata下还可以配置其他的自定义参数**

```yaml
server:
  port: 8070
spring:
  cloud:
    nacos:
      discovery:
        server-addr: localhost:8848
        # 集群
        cluster-name: NJ-CLUSTER
        # 版本号
        metadata:
          current-version: v1
  application:
    name: consumer
```



## 8、Ribbon源码分析

https://www.processon.com/diagraming/60ada5947d9c0821843239da

![Ribbon](G:\myStudy\img\microService\springcloudalibaba\ribbon\Ribbon.jpg)
