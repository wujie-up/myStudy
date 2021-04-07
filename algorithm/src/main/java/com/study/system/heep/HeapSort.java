package com.study.system.heep;

import java.util.Arrays;

public class HeapSort {
    public void sort(int[] arr) {
        if (null == arr || arr.length < 2) {
            return;
        }

        Heap heep = new Heap(arr.length);
        int heapSize = arr.length;
        // 堆化, 2种方式
        // 1、自下而上 O(N * logN)
        for (int i = 0; i < heapSize; i++) {
            heep.heapInsert(arr, i); // O(logN)
        }
        // 2、自上而下 O(N)
//        for (int i = heapSize - 1; i >= 0; i--) {
//            heep.heapify(arr, i, heapSize);
//        }


        // 排序
        while (heapSize > 0) {
            swap(arr, 0, --heapSize);
            heep.heapify(arr, 0, heapSize);
        }
    }

    private void swap(int[] arr, int a, int b) {
        int temp = arr[a];
        arr[a] = arr[b];
        arr[b] = temp;
    }

    public static void main(String[] args) {
        int maxLen = 5;
        int maxValue = 5000;
        int times = 8888888;


        for (int i = 0; i < times; i++) {
            int[] arr =  randomArr(maxLen, maxValue);
            HeapSort heapSort = new HeapSort();
            heapSort.sort(arr);
            if (!test(arr)) {
                System.out.println("oops !!!");
                System.out.println(Arrays.toString(arr));
                break;
            }
        }
    }

    private static boolean test(int[] arr) {
        if (arr == null || arr.length < 2) {
            return true;
        }
        int m = 0;
        for (int i = 1; i < arr.length; i++) {
            if (arr[i] <= arr[m]) {
                m = i;
            } else {
                return false;
            }
        }
        return true;
    }

    private static int[] randomArr(int maxLen, int maxValue) {
        int len = (int) (Math.random() * maxLen);
        int[] arr = new int[len];
        for (int i = 0; i < len; i++) {
            arr[i] = (int) (Math.random() * maxValue);
        }
        return arr;
    }
}
