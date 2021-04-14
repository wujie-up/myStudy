package com.wj.study.rpc.invoke;

import com.alibaba.fastjson.JSON;
import com.wj.study.rpc.RpcException.RpcException;
import com.wj.study.rpc.protocol.ResponseMappingCallBack;
import com.wj.study.rpc.protocol.netty.client.NettyClient;
import com.wj.study.rpc.transport.Header;
import com.wj.study.rpc.transport.req.Request;
import com.wj.study.rpc.transport.resp.Response;
import com.wj.study.rpc.util.SerializeUtil;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.handler.codec.http.DefaultFullHttpRequest;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.HttpVersion;

import java.util.concurrent.CompletableFuture;

public class NettyInvoker implements Invoker{

    private NettyClient client;
    private Request request;

    public NettyInvoker(NettyClient client, Request request) {
        this.client = client;
        this.request = request;
    }

    @Override
    public Object invoke() throws Exception{

//        CompletableFuture<Response> future = nettyInvoke();
        CompletableFuture<Response> future = httpInvoke();

        Response response = future.get();
        Object res = response.getObj();
        if (res instanceof RpcException) {
            RpcException rpcEx = (RpcException) res;
            throw rpcEx;
        }
        return res;
    }

    private CompletableFuture<Response> httpInvoke() {
        CompletableFuture<Response> future = new CompletableFuture<>();
        ResponseMappingCallBack.addCallBack(request.getRequestId(), future);

        byte[] resBytes = SerializeUtil.obj2Bytes(request);
        ByteBuf byteBuf = PooledByteBufAllocator.DEFAULT.directBuffer(resBytes.length);
        byteBuf.writeBytes(resBytes);
        DefaultFullHttpRequest request =
                new DefaultFullHttpRequest(HttpVersion.HTTP_1_1, HttpMethod.POST, "/", byteBuf);
        request.headers().add("content-length", resBytes.length);
        client.send(request);
        return future;
    }

    private CompletableFuture<Response> nettyInvoke() {
        String requestId = request.getRequestId();
        CompletableFuture<Response> future = new CompletableFuture<>();
        ResponseMappingCallBack.addCallBack(requestId, future);

        byte[] data = SerializeUtil.obj2Bytes(request);
        Header header = new Header();
        header.setDataLen(data.length);
        byte[] head = SerializeUtil.obj2Bytes(header);


        ByteBuf byteBuf = PooledByteBufAllocator.DEFAULT.directBuffer(head.length + data.length);
        byteBuf.writeBytes(head);
        byteBuf.writeBytes(data);

        client.send(byteBuf);
        return future;
    }
}
