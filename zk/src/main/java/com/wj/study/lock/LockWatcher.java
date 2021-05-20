package com.wj.study.lock;

import org.apache.zookeeper.*;
import org.apache.zookeeper.data.Stat;

import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CountDownLatch;

/**
 * @description: 锁监视器
 * @author: wj2wml@qq.com
 * @date: 2021-05-16 16:56
 **/
public class LockWatcher implements Watcher,
        AsyncCallback.StringCallback,
        AsyncCallback.Children2Callback,
        AsyncCallback.StatCallback {
    private ZooKeeper zk;
    private CountDownLatch cd = new CountDownLatch(1);
    private String pathName;
    private String threadName;

    public void setZk(ZooKeeper zk) {
        this.zk = zk;
    }

    public void setThreadName(String threadName) {
        this.threadName = threadName;
    }

    // 监听 回调
    @Override
    public void process(WatchedEvent watchedEvent) {
        switch (watchedEvent.getType()) {
            case None:
                break;
            case NodeCreated:
                break;
            case NodeDeleted:
                // 前面的节点被删除后，查看自己是不是所有子节点的第一个
                zk.getChildren("/", false, this, "abb");
                break;
            case NodeDataChanged:
                break;
            case NodeChildrenChanged:
                break;
        }
    }

    // create 回调
    @Override
    public void processResult(int rc, String path, Object ctx, String name) {
        if (name != null) {
            this.pathName = name;
            // 创建成功, 拿到所有的 孩子节点
            zk.getChildren("/", false, this, "abb");
        }
    }

    // 获取孩子节点 回调
    @Override
    public void processResult(int rc, String path, Object ctx, List<String> children, Stat stat) {
        Collections.sort(children);

        int i = children.indexOf(pathName.substring(1));

        if (i == 0) {
            System.out.println(threadName + " 拿到了锁: " + pathName);
//            try {
//                zk.setData(pathName, "gg".getBytes(StandardCharsets.UTF_8), 0);
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
            // 解锁业务线程
            cd.countDown();
        } else {
            zk.exists("/" + children.get(i - 1), this, this, "ddd");
        }
    }

    // exits 回调
    @Override
    public void processResult(int rc, String path, Object ctx, Stat stat) {
        // 当监听前面的节点时，可能前面的节点已经删除了，所以需要重新获取孩子节点
        if (null == stat) {
            zk.getChildren("/", false, this, "abb");
        }
    }

    public void lock() {
        try {

            zk.create("/lock", threadName.getBytes(StandardCharsets.UTF_8), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL_SEQUENTIAL, this, "abc");
            cd.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void unLock() {
        try {
            zk.delete(pathName, 0);
            System.out.println(threadName + "删除了" + pathName);
        } catch (Exception e) {
        }
    }
}
