package com.study.system.merge;

import java.util.Arrays;

public class BiggerThanRightTwice {
    public static void main(String[] args) {
        int maxLen = 30;
        int maxValue = 500;
        int times = 60002;
        System.out.println("测试开始");
        for (int i = 0; i < times; i++) {
            int[] arr = randomArr(maxLen, maxValue);
            int count1 = test(arr);
            int[] cpArr = copyArr(arr);
            int count2 = process(arr, 0, arr.length - 1);
            if (count1 != count2) {
                System.out.println(Arrays.toString(cpArr));
                System.out.println(Arrays.toString(arr));
                System.out.println("oops !!!");
                break;
            }
        }
        System.out.println("测试结束");
    }

    private static int[] copyArr(int[] arr) {
        int[] cpArr = new int[arr.length];
        for (int i = 0; i < arr.length; i++) {
            cpArr[i] = arr[i];
        }
        return cpArr;
    }

    private static int test(int[] arr) {
        int ans = 0;
        for (int i = 0; i < arr.length; i++) {
            for (int j = i + 1; j < arr.length; j++) {
                if (arr[i] > arr[j] * 2) {
                    ans++;
                }
            }
        }
        return ans;
    }

    private static int[] randomArr(int maxLen, int maxValue) {
        int len = (int) (Math.random() * maxLen) + 2;
        int[] arr = new int[len];
        for (int i = 0; i < len; i++) {
            arr[i] = (int) (Math.random() * maxValue) + 1;
        }
        return arr;
    }

    public static int process(int[] arr, int L, int R) {
        if (L == R) {
            return 0;
        }
        int M = (L + R) / 2;
        return process(arr, L, M) + process(arr, M + 1, R) + merge(arr, L, M, R);
    }

    private static int merge(int[] arr, int l, int m, int r) {
        int ans = 0;
        int windowR = m + 1; // 记录右边指针

        // 遍历左边的数
        for (int i = l; i <= m; i++) {
            while (windowR <= r && (arr[i] > arr[windowR] * 2)) {
                windowR++;
            }
            ans += windowR - m - 1;
        }

        int[] help = new int[r - l + 1];
        int p1 = l;
        int p2 = m + 1;
        int i = 0;
        while (p1 <= m && p2 <= r) {
            help[i++] = arr[p1] <= arr[p2] ? arr[p1++] : arr[p2++];
        }
        while (p1 <= m) {
            help[i++] = arr[p1++];
        }
        while (p2 <= r) {
            help[i++] = arr[p2++];
        }

        for (int k = 0; k < help.length; k++) {
            arr[l + k] = help[k];
        }
        return ans;
    }
}
