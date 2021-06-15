package com.study.system.recursion2dp;

/**
 * @description: 机器人行走问题
 * @author: wj2wml@qq.com
 * @date: 2021-06-13 22:17
 **/
public class Q1_RobotWalk {

    public static Integer walkWays1(int N, int start, int target, int K) {
        if (N < 2 || start < 1 || start > N || target < 1 || target > N || K < 1) {
            return 0;
        }
        return walk1(N, start, target, K);
    }

    /**
     * @param N
     * @param cur    当前来到的位置
     * @param target 目标位置
     * @param rest   剩余步数
     */
    public static Integer walk1(int N, int cur, int target, int rest) {
        if (rest == 0) {
            // 步数为0，判断当前位置是否为target位置，是则返回1
            return cur == target ? 1 : 0;
        }
        // 只能往右走，1种选择
        if (cur == 1) {
            return walk1(N, cur + 1, target, rest - 1);
        }
        // 只能往左走，1种选择
        if (cur == N) {
            return walk1(N, cur - 1, target, rest - 1);
        }
        // 往左 往右走
        return walk1(N, cur - 1, target, rest - 1) + walk1(N, cur + 1, target, rest - 1);
    }


    public static Integer walkWays2(int N, int start, int target, int K) {
        if (N < 2 || start < 1 || start > N || target < 1 || target > N || K < 1) {
            return 0;
        }
        // dp存放所有 {当前位置，剩余步数} 的结果
        int[][] dp = new int[N + 1][K + 1];
        for (int i = 0; i <= N; i++) {
            for (int j = 0; j <= K; j++) {
                dp[i][j] = -1;
            }
        }
        // dp[m][n] == -1 表示{m,n}还没计算过
        return walk2(start, target, N, K, dp);
    }

    private static Integer walk2(int cur, int target, int N, int rest, int[][] dp) {
        if (dp[cur][rest] != -1) {
            // 已经计算过的，直接返回
            return dp[cur][rest];
        }
        int ans = 0;
        if (rest == 0) {
            // 步数为0，判断当前位置是否为target位置，是则返回1
            ans = cur == target ? 1 : 0;
        } else if (cur == 1) {
            ans = walk2(cur + 1, target, N, rest - 1, dp);
        } else if (cur == N) {
            ans = walk2(cur - 1, target, N, rest - 1, dp);
        } else {
            ans = walk2(cur - 1, target, N, rest - 1, dp)
                    + walk2(cur + 1, target, N, rest - 1, dp);
        }
        dp[cur][rest] = ans;
        return ans;
    }

    private static int walkWays3(int N, int start, int target, int K) {
        if (N < 2 || start < 1 || start > N || target < 1 || target > N || K < 1) {
            return 0;
        }

        int[][] dp = new int[N + 1][K + 1];
        // 只有[target, rest==0]位置等于1
        dp[target][0] = 1;
        // 从第 1 号列开始计算，每列每列的算
        for (int rest = 1; rest <= K; rest++) {
            // cur == 1，依赖 cur == 2, rest - 1
            dp[1][rest] = dp[2][rest - 1];
            for (int cur = 2; cur < N; cur++) {
                dp[cur][rest] = dp[cur - 1][rest - 1] + dp[cur + 1][rest - 1];
            }
            // cur == N，依赖 cur == N-1, rest - 1
            dp[N][rest] = dp[N - 1][rest - 1];
        }
        return dp[start][K];
    }


    public static void main(String[] args) {
        int N = 10;
        int M = 3;
        int P = 4;
        int K = 7;
        System.out.println(walkWays1(N, M, P, K));
        System.out.println(walkWays2(N, M, P, K));
        System.out.println(walkWays3(N, M, P, K));
    }


}
