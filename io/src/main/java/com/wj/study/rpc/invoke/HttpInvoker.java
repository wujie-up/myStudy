package com.wj.study.rpc.invoke;

import com.wj.study.rpc.protocol.http.HttpClient;
import com.wj.study.rpc.transport.req.Request;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class HttpInvoker implements Invoker{
    private HttpClient client;
    private Request request;

    public HttpInvoker(HttpClient client, Request request) {
        this.request = request;
        this.client = client;
    }

    @Override
    public Object invoke()  {
        Object res;
        try {
            res = client.doRequest(request);
        } catch (Exception e) {
           log.info("{}", e);
            throw new RuntimeException();
        }
        return res;
    }
}
