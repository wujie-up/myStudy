package com.wj.study.lock;

import com.wj.study.ZkUtil;
import org.apache.zookeeper.ZooKeeper;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.concurrent.CountDownLatch;

/**
 * @description: 锁测试
 * @author: wj2wml@qq.com
 * @date: 2021-05-16 16:57
 **/
public class LockTest {
    ZooKeeper zk;

    @Before
    public void init() {
        zk = ZkUtil.getZk("lock");
    }

    @Test
    public void test() {
        CountDownLatch cd = new CountDownLatch(10);
        for (int i = 0; i < 10; i++) {
            new Thread(() -> {
                String name = Thread.currentThread().getName();
                LockWatcher watcher = new LockWatcher();
                watcher.setZk(zk);
                watcher.setThreadName(name);
                watcher.lock();
                // 执行业务代码
                System.out.println(name + ", 完成业务");
                watcher.unLock();
                cd.countDown();
            }).start();
        }
        try {
            cd.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
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
}
