package com.wj.study.myNetty;


import com.wj.study.myNetty.channel.NettyChannel;
import com.wj.study.myNetty.handler.ChannelDefaultInHandler;

public class MyHandler extends ChannelDefaultInHandler {
    @Override
    public void afterAccept(NettyChannel c) {
        String remoteAddress = c.getRemoteAddress();
        System.out.println("客户端: " + remoteAddress + "上线了");
    }

    @Override
    public void afterRead(NettyChannel c, String msg) {
        System.out.println("收到客户端消息: " + msg);
        c.write("你好，客户端\n");
    }

    @Override
    public void close(NettyChannel c) {
        System.out.println("客户端:" + c.getRemoteAddress() + "离线了");
        c.close();
    }
}