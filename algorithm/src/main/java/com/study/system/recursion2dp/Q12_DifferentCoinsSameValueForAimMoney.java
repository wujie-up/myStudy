package com.study.system.recursion2dp;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * @description: 用不同货币凑目标钱数 (面值相同的货币，认为是一样的)
 * @author: wj2wml@qq.com
 * @date: 2021-06-21 22:14
 **/
public class Q12_DifferentCoinsSameValueForAimMoney {

    public static int ways1(int[] arr, int aim) {
        if (null == arr || arr.length == 0 || aim < 0) {
            return 0;
        }

        // 货币面值 - 张数
        Map<Integer, Integer> coinMap = new HashMap<>();
        for (int coin : arr) {
            if (coinMap.containsKey(coin)) {
                coinMap.put(coin, coinMap.get(coin) + 1);
            } else {
                coinMap.put(coin, 1);
            }
        }

        Integer[] coins = coinMap.keySet().toArray(new Integer[]{});
        return process1(coinMap, coins, 0, aim);
    }

    // index  当前来到的位置
    // rest   剩下的钱数
    public static int process1(Map<Integer, Integer> coinMap, Integer[] arr, int index, int rest) {
        if (index == arr.length) {
            // 已经用完所有的货币，剩下的钱数 刚好为0，则为一种有效方法
            return rest == 0 ? 1 : 0;
        }
        int ways = 0;
        // 当前货币的张数
        int count = coinMap.get(arr[index]);
        for (int zhang = 0; zhang * arr[index] <= rest && zhang <= count; zhang++) {
            ways += process1(coinMap, arr, index + 1, rest - zhang * arr[index]);
        }
        return ways;
    }

    public static int ways2(int[] arr, int aim) {
        if (null == arr || arr.length == 0 || aim < 0) {
            return 0;
        }
        // 货币面值 - 张数
        Map<Integer, Integer> coinMap = new HashMap<>();
        for (int coin : arr) {
            if (coinMap.containsKey(coin)) {
                coinMap.put(coin, coinMap.get(coin) + 1);
            } else {
                coinMap.put(coin, 1);
            }
        }
        Integer[] coins = coinMap.keySet().toArray(new Integer[]{});

        int N = coins.length;
        int[][] dp = new int[N + 1][aim + 1];

        dp[N][0] = 1;

        for (int index = N - 1; index >= 0; index--) {
            for (int rest = 0; rest <= aim; rest++) {
                int ways = 0;
                // 当前货币的张数
                int count = coinMap.get(coins[index]);
                for (int zhang = 0; zhang * coins[index] <= rest && zhang <= count; zhang++) {
                    ways += dp[index + 1][rest - zhang * coins[index]];
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
        // 货币面值 - 张数
        Map<Integer, Integer> coinMap = new HashMap<>();
        for (int coin : arr) {
            if (coinMap.containsKey(coin)) {
                coinMap.put(coin, coinMap.get(coin) + 1);
            } else {
                coinMap.put(coin, 1);
            }
        }
        Integer[] coins = coinMap.keySet().toArray(new Integer[]{});

        int N = coins.length;
        int[][] dp = new int[N + 1][aim + 1];

        dp[N][0] = 1;

        for (int index = N - 1; index >= 0; index--) {
            for (int rest = 0; rest <= aim; rest++) {
                dp[index][rest] = dp[index + 1][rest];
                if (rest - coins[index] >= 0) {
                    dp[index][rest] += dp[index][rest - coins[index]];
                }
                int r = rest - (coinMap.get(coins[index]) + 1) * coins[index];
                if (r >= 0) {
                    dp[index][rest] -= dp[index + 1][r];
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
            if (w1 != w2 || w1 != w3) {
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
