package com.wj.study.rpc.protocol.netty.server;

import com.wj.study.rpc.protocol.netty.handler.HttpServerHandler;
import com.wj.study.rpc.protocol.netty.handler.NettyRpcDecoder;
import com.wj.study.rpc.protocol.netty.handler.NettyServerHanlder;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;

public class NettyServer {

    private int bossSize;
    private int workerSize;

    public NettyServer(int bossSize, int workerSize) {
        this.bossSize = bossSize;
        this.workerSize = workerSize;
    }

    public void start(int port) {
        NioEventLoopGroup bossGroup = new NioEventLoopGroup(bossSize);
        NioEventLoopGroup workerGroup = new NioEventLoopGroup(workerSize);

        ServerBootstrap bootStrap = new ServerBootstrap();
        bootStrap.group(bossGroup, workerGroup)
                .channel(NioServerSocketChannel.class)
                .option(ChannelOption.SO_BACKLOG, 1024)
                .option(ChannelOption.RCVBUF_ALLOCATOR, new FixedRecvByteBufAllocator(65535)) // 解决不能发送字节不超过1024问题
                .childHandler(new ChannelInitializer<Channel>() {
                    @Override
                    protected void initChannel(Channel ch) throws Exception {
                        ChannelPipeline p = ch.pipeline();
//                        p.addLast(new NettyRpcDecoder());
//                        p.addLast(new NettyServerHanlder());
                        // 使用http协议
                        p.addLast(new HttpServerCodec());
                        p.addLast(new HttpObjectAggregator(65535));
                        p.addLast(new HttpServerHandler());
                    }
                });

        try {
            ChannelFuture cf = bootStrap.bind(port).sync();
            cf.addListener(new ChannelFutureListener() {
                @Override
                public void operationComplete(ChannelFuture cf) throws Exception {
                    if (cf.isSuccess()) {
                        System.out.println("服务启动成功......");
                    } else {
                        System.out.println("服务启动失败......");
                    }
                }
            });
            cf.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
