package com.wj.study.rpc;

import com.wj.study.rpc.protocol.http.HttpServer;
import com.wj.study.rpc.protocol.netty.server.NettyServer;
import com.wj.study.rpc.proxy.InvokeProxy;
import com.wj.study.rpc.registry.LocalServiceRegistry;
import com.wj.study.rpc.registry.RemoteServiceRegistry;
import com.wj.study.rpc.service.UserService;
import com.wj.study.rpc.service.entity.User;
import com.wj.study.rpc.service.impl.UserServiceFailImpl;
import com.wj.study.rpc.service.impl.UserServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
public class RpcTest {

    AtomicInteger count = new AtomicInteger(0);

    @Test
    public void initServer() throws IOException {
        RemoteServiceRegistry.registry(UserService.class.getName(), new UserServiceImpl());

        NettyServer nettyServer = new NettyServer(1, 3);
        nettyServer.start(9090);

//        HttpServer httpServer = new HttpServer();
//        httpServer.start(9090);
        System.in.read();
    }

    @Test
    public void cliInvoke() {
        LocalServiceRegistry.registry(UserService.class.getName(), new UserServiceFailImpl());
        int num = 20;
        CountDownLatch downLatch = new CountDownLatch(num);
        AtomicInteger count = new AtomicInteger(0);
        for (int i = 0; i < num; i++) {
            new Thread(
                    () -> {
                        UserService userService = InvokeProxy.proxy(UserService.class);
                        User user = userService.get(22);
                        log.info("第{}次完成调用，结果:{}", count.incrementAndGet(), user.getName());
                        downLatch.countDown();
                    }
            ).start();
        }
        try {
            downLatch.await();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
