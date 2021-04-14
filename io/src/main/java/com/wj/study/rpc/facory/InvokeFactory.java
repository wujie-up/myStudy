package com.wj.study.rpc.facory;

import com.wj.study.rpc.annotation.Rpc;
import com.wj.study.rpc.invoke.Invoker;
import com.wj.study.rpc.protocol.Protocol;
import com.wj.study.rpc.protocol.Uri;
import com.wj.study.rpc.protocol.http.HttpProtocol;
import com.wj.study.rpc.protocol.netty.NettyProtocol;
import com.wj.study.rpc.registrycenter.RegistryCenter;
import com.wj.study.rpc.transport.Content;
import com.wj.study.rpc.transport.req.Request;
import org.apache.commons.collections4.CollectionUtils;

import java.lang.reflect.Method;
import java.util.*;

public class InvokeFactory {

    private static Map<String, Protocol> protocolMap = new HashMap<>();

    static {
        // 在这里初始化所有的协议
        protocolMap.put("netty", new NettyProtocol());
        protocolMap.put("http", new HttpProtocol());
    }

    public static Invoker createInvoke(Class clazz, Method method, Object[] args) {
        // 1、获取服务地址
        Uri uri = getRemoteUri(clazz.getName());
        // 2、确定使用的协议, 协议只负责 创建连接渠道
        Protocol protocol = getProtocol(method, clazz);
        // 3、通用的请求封装
        Request request = wrapperRequest(clazz.getName(), method, args);
        // 4、通过协议返回调用Invoker对象，进行调用
        return buildInvoker(protocol, uri, request);
    }

    private static Request wrapperRequest(String serviceName, Method method, Object[] args) {
        Request request =  new Request();
        Content content = Content.builder()
                .serviceName(serviceName)
                .methodName(method.getName())
                .paramTypes(method.getParameterTypes())
                .args(args)
                .build();
        request.setRequestId(UUID.randomUUID().toString());
        request.setContent(content);
        return request;
    }

    private static Invoker buildInvoker(Protocol protocol, Uri uri, Request request) {
        return protocol.getInvoker(uri, request);
    }

    /**
     *  获取远程服务地址
     * @param serviceName
     * @return
     */
    private static Uri getRemoteUri(String serviceName){
        // 1、先尝试从本地获取
        List<Uri> uris = RegistryCenter.getServerUris(serviceName);
        // 2、本地获取不到，从远程获取
        if (CollectionUtils.isEmpty(uris)) {
            synchronized (RegistryCenter.class) {
                if (CollectionUtils.isEmpty(uris)) {
                    uris = getFromRemote();
                    RegistryCenter.registry(serviceName, uris);
                }
            }
        }
        // 3、拿到服务列表后，做负责均衡
        return loadBalance(uris);
    }

    private static Uri loadBalance(List<Uri> uris) {
        // todo 可以设置多种负载均衡策略，此处随机
        Random r = new Random();
        int index = r.nextInt(uris.size());
        return uris.get(index);
    };

    private static List<Uri> getFromRemote() {
        // todo 从注册中心拉取服务
        return new ArrayList<>();
    }


    private static <T> Protocol getProtocol(Method method, Class<T> clazz) {
        Rpc anno = method.getAnnotation(Rpc.class);
        if (null == anno) {
            anno = clazz.getAnnotation(Rpc.class);
        } else {
            throw new RuntimeException();
        }

        String pt = anno.protocol();
        return protocolMap.get(pt);
    }
}
