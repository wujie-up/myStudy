package com.wj.study.rpc;

import com.wj.study.rpc.protocol.netty.server.NettyServer;
import com.wj.study.rpc.proxy.InvokeProxy;
import com.wj.study.rpc.registry.LocalServiceRegistry;
import com.wj.study.rpc.registry.RemoteServiceRegistry;
import com.wj.study.rpc.service.UserService;
import com.wj.study.rpc.service.entity.User;
import com.wj.study.rpc.service.impl.UserServiceFailImpl;
import com.wj.study.rpc.service.impl.UserServiceImpl;
import com.wj.study.rpc.transport.Header;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
public class RpcTest {

    AtomicInteger count = new AtomicInteger(0);

    @Test
    public void initServer() throws IOException {
        RemoteServiceRegistry.registry(UserService.class.getName(), new UserServiceImpl());

        NettyServer nettyServer = new NettyServer(1, 3);
        nettyServer.start(9090);
    }

    @Test
    public void cliInvoke() {
        LocalServiceRegistry.registry(UserService.class.getName(), new UserServiceFailImpl());
        for (int i = 0; i < 20; i++) {
            new Thread(
                    () -> {
                        UserService userService = InvokeProxy.proxy(UserService.class);
                        User user = userService.get(22);
                        log.info("第{}次完成调用，结果:{}", count.incrementAndGet(), user.getName());
                    }
            ).start();
        }

        try {
            System.in.read();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
