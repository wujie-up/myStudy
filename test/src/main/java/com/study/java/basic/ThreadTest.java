package com.study.java.basic;

import org.junit.Test;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ThreadTest {

    public synchronized void before() {
        System.out.println("before");
        notify();
    }

    public synchronized void after() {
        try {
            wait();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("after");
    }

    @Test
    public void test1() {
        ThreadTest threadTest = new ThreadTest();
        ExecutorService executor = Executors.newFixedThreadPool(2);
        executor.execute(()->threadTest.after());
        executor.execute(()->threadTest.before());
    }
}
