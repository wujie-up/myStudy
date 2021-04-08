package com.wj.study.rpc.protocol.netty.client;

import com.wj.study.rpc.protocol.Uri;
import com.wj.study.rpc.protocol.netty.handler.NettyRpcDecoder;
import com.wj.study.rpc.protocol.netty.handler.NettyCliHandler;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;

public class NettyClient {

    private int defaultGroupSize = 1;

    public NioSocketChannel create(Uri uri) {
        NioEventLoopGroup group = new NioEventLoopGroup(defaultGroupSize);
        Bootstrap bootstrap = new Bootstrap();
        bootstrap.group(group)
                .channel(NioSocketChannel.class)
                .option(ChannelOption.RCVBUF_ALLOCATOR, new FixedRecvByteBufAllocator(65535)) // 解决不能发送字节不超过1024问题
                .handler(new ChannelInitializer<NioSocketChannel>() {
                    @Override
                    protected void initChannel(NioSocketChannel channel) throws Exception {
                        ChannelPipeline p = channel.pipeline();
                        p.addLast(new NettyRpcDecoder());
                        p.addLast(new NettyCliHandler());
                    }
                });

        Channel channel = bootstrap.connect(uri.getHost(), uri.getPort()).channel();
        return (NioSocketChannel)channel;
    }
}
