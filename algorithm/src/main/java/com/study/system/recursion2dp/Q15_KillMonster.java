package com.study.system.recursion2dp;

/**
 * @description: 杀死怪物
 * @author: wj2wml@qq.com
 * @date: 2021-06-29 21:56
 **/
public class Q15_KillMonster {
    public static double kill1(int N, int M, int K) {
        if (N < 1 || M < 1 || K < 1) {
            return 0;
        }
        return process1(N, M, K) / Math.pow(M + 1, K);
    }

    private static double process1(int hp, int M, int rest) {
        if (rest == 0) {
            return hp > 0 ? 0 : 1;
        }
        if (hp <= 0) {
            return Math.pow(M + 1, rest);
        }
        int ways = 0;
        for (int i = 0; i <= M; i++) {
            ways += process1(hp - i, M, rest - 1);
        }
        return ways;
    }

    public static double dp1(int N, int M, int K) {
        if (N < 1 || M < 1 || K < 1) {
            return 0;
        }
        double[][] dp = new double[N + 1][K + 1];
        dp[0][0] = 1;
        for (int rest = 1; rest <= K; rest++) {
            dp[0][rest] = Math.pow(M + 1, rest);
            for (int hp = 1; hp <= N; hp++) {
                int ways = 0;
                for (int i = 0; i <= M; i++) {
                    if (hp - i <= 0) {
                        ways += dp[0][rest - 1];
                    } else {
                        ways += dp[hp - i][rest - 1];
                    }
                }
                dp[hp][rest] = ways;
            }
        }
        return dp[N][K] / Math.pow(M + 1, K);
    }

    public static double dp2(int N, int M, int K) {
        if (N < 1 || M < 1 || K < 1) {
            return 0;
        }
        double[][] dp = new double[N + 1][K + 1];
        dp[0][0] = 1;
        for (int rest = 1; rest <= K; rest++) {
            dp[0][rest] = Math.pow(M + 1, rest);
            for (int hp = 1; hp <= N; hp++) {
                dp[hp][rest] = dp[hp - 1][rest] + dp[hp][rest - 1];
                if (hp - M - 1 > 0) {
                    dp[hp][rest] -= dp[hp - M - 1][rest - 1];
                } else {
                    // 当 hp - M -1 <= 0, 依然要减去多算的 一次 0位置
                    dp[hp][rest] -= dp[0][rest - 1];
                }
            }
        }
        return dp[N][K] / Math.pow(M + 1, K);
    }

    public static void main(String[] args) {
        int N = (int) (Math.random() * 25);
        int M = (int) (Math.random() * 8);
        int K = (int) (Math.random() * 8);
        int times = 80000;
        for (int i = 0; i < times; i++) {
            double d1 = kill1(N, M, K);
            double d2 = dp1(N, M, K);
            double d3 = dp2(N, M, K);
            if (d1 != d2 || d1 != d3) {
                System.out.println("完犊子了!");
                break;
            }
        }
        System.out.println("success!!!");
    }
}
