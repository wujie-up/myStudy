package com.wj.study.rpc.facory;

import com.wj.study.rpc.protocol.Uri;
import com.wj.study.rpc.protocol.netty.client.NettyClient;
import io.netty.channel.socket.nio.NioSocketChannel;

import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

public class NettyClientFactory {
    private static int defaultPoolSize = 5;
    private static ConcurrentHashMap<Uri, NettyCliPool> cliMap = new ConcurrentHashMap<>();

    public static NioSocketChannel getCli(Uri uri) {
        NettyCliPool cliPool = cliMap.get(uri);
        if (null == cliPool) {
            cliPool = initPool(uri);
        }
        Random r = new Random();
        int i = r.nextInt(defaultPoolSize);
        if (null == cliPool.channels[i] || !cliPool.channels[i].isActive()) {
            synchronized (cliPool.lock[i]) {
                if (null == cliPool.channels[i]) {
                    cliPool.channels[i] = createCli(uri);
                }
            }
        }
        return cliPool.channels[i];
    }

    private static NettyCliPool initPool(Uri uri) {
        synchronized (cliMap) {
            if (null == cliMap.get(uri)) {
                NettyCliPool cliPool = new NettyCliPool(defaultPoolSize);
                cliMap.putIfAbsent(uri, cliPool);
            }
        }
        return cliMap.get(uri);
    }

    private static NioSocketChannel createCli(Uri uri) {
        NettyClient client = new NettyClient();
        return client.create(uri);
    }

    private static class NettyCliPool {
        private volatile NioSocketChannel[] channels;
        private Object[] lock;

        public NettyCliPool(int size) {
            channels = new NioSocketChannel[size];
            lock = new Object[size];
            for (int i = 0; i < size; i++) {
                lock[i] = new Object();
            }
        }
    }
}
