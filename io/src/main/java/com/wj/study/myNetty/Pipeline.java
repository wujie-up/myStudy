package com.wj.study.myNetty;

import com.wj.study.myNetty.channel.NettyChannel;
import com.wj.study.myNetty.handler.ChannelHandler;
import com.wj.study.myNetty.handler.ChannelInitHandler;

import java.nio.ByteBuffer;

public class Pipeline {
    ChannelContext head;
    ChannelContext tail;

    public Pipeline(ChannelHandler handler) {
        head = tail = new ChannelContext(handler);
    }

    public synchronized void addFirst(ChannelHandler channelHandler) {
        ChannelContext context = new ChannelContext(channelHandler);
        if (null == head) {
            head = tail = context;
        } else {
            context.next = head;
            head = context;
        }
    }

    public synchronized void addLast(ChannelHandler channelHandler) {
        ChannelContext context = new ChannelContext(channelHandler);
        if (null == head) {
            head = tail = context;
        } else {
            tail.next = context;
            tail = context;
        }
    }

    /**
     * 在Channel读取到客户端 发送数据 后被调用
     */
    public void afterRead(NettyChannel channel, ByteBuffer msg) {
        ChannelContext context = head;
        while (null != context) {
            context.getChannelHandler().afterRead(channel, msg);
            context = context.next;
        }
    }

    public void afterAccept(NettyChannel channel) {
        ChannelContext context = head;
        while (null != context) {
            context.getChannelHandler().afterAccept(channel);
            context = context.next;
        }
    }

    public void close(NettyChannel channel) {
        ChannelContext context = head;
        while (null != context) {
            context.getChannelHandler().close(channel);
            context = context.next;
        }
    }

    public void init(NettyChannel channel) {
        ChannelInitHandler handler = (ChannelInitHandler) head.channelHandler;
        handler.init(channel);
        head = head.next;
    }
}
