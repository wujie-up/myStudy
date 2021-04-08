package com.wj.study.rpc;

import com.wj.study.rpc.protocol.netty.server.NettyServer;
import com.wj.study.rpc.proxy.InvokeProxy;
import com.wj.study.rpc.registry.LocalServiceRegistry;
import com.wj.study.rpc.registry.RemoteServiceRegistry;
import com.wj.study.rpc.service.UserService;
import com.wj.study.rpc.service.entity.User;
import com.wj.study.rpc.service.impl.UserServiceFailImpl;
import com.wj.study.rpc.service.impl.UserServiceImpl;
import org.junit.Test;

import java.io.IOException;

public class RpcTest {
    @Test
    public void initServer() throws IOException {
        RemoteServiceRegistry.registry(UserService.class.getName(), new UserServiceImpl());

        NettyServer nettyServer = new NettyServer(1, 3);
        nettyServer.start(9090);
    }

    @Test
    public void cliInvoke() {
        LocalServiceRegistry.registry(UserService.class.getName(), new UserServiceFailImpl());

        UserService userService = InvokeProxy.proxy(UserService.class);
        User user = userService.get(22);
        System.out.println(user);
    }
}
