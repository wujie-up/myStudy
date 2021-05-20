package com.wj.study.config;

import com.wj.study.ZkUtil;
import org.apache.zookeeper.ZooKeeper;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.concurrent.CountDownLatch;

/**
 * @description: zk注册中心demo
 * @author: wj2wml@qq.com
 * @date: 2021-05-20 20:54
 **/
public class ConfigTest {
    ZooKeeper zk;
    String config;

    @Before
    public void init() {
        zk = ZkUtil.getZk("");
    }

    @Test
    public void test() {
        CountDownLatch cd = new CountDownLatch(1);
        ConfigWatcher watcher = new ConfigWatcher();
        watcher.setZk(zk);
        watcher.setConfig(this);
        watcher.setCd(cd);
        watcher.start();
        while (true) {
            try {
                cd.await();
                System.out.println(this.config);
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    @After
    public void after() {
        try {
            zk.close();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void setConfig(String config) {
        this.config = config;
    }

}
