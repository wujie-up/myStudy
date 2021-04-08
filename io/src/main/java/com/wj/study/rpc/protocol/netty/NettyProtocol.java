package com.wj.study.rpc.protocol.netty;

import com.wj.study.rpc.RpcException.RpcException;
import com.wj.study.rpc.facory.NettyClientFactory;
import com.wj.study.rpc.protocol.CommonProtocol;
import com.wj.study.rpc.protocol.ResponseMappingCallBack;
import com.wj.study.rpc.protocol.Uri;
import com.wj.study.rpc.transport.req.Request;
import com.wj.study.rpc.transport.resp.Response;
import com.wj.study.rpc.util.SerializeUtil;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.socket.nio.NioSocketChannel;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;


public class NettyProtocol extends CommonProtocol {

    @Override
    protected Object doInvoke(Request request, Uri uri) throws Exception {
        final NioSocketChannel channel = NettyClientFactory.getCli(uri);

        String requestId = UUID.randomUUID().toString();
        request.setRequestId(requestId);
        CompletableFuture<Response> future = new CompletableFuture<>();
        ResponseMappingCallBack.addCallBack(requestId, future);

        byte[] bytes = SerializeUtil.obj2Bytes(request);
        int len = bytes.length;
        ByteBuf byteBuf = PooledByteBufAllocator.DEFAULT.directBuffer(4 + bytes.length);
        byteBuf.writeInt(len);
        byteBuf.writeBytes(bytes);

        channel.writeAndFlush(byteBuf);
        Response response = future.get();
        Object res = response.getObj();
        if (res instanceof RpcException) {
            RpcException rpcEx = (RpcException) res;
            throw rpcEx;
        }
        return res;
    }
}

