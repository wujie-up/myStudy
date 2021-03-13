package com.study.greenhander;

import java.util.Arrays;

public class VerifySort {
    public static void main(String[] args) {
        int len = 20;
        int max = 1000;
        int times = 100000;

        for (int i = 0; i < times; i++) {
            int[] arr = randomArr(len, max);
//            bubbleSort(arr);
            insertSort(arr);
           if (!checkSort(arr)) {
               System.out.println(Arrays.toString(arr));
           }
        }
    }

    public static boolean checkSort(int[] arr) {
        if (arr == null || arr.length == 0) return true;
        int min = arr[0];
        for (int i = 1; i < arr.length; i++) {
            if (arr[i] < min) {
                return false;
            }
        }
        return true;
    }

    public static int[] randomArr(int maxLen, int maxValue) {
        int len = (int) (Math.random() * maxLen);
        int[] arr = new int[len];
        for (int i = 0; i < len; i++) {
            arr[i] = (int) (Math.random() * maxValue);
        }
        return arr;
    }

    public static void bubbleSort(int[] arr) {
        if (arr == null || arr.length < 2) {
            return;
        }
        int end = arr.length;
        for (int i = end; i >= 1; i--) {
            for (int j = 1; j < end; j++) {
                if (arr[j - 1] > arr[j]) {
                    swap(arr, j - 1, j);
                }
            }
        }
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
                } else {
                    break;
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
