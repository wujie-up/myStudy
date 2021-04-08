package com.wj.study.rpc.registry;

import java.util.concurrent.ConcurrentHashMap;

public class LocalServiceRegistry {
    private static ConcurrentHashMap<String, Object> serviceMap = new ConcurrentHashMap<>();

    public static void registry(String serviceName, Object obj) {
        serviceMap.putIfAbsent(serviceName, obj);
    }

    public static Object getService(String serviceName) {
        return serviceMap.get(serviceName);
    }
}
