package com.wj.study.rpc.protocol.netty.handler;

import com.wj.study.rpc.util.SerializeUtil;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageDecoder;

import java.util.List;

public class NettyRpcDecoder extends MessageToMessageDecoder<ByteBuf> {
    @Override
    protected void decode(ChannelHandlerContext channelHandlerContext, ByteBuf byteBuf, List<Object> list) throws Exception {
        while (byteBuf.readableBytes() >= 4) {
            // 得到数据的长度, 解决粘包拆包问题
            int len = byteBuf.getInt(byteBuf.readerIndex());
            if (byteBuf.readableBytes() >= len) {
                len = byteBuf.readInt();
                byte[] bytes = new byte[len];
                byteBuf.readBytes(bytes);
                Object resp =  SerializeUtil.bytes2Obj(bytes);
                list.add(resp);
            } else {
                break;
            }
        }
    }
}
