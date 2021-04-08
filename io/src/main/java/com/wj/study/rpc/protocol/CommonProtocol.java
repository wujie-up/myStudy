package com.wj.study.rpc.protocol;

import com.wj.study.rpc.registrycenter.RegistryCenter;
import com.wj.study.rpc.transport.Content;
import com.wj.study.rpc.transport.req.Request;
import org.apache.commons.collections4.CollectionUtils;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;


public abstract class CommonProtocol implements Protocol{

    protected abstract Object doInvoke(Request request, Uri uri) throws Exception;

    @Override
    public Object invoke(Class clazz, Method method, Object[] args) throws Exception {
        Uri uri = getRemoteUri(clazz.getName());
        Request request = buildRequest(clazz, method, args);
        return doInvoke(request, uri);
    }

    private Request buildRequest(Class clazz, Method method, Object[] args) {
        Request request =  new Request();
        Content content = Content.builder()
                .serviceName(clazz.getName())
                .methodName(method.getName())
                .paramTypes(method.getParameterTypes())
                .args(args)
                .build();
        request.setRequestId(UUID.randomUUID().toString());
        request.setContent(content);
        return request;
    }

    /**
     *  获取远程服务地址
     * @param serviceName
     * @return
     */
    private Uri getRemoteUri(String serviceName){
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

    private Uri loadBalance(List<Uri> uris) {
        // todo 可以设置多种负载均衡策略，此处随机
        Random r = new Random();
        int index = r.nextInt(uris.size());
        return uris.get(index);
    };

    private List<Uri> getFromRemote() {
        // todo 从注册中心拉取服务
        return new ArrayList<>();
    }
}
