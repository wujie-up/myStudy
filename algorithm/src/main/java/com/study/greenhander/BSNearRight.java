package com.study.greenhander;

import org.junit.Assert;

import java.util.Arrays;

public class BSNearRight {
    public static void main(String[] args) {
        int maxLen = 15;
        int maxValue = 10000;
        int times = 500000;

        for (int i = 0; i < times; i++) {
            int num = (int) (Math.random() * maxValue);
            int[] arr = randomArr(maxLen, maxValue);
            Arrays.sort(arr);
            int maxLeft = findMinRight(arr, num);
            check(arr, num, maxLeft);
        }
    }

    public static void check(int[] arr, int num, int index) {
        int len = arr.length;
        int ans = -1;
        for (int i = 0; i < len; i++) {
            if (arr[i] <= num) {
                ans = i;
            } else {
                break;
            }
        }
        Assert.assertEquals(index, ans);
    }

    public static int[] randomArr(int maxLen, int maxValue) {
        int len = (int) (Math.random() * maxLen);
        int[] arr = new int[len];
        for (int i = 0; i < len; i++) {
            arr[i] = (int) (Math.random() * maxValue);
        }
        return arr;
    }

    public static int findMinRight(int[] arr, int num) {
        if (null == arr || arr.length == 0) return -1;

        int L = 0;
        int R = arr.length - 1;
        int ans = -1;
        while (L <= R) {
            int mid = (L + R) / 2;
            if (arr[mid] <= num) {
                ans = mid;
                L = mid + 1;
            } else {
                R = mid - 1;
            }
        }
        return ans;
    }
}
