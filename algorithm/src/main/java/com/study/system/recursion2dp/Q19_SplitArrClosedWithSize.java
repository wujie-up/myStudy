package com.study.system.recursion2dp;

/**
 * @description: 分割数组，使其个数和值都最接近
 * @author: wj2wml@qq.com
 * @date: 2021-07-04 22:26
 **/
public class Q19_SplitArrClosedWithSize {

    public static int split(int[] arr) {
        if (null == arr && arr.length < 2) {
            return 0;
        }
        int N = arr.length;
        int sum = 0;
        for (int a : arr) {
            sum += a;
        }
        if ((N & 1) == 0) {
            return process(arr, 0, N / 2, sum / 2);
        } else {
            return Math.max(process(arr, 0, N / 2, sum / 2), process(arr, 0, N / 2 + 1, sum / 2));
        }
    }

    /**
     * @param arr
     * @param index 当前位置
     * @param restSize 剩余个数
     * @param rest  剩余的数值
     */
    private static int process(int[] arr, int index, int restSize, int rest) {
        if (index == arr.length) {
            return restSize == 0 ? 0 : -1;
        }
        int p1 = process(arr, index + 1, restSize, rest);
        int p2 = -1;
        if (arr[index] <= rest) {
            p2 = process(arr, index + 1, restSize - 1, rest - arr[index]);
        }
        if (p2 != -1) {
            p2 += arr[index];
        }
        return Math.max(p1, p2);
    }

    public static int dp1(int[] arr) {
        if (null == arr && arr.length < 2) {
            return 0;
        }
        int N = arr.length;
        int sum = 0;
        for (int a : arr) {
            sum += a;
        }
        int size = (N + 1) / 2;
        sum /= 2;

        int[][][] dp = new int[N + 1][size + 1][sum + 1];

        for (int index = 0; index <= N; index++) {
            for (int num = 0; num <= size; num++) {
                for (int rest = 0; rest <= sum; rest++) {
                    dp[index][num][rest] = -1;
                }
            }
        }

        for (int i = 0; i <= sum; i++) {
            dp[N][0][i] = 0;
        }


        for (int index = N - 1; index >= 0; index--) {
            for (int num = 0; num <= size; num++) {
                for (int rest = 0; rest <= sum; rest++) {
                    int p1 = dp[index + 1][num][rest];
                    int p2 = -1;
                    if (num > 0 && arr[index] <= rest) {
                        p2 = dp[index + 1][num - 1][rest - arr[index]];
                    }
                    if (p2 != -1) {
                        p2 += arr[index];
                    }
                    dp[index][num][rest] = Math.max(p1, p2);
                }
            }
        }

        if ((N & 1) == 0) {
            return dp[0][N / 2][sum];
        } else {
            return Math.max(dp[0][N/2][sum], dp[0][N/2 + 1][sum]);
        }
    }

    public static void main(String[] args) {
        int maxLen = 20;
        int maxValue = 500;
        int times = 30000;
        for (int i = 0; i < times; i++) {
            int[] arr = makeArr(maxLen, maxValue);
            int v1 = split(arr);
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
            arr[i] = (int) (Math.random() * maxValue) + 1;
        }
        return arr;
    }
}
