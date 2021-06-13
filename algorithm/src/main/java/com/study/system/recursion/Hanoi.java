package com.study.system.recursion;

/**
 * @description: 汉诺塔问题
 * @author: wj2wml@qq.com
 * @date: 2021-06-10 22:31
 **/
public class Hanoi {
    // 将n从左移到右
    public static void left2Right(int n) {
        if (n == 1) {
            System.out.println("move [" + n + "] from left to right");
            return;
        }
        // 将n-1移到到中间
        left2Mid(n - 1);
        //移动n
        System.out.println("move [" + n + "] from left to right");
        // 将n-1移动到右边
        mid2Right(n - 1);
    }

    private static void left2Mid(int n) {
        if (n == 1) {
            System.out.println("move [" + n + "] from left to middle");
            return;
        }
        // 将n-1移动到右边
        left2Right(n - 1);
        System.out.println("move [" + n + "] from left to middle");
        // 将n-1移动到中间
        right2Mid(n - 1);
    }

    private static void right2Mid(int n) {
        if (n == 1) {
            System.out.println("move [" + n + "] from right to middle");
            return;
        }
        // 将n-1移动从右移动到左边
        right2Left(n - 1);
        System.out.println("move [" + n + "] from right to middle");
        // 将n-1移动到中间
        left2Mid(n - 1);
    }

    private static void right2Left(int n) {
        if (n == 1) {
            System.out.println("move [" + n + "] from right to left");
            return;
        }
        // 将n-1移动从右移动到中间
        right2Mid(n - 1);
        System.out.println("move [" + n + "] from right to left");
        // 将n-1移动到中间
        mid2Left(n - 1);
    }

    private static void mid2Left(int n) {
        if (n == 1) {
            System.out.println("move [" + n + "] from middle to left");
            return;
        }
        // 将n-1移动从中移到右边
        mid2Right(n - 1);
        System.out.println("move [" + n + "] from middle to left");
        // 将n-1移动到左边
        right2Left(n - 1);
    }

    private static void mid2Right(int n) {
        if (n == 1) {
            System.out.println("move [" + n + "] from middle to right");
            return;
        }
        // 将n-1移动到左边
        mid2Left(n - 1);
        System.out.println("move [" + n + "] from  middle to right");
        // 将n-1移动到右边
        left2Right(n - 1);
    }

    private static void hanio1(int n) {
        if (n > 0) {
            left2Right(n);
        }
    }

    public static void main(String[] args) {
        int n = 4;
        hanio1(n);
        System.out.println("============");
        hanio2(n);
    }


    public static void hanio2(int n) {
        if (n > 0) {
            f(n, "left", "right", "middle");
        }
    }

    /**
     * @param: n     要移动的数
     * @param: from  移动的起始位置
     * @param: to    移动的目的地
     * @param: other 其他位置
     **/
    public static void f(int n, String from, String to, String other) {
        if (n == 1) {
            System.out.println("move [" + n + "] from  " + from + " to " + to + "");
            return;
        }
        // 将n-1 从 from 移动到 other, 所以 形参to 位置应该传 other
        f(n - 1, from, other, to);
        System.out.println("move [" + n + "] from  " + from + " to " + to + "");
        // 将n-1 从 other 移动到 to, 所以形参to 位置应该串 to
        f(n - 1, other, to, from);
    }
}
