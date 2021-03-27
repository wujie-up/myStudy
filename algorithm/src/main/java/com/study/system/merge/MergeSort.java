package com.study.system.merge;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MergeSort {
    public static void main(String[] args) {
        int maxValue = 10000;
        int maxLen = 6;
        int times = 500666;
        MergeSort sort =new MergeSort();
        for (int i = 0; i < times; i++) {
            int[] arr = randomArray(maxLen, maxValue);
//            sort.sort(arr, 0,arr.length - 1);
            sort.sort2(arr);
            int min = arr[0];
            List<int[]> list = new ArrayList<>();
            for (int a : arr) {
                if (a < min) {
                    list.add(arr);
                    System.out.println("oops !!!");
                }
            }
            for (int[] a : list) {
                System.out.println(Arrays.toString(a));
            }
        }
    }

    private static int[] randomArray(int maxLen, int maxValue) {
        int len = (int) (Math.random() * maxLen) + 1;
        int[] arr = new int[len];
        for (int i = 0; i < len; i++) {
            arr[i] = (int) (Math.random() * maxValue) + 1;
        }
        return arr;
    }

    public void sort(int[] arr, int l, int r) {
        // 递归出口
        if (l == r) {
            return;
        }

        int mid = (l + r) / 2;
        sort(arr, l, mid);
        sort(arr, mid + 1, r);
        merge(arr, l, mid, r);
    }

    private void merge(int[] arr, int l, int m, int r) {
        int[] help = new int[r - l + 1];
        int i = 0;
        int p1 = l;
        int p2 = m + 1;// 合并右边的第一个数

        while (p1 <= m && p2 <= r) {
            help[i++] = arr[p1] < arr[p2] ? arr[p1++] : arr[p2++];
        }

        while (p1 <= m) {
            help[i++] = arr[p1++];
        }
        while (p2 <= r) {
            help[i++] = arr[p2++];
        }

        for (int j = 0; j < help.length; j++) {
            arr[l + j] = help[j];
        }
    }

    public void sort2(int[] arr) {
        if (null == arr || arr.length < 2) {
            return;
        }

        int step = 1;
        int N = arr.length;

        while (step < N) {
            int L = 0;
            while (L < N) {
                int M = L + step - 1; // 左边最后一个数
                if (M >= N) { // 右边没有数，则不需要继续
                    break;
                }
                // 右边的步长不够时，R的 为 N - 1;
                // 相当于 R = M + step >= N ? : N-1;
                int R = M + Math.min(step, N - (M + 1));
                merge(arr, L, M, R);
                L = R + 1;
            }
            // 防止 N 靠近Integer.MAX_VALUE时，step << 1 变为负数
            // 如果step >= N/2 则执行完step << 1 也不会进入最外面的 While循环
            if (step > N / 2) {
                break;
            }
            step <<= 1;
        }
    }
}
