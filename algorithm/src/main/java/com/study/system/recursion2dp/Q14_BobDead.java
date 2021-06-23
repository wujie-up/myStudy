package com.study.system.recursion2dp;

/**
 * @description: 醉汉死亡概率
 * @author: wj2wml@qq.com
 * @date: 2021-06-23 19:59
 **/
public class Q14_BobDead {
    public static double die1(int N, int M, int row, int col, int K) {
        if (N <= 0 || M <= 0 || K < 0) {
            return 0;
        }

        return process1(N, M, row, col, K) / Math.pow(K, 4);
    }


    public static double process1(int N, int M, int x, int y, int rest) {
        if (x == N || x < 0 || y < 0 || y == M) {
            return 0;
        }
        if (rest == 0) {
            return 1;
        }
        // 可以往四个方向走
        int life = 0;
        life += process1(N, M, x + 1, y, rest - 1);
        life += process1(N, M, x - 1, y, rest - 1);
        life += process1(N, M, x, y - 1, rest - 1);
        life += process1(N, M, x, y + 1, rest - 1);
        return life;
    }

    public static double die2(int N, int M, int row, int col, int K) {
        if (N <= 0 || M <= 0 || K < 0) {
            return 0;
        }
        int[][][] dp = new int[N][M][K + 1];

        for (int x = 0; x < N; x++) {
            for (int y = 0; y < M; y++) {
                dp[x][y][0] = 1;
            }
        }
        // 注意注意注意： rest层的循环，一定要放在最外层，rest + 1层依赖于整个rest层
        for (int rest = 1; rest <= K; rest++) {
            for (int x = 0; x < N; x++) {
                for (int y = 0; y < M; y++) {
                    dp[x][y][rest] = getLife(dp, x + 1, y, rest - 1, N, M);
                    dp[x][y][rest] += getLife(dp, x - 1, y, rest - 1, N, M);
                    dp[x][y][rest] += getLife(dp, x, y + 1, rest - 1, N, M);
                    dp[x][y][rest] += getLife(dp, x, y - 1, rest - 1, N, M);
                }
            }
        }

        return dp[row][col][K] / Math.pow(K, 4);
    }

    private static int getLife(int[][][] dp, int x, int y, int rest, int N, int M) {
        if (x < 0 || x == N || y < 0 || y == M) {
            return 0;
        }
        return dp[x][y][rest];
    }

    public static void main(String[] args) {
        int maxNum = 10;
        int maxK = 5;
        int times = 20000;
        for (int i = 0; i < times; i++) {
            int N = (int) (Math.random() * maxNum);
            int M = (int) (Math.random() * maxNum);
            int row = (int) (Math.random() * N);
            int col = (int) (Math.random() * M);
            int K = (int) (Math.random() * maxK);
            double d1 = die1(N, M, row, col, K);
            double d2 = die2(N, M, row, col, K);
            if (d1 != d2) {
                System.out.println("完犊子了!" + d1 + " " + d2);
                break;
            }
        }
        System.out.println("success!!!");
    }
}
