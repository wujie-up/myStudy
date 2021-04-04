package com.wj.study.myNetty.channel;

import com.wj.study.myNetty.Pipeline;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.Channel;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;

/**
 * 自己封装的Channel，里面包装了用于通信的Channel 和自定义的pipeline
 */
public class NettyChannel {
    Channel channel;
    Pipeline pipeline;

    public NettyChannel(Channel channel, Pipeline pipeline) {
        this.channel = channel;
        this.pipeline = pipeline;
    }

    public void write(String msg) {
        SocketChannel client = (SocketChannel) channel;
        ByteBuffer bf = ByteBuffer.wrap(msg.getBytes(StandardCharsets.UTF_8));
        try {
            client.write(bf);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String getRemoteAddress() {
        SocketChannel client = (SocketChannel) channel;
        String address = "";
        try {
            address = client.getRemoteAddress().toString();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return address;
    }

    public Pipeline pipeline() {
        return this.pipeline;
    }

    public Channel channel() {
        return this.channel;
    }

    public void close() {
        try {
            channel.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
