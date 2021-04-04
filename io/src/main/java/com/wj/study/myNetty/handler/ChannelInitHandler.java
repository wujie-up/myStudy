package com.wj.study.myNetty.handler;
import com.wj.study.myNetty.channel.NettyChannel;

import java.nio.ByteBuffer;

/**
 * 共享的，用于初始化的 channelHandler，将我们自定义的 handler 添加到pipeline中
 * @Shared 可以像netty 一样标注为共享的
 */
public abstract class ChannelInitHandler implements ChannelHandler{
    public abstract void init(NettyChannel c);

    @Override
    public void afterAccept(NettyChannel c) {
    }

    @Override
    public void afterRead(NettyChannel c, ByteBuffer msg) {
    }

    @Override
    public void close(NettyChannel c) {
    }
}
