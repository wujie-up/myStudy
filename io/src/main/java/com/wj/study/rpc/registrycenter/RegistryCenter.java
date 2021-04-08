package com.wj.study.rpc.registrycenter;

import com.wj.study.rpc.protocol.Uri;
import com.wj.study.rpc.service.UserService;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 本地注册表，需要定时从注册中心拉取服务列表
 */
public class RegistryCenter {
    private static ConcurrentHashMap<String, List<Uri>> localServiceUris = new ConcurrentHashMap<>();

    static {
        // 模拟直接注册一个
        localServiceUris.putIfAbsent(UserService.class.getName(), new ArrayList<Uri>(){{
            add(new Uri("127.0.0.1", 9090));
        }});
    }

    public static List<Uri> getServerUris(String serviceName) {
        return localServiceUris.get(serviceName);
    }

    public static void registry(String serviceName, List<Uri> serverUris) {
        localServiceUris.putIfAbsent(serviceName, serverUris);
    }
}
