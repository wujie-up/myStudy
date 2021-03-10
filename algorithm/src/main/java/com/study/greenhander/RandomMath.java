package com.study.greenhander;

public class RandomMath {
    public static void main(String[] args) {
        int count = 0;
        int time = 100000;
        double c = 0.5;
        for (int i = 0; i < time; i++) {
            double d = Math.random();
            if (d < 0.1) {
                count++;
            }
        }
        System.out.println((double) count / (double) time);
    }
}
