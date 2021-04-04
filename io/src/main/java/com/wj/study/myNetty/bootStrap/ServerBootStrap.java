package com.wj.study.myNetty.bootStrap;
import com.wj.study.myNetty.NettyGroup;
import com.wj.study.myNetty.NettyThread;
import com.wj.study.myNetty.Pipeline;
import com.wj.study.myNetty.channel.NettyChannel;
import com.wj.study.myNetty.handler.ChannelHandler;

import java.net.InetSocketAddress;
import java.nio.channels.ServerSocketChannel;

public class ServerBootStrap {
    private NettyGroup bossGroup;
    private ChannelHandler initChannelHandler;

    public ServerBootStrap group(NettyGroup bossGroup, NettyGroup workerGroup) {
        this.bossGroup = bossGroup;
        this.bossGroup.setWorker(workerGroup);
        bossGroup.setBootStrap(this);
        workerGroup.setBootStrap(this);
        return this;
    }

    public ChannelHandler getInitChannelHandler() {
        return this.initChannelHandler;
    }

    public ServerBootStrap initHandler(ChannelHandler channelHandler) {
        this.initChannelHandler = channelHandler;
        return this;
    }

    public void bind(InetSocketAddress address) {
        // 绑定到boss线程组
        for (int i = 0; i < bossGroup.getThreads().length; i++) {
            try {
                ServerSocketChannel server = ServerSocketChannel.open();
                server.configureBlocking(false);
                server.bind(address);
                // 给boss线程添加任务：1、注册client到selector，2、初始化pipeline
                NettyThread thread = bossGroup.getThreads()[i];
                thread.getTasks().put(new NettyChannel(server, new Pipeline(initChannelHandler)));
                // 唤醒boss线程处理上面分发的任务
                bossGroup.getThreads()[i].getSelector().wakeup();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
