package com.wj.study.rpc.protocol.http;

import com.wj.study.rpc.facory.HttpClientFacory;
import com.wj.study.rpc.invoke.HttpInvoker;
import com.wj.study.rpc.invoke.Invoker;
import com.wj.study.rpc.protocol.Protocol;
import com.wj.study.rpc.protocol.Uri;
import com.wj.study.rpc.transport.req.Request;

public class HttpProtocol implements Protocol {
    @Override
    public Invoker getInvoker(Uri uri, Request request) {
        HttpClient client = HttpClientFacory.createCli(uri);
        return new HttpInvoker(client, request);
    }
}
