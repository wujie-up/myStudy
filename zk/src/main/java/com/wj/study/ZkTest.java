package com.wj.study;

import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.ZooDefs;
import org.apache.zookeeper.ZooKeeper;

import java.util.List;
import java.util.concurrent.CountDownLatch;

/**
 * @description:
 * @author: wj2wml@qq.com
 * @date: 2021-05-16 20:39
 **/
public class ZkTest {
    public static void main(String[] args) throws Exception{
        CountDownLatch cd = new CountDownLatch(1);
        ZooKeeper zk = new ZooKeeper("127.0.0.1:2183", Integer.MAX_VALUE, event -> {
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

        String path = zk.create("/test10", "ss".getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
//        List<String> children = zk.getChildren("/", false);

        System.out.println(path);
    }
}
