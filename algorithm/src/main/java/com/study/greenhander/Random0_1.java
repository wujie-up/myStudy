package com.study.greenhander;

public class Random0_1 {
    public static void main(String[] args) {
        int count = 0;
        int time = 10000000;
        for (int i = 0; i < time; i++) {
            if (g() == 1) {
                count++;
            }
        }
        System.out.println((double) count / (double) time);
    }

    /**
     *  计算两次f(), 则出现概率统计如下
     *   0 0 0.81 * 0.81  (舍弃)
     *   1 1 0.19 * 0.19  (舍弃)
     *   0 1 0.81 * 0.19  返回0
     *   1 0 0.19 * 0.81  返回1
     */
    public static int g() {
        int r;
        do {
           r = f();
        } while (r == f());
        return (r == 0 ? 0 : 1);
    }
    // 0 = 81%, 1 = 19%
    public static int f() {
        return (Math.random() < 0.81) ? 0 : 1;
    }
}
