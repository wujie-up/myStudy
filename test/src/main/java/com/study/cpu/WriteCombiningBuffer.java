package com.study.cpu;

import org.junit.Test;

public class WriteCombiningBuffer {
    private static final int MAX = Integer.MAX_VALUE;
    private static final int ITEMS = 1 << 24;
    private static final int MARK = ITEMS - 1;

    private static final byte[] arrA = new byte[ITEMS];
    private static final byte[] arrB = new byte[ITEMS];
    private static final byte[] arrC = new byte[ITEMS];
    private static final byte[] arrD = new byte[ITEMS];
    private static final byte[] arrE = new byte[ITEMS];
    private static final byte[] arrF = new byte[ITEMS];

    @Test
    public void test() {
        runMethod1();
        runMethod2();
    }

    private void runMethod1() {
        long start = System.currentTimeMillis();
        for (int i = 0; i < MAX; i++) {
            int slot = i & MARK;
            byte b = (byte) i;
            arrA[slot] = b;
            arrB[slot] = b;
            arrC[slot] = b;
            arrD[slot] = b;
            arrE[slot] = b;
            arrF[slot] = b;
        }
        long end = System.currentTimeMillis();
        System.out.println("take time:" + (end - start));
    }

    private void runMethod2() {
        long start = System.currentTimeMillis();
        for (int i = 0; i < MAX; i++) {
            int slot = i & MARK;
            byte b = (byte) i;
            arrA[slot] = b;
            arrB[slot] = b;
            arrC[slot] = b;
        }

        for (int i = 0; i < MAX; i++) {
            int slot = i & MARK;
            byte b = (byte) i;
            arrD[slot] = b;
            arrE[slot] = b;
            arrF[slot] = b;
        }
        long end = System.currentTimeMillis();
        System.out.println("take time:" + (end - start));
    }
}
