package com.wj.study.rpc.protocol;

import com.wj.study.rpc.invoke.Invoker;
import com.wj.study.rpc.transport.req.Request;

import java.lang.reflect.Method;

public interface Protocol {
    Invoker getInvoker(Uri uri, Request request);
}
