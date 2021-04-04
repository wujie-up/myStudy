package com.study.system.quicksort;

import com.sun.org.apache.bcel.internal.generic.IF_ACMPEQ;

import java.util.Arrays;

public class Partition {
    public static void main(String[] args) {
        int maxLen = 10;
        int maxValue = 500;
        int times = 888888;
        for (int i = 0; i < times; i++) {
            int[] arr = randomArr(maxLen, maxValue);
            int index = (int) (Math.random() * (arr.length - 1));
            int num = arr.length == 0 ? 0 : arr[index];
            process2(arr, num);
            if (!test2(arr, num)) {
                System.out.println("oops!!!");
                System.out.println(Arrays.toString(arr));
                break;
            }
        }
    }
    private static boolean test2(int[] arr, int num) {
        int count = 0;
        boolean less = true;
        boolean equal = false;
        for (int i = 0; i < arr.length; i++) {
            if (arr[i] < num && !less) {
                less = true;
                equal = false;
                count++;
            }
            if (arr[i] ==num && !equal) {
                equal = true;
                count++;
            }
            if (arr[i] > num && less) {
                equal = false;
                less = false;
                count++;
            }
        }
        return count <= 2;
    }


    private static boolean test1(int[] arr, int num) {
        int count = 0;
        boolean less = true;
        for (int i = 0; i < arr.length; i++) {
            if (arr[i] <= num && !less) {
                less = true;
                count++;
            }
            if (arr[i] > num && less) {
                less = false;
                count++;
            }
        }
        return count <= 1;
    }

    public static int[] randomArr(int maxLen, int maxValue) {
        int len = (int) (Math.random() * maxLen);
        int[] arr = new int[len];
        for (int i = 0; i < len; i++) {
            arr[i] = (int) (Math.random() * maxValue);
        }
        return arr;
    }

    public static void process1(int[] arr, int num) {
        if (null == arr || arr.length < 2) {
            return;
        }
        int L = -1;
        int len = arr.length;
        int cur = 0;

        while (cur < len) {
            if (arr[cur] <= num) {
                swap(arr, L + 1, cur);
                L++;
                cur++;
            } else {
                cur++;
            }
        }
    }

    public static void process2(int[] arr, int num) {
        if (null == arr || arr.length < 2) {
            return;
        }
        int L = -1;
        int R = arr.length;
        int cur = 0;

        while (cur < R) {
            if (arr[cur] < num) {
                swap(arr, L + 1, cur);
                L++;
                cur++;
            } else if (arr[cur] == num) {
                cur++;
            } else {
                swap(arr, R - 1, cur);
                R--;
            }
        }
    }

    private static void swap(int[] arr, int a, int b) {
        if (a == b) {
            return;
        }
        int temp = arr[a];
        arr[a] = arr[b];
        arr[b] = temp;
    }
}
