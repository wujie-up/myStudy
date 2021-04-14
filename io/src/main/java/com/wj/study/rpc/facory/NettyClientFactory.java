package com.wj.study.rpc.facory;

import com.wj.study.rpc.protocol.Uri;
import com.wj.study.rpc.protocol.netty.client.NettyClient;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

@Slf4j
public class NettyClientFactory {
    private static int defaultPoolSize = 1;
    private static ConcurrentHashMap<String, NettyClient[]> cliMap = new ConcurrentHashMap<>();
    private static int NettyClientGroupSize = 1;

    private static Lock lock = new ReentrantLock();

    private static AtomicInteger index = new AtomicInteger(0);

    protected static AtomicInteger lockCount = new AtomicInteger(0);
    protected static AtomicInteger cliCount = new AtomicInteger(0);

    public static NettyClient createCli(Uri uri) {
        String url = uri.getHost() + uri.getPort();
        NettyClient[] clients = cliMap.get(url);

        if (null == clients) {
            try {
                lock.lock();
                log.info("【{}】线程获取了，第【{}】把锁", Thread.currentThread().getName(), lockCount.incrementAndGet());
                if (null == cliMap.get(url)) {
                    log.info("【{}】线程初始化，第【{}】次cli", Thread.currentThread().getName(), cliCount.incrementAndGet());
                    clients = new NettyClient[defaultPoolSize];
                    for (int i = 0; i < clients.length; i++) {
                        clients[i] = new NettyClient(NettyClientGroupSize);
                    }
                    cliMap.put(url, clients);
                }
            }  finally {
                lock.unlock();
            }
        }

        int index = NettyClientFactory.index.getAndIncrement();
        NettyClient client = cliMap.get(url)[index % defaultPoolSize];
        if (!client.isAlive()) {
            client.connect(uri);
        }
        return client;
    }
}
