package com.study.system.recursion2dp;

/**
 * @description: 分隔数组，使其接近
 * @author: wj2wml@qq.com
 * @date: 2021-07-04 20:32
 **/
public class Q18_SplitArrrClosed {

    public static int splitMin(int[] arr) {
        if (null == arr || arr.length < 2) {
            return 0;
        }

        int count = 0;
        int N = arr.length;
        for (int i = 0; i < N; i++) {
            count += arr[i];
        }

        return process(arr, 0, count / 2);
    }

    /**
     * @param arr
     * @param index 当前位置
     * @param rest  剩余数
     */
    private static int process(int[] arr, int index, int rest) {
        if (index == arr.length) {
            return 0;
        }
        // 当前位置 要 或不要
        int p1 = process(arr, index + 1, rest);
        int p2 = 0;
        if (arr[index] <= rest) {
            p2 = arr[index] + process(arr, index + 1, rest - arr[index]);
        }
        return Math.max(p1, p2);
    }

    public static int dp1(int[] arr) {
        if (null == arr || arr.length < 2) {
            return 0;
        }
        int count = 0;
        int N = arr.length;
        for (int i = 0; i < N; i++) {
            count += arr[i];
        }

        count = count / 2;
        int[][] dp = new int[N + 1][count + 1];

        for (int index = N - 1; index >= 0; index--) {
            for (int rest = 0; rest <= count; rest++) {
                int p1 = dp[index + 1][rest];
                int p2 = 0;
                if (arr[index] <= rest) {
                    p2 = arr[index] + dp[index + 1][rest - arr[index]];
                }
                dp[index][rest] = Math.max(p1, p2);
            }
        }
        return dp[0][count];
    }


    public static void main(String[] args) {
        int maxLen = 20;
        int maxValue = 500;
        int times = 50000;
        for (int i = 0; i < times; i++) {
            int[] arr = makeArr(maxLen, maxValue);
            int v1 = splitMin(arr);
            int v2 = dp1(arr);
            if (v1 != v2) {
                System.out.println("完犊子了！");
                break;
            }
        }
        System.out.println("success!!!");
    }

    private static int[] makeArr(int maxLen, int maxValue) {
        int len = (int) (Math.random() * maxLen);
        int[] arr = new int[len];
        for (int i = 0; i < len; i++) {
            arr[i] = (int)(Math.random() * maxValue) + 1;
        }
        return arr;
    }

}
