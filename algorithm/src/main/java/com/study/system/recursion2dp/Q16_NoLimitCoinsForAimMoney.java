package com.study.system.recursion2dp;

/**
 * @description: 无数不重复货币组成目标钱数 的最少张数
 * @author: wj2wml@qq.com
 * @date: 2021-06-29 22:21
 **/
public class Q16_NoLimitCoinsForAimMoney {

    public static int min1(int[] arr, int aim) {
        if (null == arr || arr.length == 0 || aim < 0) {
            return 0;
        }
        return process1(arr, 0, aim);
    }

    /**
     * @param arr
     * @param index 当前来到的货币下标
     * @param rest  剩余的钱数
     */
    private static int process1(int[] arr, int index, int rest) {
        if (index == arr.length) {
            return rest == 0 ? 0 : Integer.MAX_VALUE;
        }
        int min = Integer.MAX_VALUE;
        // 当前位置的货币 可以用 0 ~ rest/arr[index] 张
        for (int zhang = 0; zhang * arr[index] <= rest; zhang++) {
            int next = process1(arr, index + 1, rest - zhang * arr[index]);
            if (next != Integer.MAX_VALUE) {
                min = Math.min(min, next + zhang);
            }
        }
        return min;
    }

    public static int dp1(int[] arr, int aim) {
        if (null == arr || arr.length == 0 || aim < 0) {
            return 0;
        }
        int N = arr.length;
        int[][] dp = new int[N + 1][aim + 1];
        for (int i = 1; i <= aim; i++) {
            dp[N][i] = Integer.MAX_VALUE;
        }
        for (int index = N - 1; index >= 0; index--) {
            for (int rest = 1; rest <= aim; rest++) {
                int min = Integer.MAX_VALUE;
                for (int zhang = 0; zhang * arr[index] <= rest; zhang++) {
                    int next = dp[index + 1][rest - zhang * arr[index]];
                    if (next != Integer.MAX_VALUE) {
                        min = Math.min(min, next + zhang);
                    }
                }
                dp[index][rest] = min;
            }
        }
        return dp[0][aim];
    }

    public static int dp2(int[] arr, int aim) {
        if (null == arr || arr.length == 0 || aim < 0) {
            return 0;
        }
        int N = arr.length;
        int[][] dp = new int[N + 1][aim + 1];
        for (int i = 1; i <= aim; i++) {
            dp[N][i] = Integer.MAX_VALUE;
        }
        for (int index = N - 1; index >= 0; index--) {
            for (int rest = 1; rest <= aim; rest++) {
                dp[index][rest] = dp[index + 1][rest];
                if (rest - arr[index] >= 0 && dp[index][rest - arr[index]] != Integer.MAX_VALUE) {
                    dp[index][rest] = Math.min(dp[index][rest], dp[index][rest - arr[index]] + 1);
                }
            }
        }
        return dp[0][aim];
    }

    public static void main(String[] args) {
        int arrMaxLen = 15;
        int maxValue = 20;
        int maxAim = 50;

        int times = 30000;
        for (int i = 0; i < times; i++) {
            int[] arr = makeArr(arrMaxLen, maxValue);
            int aim = (int) (Math.random() * maxAim);
            int m1 = min1(arr, aim);
            int m2 = dp1(arr, aim);
            int m3 = dp2(arr, aim);
            if (m1 != m2 || m1 != m3) {
                System.out.println("完犊子了！");
                break;
            }
        }
        System.out.println("success!!!");
    }

    private static int[] makeArr(int arrMaxLen, int maxValue) {
        int len = (int) (Math.random() * arrMaxLen);
        int[] arr = new int[len];
        boolean[] has = new boolean[maxValue + 1];
        for (int i = 0; i < len; i++) {
            do {
                arr[i] = (int) (Math.random() * maxValue) + 1;
            } while (has[arr[i]]);
            has[arr[i]] = true;
        }
        return arr;
    }
}
