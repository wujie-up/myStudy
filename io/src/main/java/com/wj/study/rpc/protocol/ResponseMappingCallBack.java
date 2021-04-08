package com.wj.study.rpc.protocol;

import com.wj.study.rpc.transport.resp.Response;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

public class ResponseMappingCallBack {
    private static ConcurrentHashMap<String, CompletableFuture<Response>> mapping = new ConcurrentHashMap<>();

    public static void addCallBack(String requestId, CompletableFuture<Response> future) {
        mapping.putIfAbsent(requestId, future);
    }

    public static void callBack(Response resp) {
        CompletableFuture<Response> future = mapping.get(resp.getRequestId());
        mapping.remove(resp.getRequestId());
        future.complete(resp);
    }
}
