package com.wj.study.config;

import org.apache.zookeeper.AsyncCallback;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.data.Stat;

import java.util.concurrent.CountDownLatch;

/**
 * @description: 配置监听
 * @author: wj2wml@qq.com
 * @date: 2021-05-20 20:57
 **/
public class ConfigWatcher implements Watcher, AsyncCallback.DataCallback {
    ZooKeeper zk;
    ConfigTest config;
    CountDownLatch cd;

    public void setZk(ZooKeeper zk) {
        this.zk = zk;
    }

    public void setConfig(ConfigTest config) {
        this.config = config;
    }

    public void setCd(CountDownLatch cd) {
        this.cd = cd;
    }

    @Override
    public void process(WatchedEvent event) {
        // 配置更新
        switch (event.getType()) {
            case None:
                break;
            case NodeCreated:
                break;
            case NodeDeleted:
                break;
            case NodeDataChanged:
                updateConfig();
                break;
            case NodeChildrenChanged:
                break;
        }
    }

    private void updateConfig() {
        cd = new CountDownLatch(1);
        start();
    }

    @Override
    public void processResult(int rc, String path, Object ctx, byte[] data, Stat stat) {
        this.config.setConfig(new String(data));
        cd.countDown();
    }

    public void start() {
        zk.getData("/config", this, this, "aaa");
    }
}
