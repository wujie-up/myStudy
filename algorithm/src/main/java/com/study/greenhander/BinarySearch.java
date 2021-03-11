package com.study.greenhander;

import org.junit.Assert;

public class BinarySearch {
    public static void main(String[] args) {
        int maxLen = 1000;
        int maxValue = 10;
        int times = 10000;

        for (int i = 0; i < times; i++) {
            int num = (int) (Math.random() * maxValue);
            int[] arr = randomArr(maxLen, maxValue);
            insertSort(arr);
            boolean exists = search(arr, num);
            check(arr, exists, num);
        }
    }

    private static void check(int[] arr, boolean exists, int num) {
        boolean ans = false;
        for (int i = 0; i < arr.length; i++) {
            if (num == arr[i]) {
                ans = true;
                break;
            }
        }
        Assert.assertEquals(ans, exists);
    }

    public static int[] randomArr(int maxLen, int maxValue) {
        int len = (int) (Math.random() * maxLen);
        int[] arr = new int[len];
        for (int i = 0; i < len; i++) {
            arr[i] = (int) (Math.random() * maxValue);
        }
        return arr;
    }

    public static boolean search(int[] arr, int num) {
        if (null == arr || arr.length == 0) return false;

        int L = 0;
        int R = arr.length - 1;

        while (L <= R) {
            int mid = (L + R) / 2;
            if (arr[mid] == num) {
                return true;
            }
            if (arr[mid] < num) {
                L = mid + 1;
            } else {
                R = mid - 1;
            }
        }
        return false;
    }

    public static void insertSort(int[] arr) {
        if (arr == null || arr.length < 2) {
            return;
        }
        int end = arr.length;
        for (int i = 1; i < end; i++) {
            // 循环排好序的列表 J
            for (int j = i; j > 0; j--) {
                if (arr[j] < arr[j - 1]) {
                    swap(arr, j, j - 1);
                }
            }
        }
    }

    public static void swap(int[] arr, int source, int target) {
        arr[source] = arr[source] ^ arr[target];
        arr[target] = arr[source] ^ arr[target];
        arr[source] = arr[source] ^ arr[target];
    }
}
