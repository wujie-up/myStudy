package com.wj.study.rpc.protocol.netty.handler;

import com.wj.study.rpc.protocol.ResponseMappingCallBack;
import com.wj.study.rpc.transport.resp.Response;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

public class NettyCliHandler extends ChannelInboundHandlerAdapter {
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        // 收到服务端响应
        System.out.println("收到服务端响应:" + msg);
        Response response = (Response) msg;
        ResponseMappingCallBack.callBack(response);
    }
}
