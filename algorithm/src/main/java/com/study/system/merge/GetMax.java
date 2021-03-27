package com.study.system.merge;

public class GetMax {

    public static void main(String[] args) {
        int maxLen = 50;
        int maxValue = 6000;
        int times = 8828888;

        for (int i = 0; i < times; i++) {
            int[] arr = randomArray(maxLen, maxValue);
            int m1 = getMax(arr, 0, arr.length - 1);
            int m2 = test(arr);
            if (m1 != m2) {
                System.out.println("oops !!!");
            }
        }
    }

    private static int test(int[] arr) {
        int max = 0 ;
        for (int a : arr) {
            if (a > max) {
                max = a;
            }
        }
        return max;
    }

    private static int[] randomArray(int maxLen, int maxValue) {
        int len = (int) (Math.random() *maxLen)  + 1;
        int[] arr = new int[len];
        for (int i = 0; i < len; i++) {
            arr[i] = (int) (Math.random() * maxValue) + 1;
        }
        return arr;
    }

    public static int getMax(int[] arr, int L, int R) {
        if (L == R) {
            return arr[L];
        }

        int M = (L + R) >> 1;

        int left = getMax(arr, L, M);
        int right = getMax(arr, M + 1, R);
        return Math.max(left, right);
    }
}
