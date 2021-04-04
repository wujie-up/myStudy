package com.study.system.merge;

import java.util.Arrays;

public class ReversePair {
    public static void main(String[] args) {
        int maxLen = 2;
        int maxValue = 6000;
        int times = 600000;
        for (int i = 0; i < times; i++) {
            int[] arr = randomArr(maxLen, maxValue);
            int[] copy = copyArr(arr);
            int sum1 = test(copy);
            int sum2 = process(arr, 0, arr.length - 1);
            if (sum1 !=sum2) {
                System.out.println("oops!!!");
                System.out.println(Arrays.toString(copy));
                System.out.println(Arrays.toString(arr));
                break;
            }
        }
    }

    private static int test(int[] arr) {
        int ans = 0;
        for (int i = 0; i < arr.length; i++) {
            for (int j = i + 1; j < arr.length; j++) {
                if (arr[i] > arr[j]) {
                    ans += 1;
                }
            }
        }
        return ans;
    }

    private static int[] copyArr(int[] arr) {
        int[] cp = new int[arr.length];
        for (int i = 0; i < arr.length; i++) {
            cp[i] = arr[i];
        }
        return cp;
    }

    private static int[] randomArr(int maxLen, int maxValue) {
        int len = (int)(Math.random() * maxLen) + 2;
        int[] arr = new int[len];
        for (int i = 0; i < len; i++) {
            arr[i] = (int) (Math.random() * maxValue);
        }
        return arr;
    }


    public static int process(int[] arr, int l, int r) {
        if (l == r) {
            return 0;
        }

        int m = (l + r) / 2;
        return process(arr, l, m) + process(arr, m + 1, r) + merge(arr, l, m, r);
    }

    private static int merge(int[] arr, int l, int m, int r) {
        int[] help = new int[r - l + 1];
        int p1 = m;
        int p2 = r;
        int ans = 0;
        int i = help.length - 1;

        while (p1 >= l && p2 > m) {
            ans += arr[p1] > arr[p2] ? (p2 - m) : 0;
            help[i--] = arr[p1] > arr[p2] ? arr[p1--] : arr[p2--];
        }

        while (p1 >= l) {
            help[i--] = arr[p1--];
        }

        while (p2 > m) {
            help[i--] = arr[p2--];
        }

        for (int j = 0; j < help.length; j++) {
            arr[l + j] = help[j];
        }

        return ans;
    }
}
