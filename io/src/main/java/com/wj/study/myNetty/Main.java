package com.wj.study.myNetty;

import com.wj.study.myNetty.bootStrap.ServerBootStrap;
import com.wj.study.myNetty.channel.NettyChannel;
import com.wj.study.myNetty.handler.ChannelInitHandler;

import java.net.InetSocketAddress;

public class Main {
    public static void main(String[] args) {
        NettyGroup bossGroup = new NettyGroup(1);
        NettyGroup workerGroup = new NettyGroup(3);
        ServerBootStrap bootStrap = new ServerBootStrap();
        bootStrap.group(bossGroup, workerGroup)
                .initHandler(new ChannelInitHandler() {
                    @Override
                    public void init(NettyChannel c) {
                        c.pipeline().addLast(new MyHandler());
                    }
                })
                .bind(new InetSocketAddress(9090));
    }
}
