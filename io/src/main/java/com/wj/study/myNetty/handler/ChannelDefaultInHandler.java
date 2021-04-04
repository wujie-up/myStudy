package com.wj.study.myNetty.handler;

import com.wj.study.myNetty.channel.NettyChannel;

import java.nio.ByteBuffer;

public abstract class ChannelDefaultInHandler extends ChannelInitHandler {
    @Override
    public void init(NettyChannel c) {
    }

    @Override
    public void afterAccept(NettyChannel c) {
    }

    @Override
    public void afterRead(NettyChannel c, ByteBuffer msg) {
        // 每次clear后pos会重置，数据从头写
        int position = msg.position();
        byte[] bs = new byte[position];
        int i = 0;
        // 只读取本次发送的有效数据，忽略历史数据
        while (i < position) {
            bs[i] = msg.get(i);
            i++;
        }
        afterRead(c, new String(bs));
    }

    public abstract void afterRead(NettyChannel c, String msg);

    @Override
    public void close(NettyChannel c) {
        c.close();
    }
}