package com.study.dubbo.spi;

import org.apache.dubbo.common.extension.ExtensionLoader;
import org.apache.dubbo.rpc.Protocol;
import org.junit.Test;

public class SpiDemo {
    @Test
    public void test1() {
        ExtensionLoader<Protocol> extensionLoader = ExtensionLoader.getExtensionLoader(Protocol.class);
        Protocol http = extensionLoader.getExtension("dubbo");
        System.out.println(http);
    }
}
