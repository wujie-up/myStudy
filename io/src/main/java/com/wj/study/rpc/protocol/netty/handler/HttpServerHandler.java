package com.wj.study.rpc.protocol.netty.handler;

import com.wj.study.rpc.RpcException.RpcException;
import com.wj.study.rpc.registry.RemoteServiceRegistry;
import com.wj.study.rpc.transport.Content;
import com.wj.study.rpc.transport.req.Request;
import com.wj.study.rpc.transport.resp.Response;
import com.wj.study.rpc.util.SerializeUtil;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.http.*;

import java.lang.reflect.Method;

public class HttpServerHandler extends ChannelInboundHandlerAdapter {
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        FullHttpRequest req = (FullHttpRequest) msg;
        ByteBuf bf = req.content();
        byte[] bytes = new byte[bf.readableBytes()];
        bf.readBytes(bytes);
        Request request = (Request) SerializeUtil.bytes2Obj(bytes);
        Object res =  invoke(request.getContent());

        Response resp = new Response();
        resp.setRequestId(request.getRequestId());
        resp.setObj(res);

        byte[] resBytes = SerializeUtil.obj2Bytes(resp);
        ByteBuf byteBuf = PooledByteBufAllocator.DEFAULT.directBuffer(resBytes.length);
        byteBuf.writeBytes(resBytes);
        DefaultFullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK, byteBuf);
        // 要在请求头中写明长度
        response.headers().add("content-length", resBytes.length);
        ctx.writeAndFlush(response);
    }

    private Object invoke(Content content) {
        Object res;
        String serviceName = content.getServiceName();
        String methodName = content.getMethodName();
        Class[] paramTypes = content.getParamTypes();
        Object[] args = content.getArgs();

        Object obj = RemoteServiceRegistry.getService(serviceName);
        final Method method;
        try {
            method = obj.getClass().getMethod(methodName, paramTypes);
            res = method.invoke(obj, args);
        } catch (Exception e) {
            res = new RpcException(e);
        }
        return res;
    }
}
