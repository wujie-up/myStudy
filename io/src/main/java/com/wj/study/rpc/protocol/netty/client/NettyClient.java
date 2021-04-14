package com.wj.study.rpc.protocol.netty.client;

import com.wj.study.rpc.protocol.Uri;
import com.wj.study.rpc.protocol.netty.handler.HttpCliHandler;
import com.wj.study.rpc.protocol.netty.handler.NettyRpcDecoder;
import com.wj.study.rpc.protocol.netty.handler.NettyCliHandler;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.http.HttpClientCodec;
import io.netty.handler.codec.http.HttpObjectAggregator;

public class NettyClient {
    private Channel channel;

    private Bootstrap bootstrap;
    /**
     * 用来标识当前客户端是否已经连接，必须volatile修饰
     */
    private volatile boolean connected;

    private final Object lock = new Object();

    public NettyClient(int groupSize) {
        this.connected = false;
        init(groupSize);
    }

    public void init(int groupSize) {
        NioEventLoopGroup group = new NioEventLoopGroup(groupSize);
        bootstrap = new Bootstrap();
        bootstrap.group(group)
                .channel(NioSocketChannel.class)
                .option(ChannelOption.SO_KEEPALIVE, true)
                .option(ChannelOption.RCVBUF_ALLOCATOR, new FixedRecvByteBufAllocator(65535)) // 解决不能发送字节不超过1024问题
                .handler(new ChannelInitializer<NioSocketChannel>() {
                    @Override
                    protected void initChannel(NioSocketChannel channel) throws Exception {
                        ChannelPipeline p = channel.pipeline();
//                        p.addLast(new NettyRpcDecoder());
//                        p.addLast(new NettyCliHandler());
                        p.addLast(new HttpClientCodec());
                        p.addLast(new HttpObjectAggregator(65535));
                        p.addLast(new HttpCliHandler());
                    }
                });
    }

    public void connect(Uri uri) {
        if (!connected) {
            synchronized (lock) {
                if (!connected) {
                    try {
                        this.channel = bootstrap.connect(uri.getHost(), uri.getPort()).sync().channel();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    connected = true;
                }
            }
        }
    }

    public void send(Object obj) {
        this.channel.writeAndFlush(obj);
    }

    public boolean isAlive() {
        // 必须满足两个情况从，才算存活
        return connected && this.channel.isActive();
    }
}
