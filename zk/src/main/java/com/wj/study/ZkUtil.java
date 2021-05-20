package com.wj.study;

import org.apache.zookeeper.ZooKeeper;

import java.util.concurrent.CountDownLatch;

/**
 * @description: zk工具类
 * @author: wj2wml@qq.com
 * @date: 2021-05-16 17:00
 **/
public class ZkUtil {

    private static ZooKeeper zk;
    private static final String url = "47.108.145.68:2181/";
    private static final int sessionTimeout = 3000; // ms

    public static ZooKeeper getZk(String path) {
        CountDownLatch cd = new CountDownLatch(1);
        try {
            zk = new ZooKeeper(url + path, sessionTimeout, event -> {
                switch (event.getState()) {
                    case Unknown:
                        break;
                    case Disconnected:
                        break;
                    case NoSyncConnected:
                        break;
                    case SyncConnected:
                        System.out.println("连接成功....");
                        cd.countDown();
                        break;
                    case AuthFailed:
                        break;
                    case ConnectedReadOnly:
                        break;
                    case SaslAuthenticated:
                        break;
                    case Expired:
                        break;
                }
            });
            cd.await();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return zk;
    }
}
