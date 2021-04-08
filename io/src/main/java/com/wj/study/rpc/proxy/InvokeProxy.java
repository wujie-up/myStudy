package com.wj.study.rpc.proxy;

import com.wj.study.rpc.RpcException.RpcException;
import com.wj.study.rpc.annotation.Rpc;
import com.wj.study.rpc.protocol.Protocol;
import com.wj.study.rpc.protocol.netty.NettyProtocol;
import com.wj.study.rpc.registry.LocalServiceRegistry;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

@Slf4j
public class InvokeProxy {

    public static <T> T proxy(Class<T> interfaceClazz) {
        Class<?>[] interfaces = {interfaceClazz};

        return (T) Proxy.newProxyInstance(interfaceClazz.getClassLoader(), interfaces,
                (proxy, method, args) -> {
                    Object res;
                    if (needRpc(interfaceClazz, method)) {
                        try {
                            res = remoteInvoke(interfaceClazz, method, args);
                        } catch (Exception e) {
                            log.error("远程调用异常: {}", e);
                            if (e instanceof RpcException) {
                                RpcException rpcEx = (RpcException) e;
                                throw rpcEx.get();
                            } else {
                                // 服务降级
                                res = localInvoke(interfaceClazz, method, args);
                            }
                        }
                    } else {
                        res = localInvoke(interfaceClazz, method, args);
                    }
                    return res;
                });
    }

    private static <T> Object remoteInvoke(Class<T> clazz, Method method, Object[] args) throws Exception {
        Object res = null;
        // 1、确定使用的协议
        Protocol protocol = getProtocol(method, clazz);

        return protocol.invoke(clazz, method, args);
    }

    private static <T> Protocol getProtocol(Method method, Class<T> clazz) {
        Protocol protocol = null;
        Rpc anno = method.getAnnotation(Rpc.class);
        if (null == anno) {
            anno = clazz.getAnnotation(Rpc.class);
        } else {
            throw new RuntimeException();
        }

        String pt = anno.protocol();
        if (pt.equals("netty")) {
            protocol = new NettyProtocol();
        } else if ("http".equals(pt)) {
            // todo
            // protocol = new HttpProtocol();
        }
        return protocol;
    }

    private static <T> Object localInvoke(Class<T> clazz, Method method, Object[] args) {
        // 从本地容器中拿取服务降级的实现
        Object obj = LocalServiceRegistry.getService(clazz.getName());
        Object res = null;
        try {
            res = method.invoke(obj, args);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
        return res;
    }

    private static <T> boolean needRpc(Class<T> clazz, Method method) {
        // 查看方法上是否有 rpc注解
        if (method.isAnnotationPresent(Rpc.class)) {
            return true;
        }
        // 查看类上是否有 rpc注解
        if (clazz.isAnnotationPresent(Rpc.class)) {
            return true;
        }
        return false;
    }
}
