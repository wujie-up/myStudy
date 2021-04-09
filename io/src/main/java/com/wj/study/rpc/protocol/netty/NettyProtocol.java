package com.wj.study.rpc.protocol.netty;

import com.wj.study.rpc.facory.NettyClientFactory;
import com.wj.study.rpc.invoke.Invoker;
import com.wj.study.rpc.invoke.NettyInvoker;
import com.wj.study.rpc.protocol.Protocol;
import com.wj.study.rpc.protocol.Uri;
import com.wj.study.rpc.protocol.netty.client.NettyClient;
import com.wj.study.rpc.transport.req.Request;


public class NettyProtocol implements Protocol {

    @Override
    public Invoker getInvoker(Uri uri, Request request) {
        NettyClient client = NettyClientFactory.createCli(uri);
        return new NettyInvoker(client, request);
    }
}

