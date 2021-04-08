package com.wj.study.rpc.protocol;

import java.lang.reflect.Method;

public interface Protocol {
    Object invoke(Class clazz, Method method, Object[] args) throws Exception;
}
