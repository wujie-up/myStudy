package com.study.system.recursion2dp;

/**
 * @description: 用无限张不同货币凑目标钱数
 * @author: wj2wml@qq.com
 * @date: 2021-06-21 22:14
 **/
public class Q13_UnlimitedDifferentCoinsForAimMoney {

    public static int ways1(int[] arr, int aim) {
        if (null == arr || arr.length == 0 || aim < 0) {
            return 0;
        }
        return process1(arr, 0, aim);
    }

    // arr[]  货币数组
    // index  当前来到的位置
    // rest   剩下的钱数
    public static int process1(int[] arr, int index, int rest) {
        if (index == arr.length) {
            // 已经用完所有的货币，剩下的钱数 刚好为0，则为一种有效方法
            return rest == 0 ? 1 : 0;
        }
        int ways = 0;
        // 当前 可以用的张数  [0 - rest/arr[index]]
        for (int zhang = 0; zhang * arr[index] <= rest; zhang++) {
            ways += process1(arr, index + 1, rest - arr[index] * zhang);
        }
        return ways;
    }

    public static int ways2(int[] arr, int aim) {
        if (null == arr || arr.length == 0 || aim < 0) {
            return 0;
        }
        int N = arr.length;
        int[][] dp = new int[N + 1][aim + 1];

        dp[N][0] = 1;

        for (int index = N - 1; index >= 0; index--) {
            for (int rest = 0; rest <= aim; rest++) {
                int ways = 0;
                for (int zhang = 0; zhang * arr[index] <= rest; zhang++) {
                    ways += dp[index + 1][rest - arr[index] * zhang];
                }
                dp[index][rest] = ways;
            }
        }
        return dp[0][aim];
    }

    public static int ways3(int[] arr, int aim) {
        if (null == arr || arr.length == 0 || aim < 0) {
            return 0;
        }
        int N = arr.length;
        int[][] dp = new int[N + 1][aim + 1];

        dp[N][0] = 1;

        for (int index = N - 1; index >= 0; index--) {
            for (int rest = 0; rest <= aim; rest++) {
                dp[index][rest] = dp[index + 1][rest];
                if (rest - arr[index] >= 0) {
                    dp[index][rest] += dp[index][rest - arr[index]];
                }
            }
        }
        return dp[0][aim];
    }


    public static void main(String[] args) {
        int maxLen = 10;
        int maxMoney = 5;
        int maxAim = 20;

        int times = 200000;
        for (int i = 0; i < times; i++) {
            int[] arr = makeArr(maxLen, maxMoney);
            int aim = (int) (Math.random() * maxAim);
            int w1 = ways1(arr, aim);
            int w2 = ways2(arr, aim);
            int w3 = ways3(arr, aim);
            if (w1 != w2 || w2 != w3) {
                System.out.println("完犊子了!!!");
                break;
            }
        }
        System.out.println("success!!!");
    }

    private static int[] makeArr(int maxLen, int maxMoney) {
        int len = (int) (Math.random() * maxLen) + 1;
        int[] arr = new int[len];
        for (int i = 0; i < len; i++) {
            arr[i] = (int) (Math.random() * maxMoney) + 1;
        }
        return arr;
    }
}
