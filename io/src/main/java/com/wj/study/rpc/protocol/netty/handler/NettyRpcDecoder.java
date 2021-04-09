package com.wj.study.rpc.protocol.netty.handler;

import com.wj.study.rpc.transport.Header;
import com.wj.study.rpc.transport.resp.Response;
import com.wj.study.rpc.util.SerializeUtil;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import io.netty.handler.codec.MessageToMessageDecoder;

import java.util.List;

public class NettyRpcDecoder extends ByteToMessageDecoder {

    private final int HEADER_SIZE = 68;

    @Override
    protected void decode(ChannelHandlerContext channelHandlerContext, ByteBuf byteBuf, List<Object> list) throws Exception {
        while (byteBuf.readableBytes() >= HEADER_SIZE) {
            byte[] bytes = new byte[HEADER_SIZE];
            byteBuf.getBytes(byteBuf.readerIndex(),bytes);
            Header header = (Header) SerializeUtil.bytes2Obj(bytes);

            if (byteBuf.readableBytes() >= header.getDataLen()) {
                byteBuf.readBytes(HEADER_SIZE);
                byte[] data = new byte[header.getDataLen()];
                byteBuf.readBytes(data);
                Object obj = SerializeUtil.bytes2Obj(data);
                list.add(obj);
            } else {
                break;
            }
        }
    }
}
