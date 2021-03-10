package com.study.greenhander;

public class XDounbleRandom {
    public static void main(String[] args) {
        int count = 0;
        int time = 10000000;
        double c = 0.3;
        for (int i = 0; i < time; i++) {
            if (xToXPower2() < c) {
                count++;
            }
        }
        System.out.println((double) count / (double) time);
    }

    public static double xToXPower2() {
        return Math.max(Math.random(), Math.random());
    }
}
