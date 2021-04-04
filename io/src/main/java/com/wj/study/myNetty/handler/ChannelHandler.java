package com.wj.study.myNetty.handler;


import com.wj.study.myNetty.channel.NettyChannel;

import java.nio.ByteBuffer;

public interface ChannelHandler {

    void afterAccept(NettyChannel c);

    void afterRead(NettyChannel c, ByteBuffer msg);

    void close(NettyChannel c);
}
