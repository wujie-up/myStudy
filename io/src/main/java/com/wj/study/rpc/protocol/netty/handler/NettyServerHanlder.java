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

import java.lang.reflect.Method;

public class NettyServerHanlder extends ChannelInboundHandlerAdapter {
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        System.out.println("收到客户端消息: " + msg);

        Request request = (Request) msg;
        Object res = invoke(request.getContent());
        Response resp = new Response();
        resp.setRequestId(request.getRequestId());
        resp.setObj(res);

        byte[] bytes = SerializeUtil.obj2Bytes(resp);
        ByteBuf byteBuf = PooledByteBufAllocator.DEFAULT.directBuffer(4 + bytes.length);
        byteBuf.writeInt(bytes.length);
        byteBuf.writeBytes(bytes);

        ctx.channel().writeAndFlush(byteBuf).sync();
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
