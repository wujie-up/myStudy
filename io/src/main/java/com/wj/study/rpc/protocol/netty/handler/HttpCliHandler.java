package com.wj.study.rpc.protocol.netty.handler;

import com.wj.study.rpc.protocol.ResponseMappingCallBack;
import com.wj.study.rpc.transport.resp.Response;
import com.wj.study.rpc.util.SerializeUtil;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.http.FullHttpResponse;

public class HttpCliHandler extends ChannelInboundHandlerAdapter {
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        FullHttpResponse response = (FullHttpResponse) msg;
        ByteBuf byteBuf = response.content();
        byte[] bytes = new byte[byteBuf.readableBytes()];
        byteBuf.readBytes(bytes);
        Response resp = (Response) SerializeUtil.bytes2Obj(bytes);
        ResponseMappingCallBack.callBack(resp);
    }
}
