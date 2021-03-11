package com.study.greenhander;

import org.junit.Assert;

import java.util.Arrays;

public class BSPartMin {
    public static void main(String[] args) {
        int maxLen = 20;
        int maxValue = 10000;
        int times = 500000;

        for (int i = 0; i < times; i++) {
            int[] arr = randomArr(maxLen, maxValue);
            int partMin = findPartMin(arr);
            if (!check(arr, partMin)) {
                System.out.println(Arrays.toString(arr));
                System.out.println(partMin);
            }
        }
    }

    public static boolean check(int[] arr, int min) {
        if (arr.length < 1) {
            return min == -1;
        }

        if (arr.length == 1) {
            return min == 0;
        }

        if (min == 0) {
            return arr[min] < arr[min + 1];
        } else if (min == (arr.length - 1)) {
            return arr[min] < arr[min - 1];
        } else {
            return (arr[min] < arr[min - 1]) && (arr[min] < arr[min + 1]);
        }
    }

    public static int[] randomArr(int maxLen, int maxValue) {
        int len = (int) (Math.random() * maxLen) + 1;
        int[] arr = new int[len];
        arr[0] = (int) (Math.random() * maxValue) + 1;
        for (int i = 1; i < len; i++) {
            do {
                arr[i] = (int) (Math.random() * maxValue) + 1;
            } while (arr[i] == arr[i - 1]);
        }
        return arr;
    }

    public static int findPartMin(int[] arr) {
        if (null == arr || arr.length == 0) return -1;
        int len = arr.length;
        if (len == 1) return 0;

        if (arr[0] < arr[1]) return 0;
        if (arr[len - 1] < arr[len - 2]) return len - 1;

        int L = 0;
        int R = arr.length - 1;
        while (L < R - 1) {
            int mid = (L + R) / 2;
            if (arr[mid] < arr[mid - 1] && arr[mid] < arr[mid + 1]) {
                return mid;
            } else {
                if (arr[mid] > arr[mid - 1]) {
                    R = mid - 1;
                } else {
                    L = mid + 1;
                }
            }
        }
        // 跳出循环时，只剩下L R两个位置
        return (arr[L] < arr[R] ? L : R);
    }
}
