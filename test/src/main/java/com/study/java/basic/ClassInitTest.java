package com.study.java.basic;

import java.util.concurrent.CountDownLatch;

public class ClassInitTest {
    private CountDownLatch cd = new CountDownLatch(1);
    public ClassInitTest() {
        try {
            cd.countDown();
            Thread.sleep(Integer.MAX_VALUE);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("ClassInitTest1");
    }

    public ClassInitTest(int i) {
        try {
            cd.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("ClassInitTest2");
    }

    public static void main(String[] args) {
        new Thread(() -> new ClassInitTest(), "t1").start();
        // 线程t2会等到t1执行完构造方法，才会执行构造方法
        new Thread(() -> new ClassInitTest(2), "t2").start();
    }
}
