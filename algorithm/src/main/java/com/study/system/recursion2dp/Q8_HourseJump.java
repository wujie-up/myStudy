package com.study.system.recursion2dp;

/**
 * @description: 走马问题
 * @author: wj2wml@qq.com
 * @date: 2021-06-18 23:43
 **/
public class Q8_HourseJump {

    public static int jumpKinds1(int x, int y, int k) {
        if (x < 0 || y < 0 || x > 8 || y > 9 || k < 0) {
            return 0;
        }
        return jump1(0, 0, x, y, k);
    }

    // (a,b) 当前来到的位置
    // (x, y) 目标位置
    // rest 还剩余的步数
    public static int jump1(int a, int b, int x, int y, int rest) {
        // 越界情况
        if (a < 0 || b < 0 || a > 8 || b > 9) {
            return 0;
        }
        // 没用步数可走时，只有当前位置是目标位置，这个路线方法才算有效
        if (rest == 0) {
            return a == x && b == y ? 1 : 0;
        }
        // 还有步数可以走，则分别往8个方向取尝试
        int ways = jump1(a + 1, b + 2, x, y, rest - 1);
        ways += jump1(a + 2, b + 1, x, y, rest - 1);
        ways += jump1(a + 2, b - 1, x, y, rest - 1);
        ways += jump1(a + 1, b - 2, x, y, rest - 1);
        ways += jump1(a - 1, b - 2, x, y, rest - 1);
        ways += jump1(a - 2, b - 1, x, y, rest - 1);
        ways += jump1(a - 2, b + 1, x, y, rest - 1);
        ways += jump1(a - 1, b + 2, x, y, rest - 1);
        return ways;
    }


    public static int jumpKinds2(int a, int b, int k) {
        if (a < 0 || b < 0 || a > 8 || b > 9 || k < 0) {
            return 0;
        }
        int[][][] dp = new int[9][10][k + 1];
//        if (rest == 0) {
//            return a == x && b == y ? 1 : 0;
//        }
        dp[a][b][0] = 1;
        for (int m = 1; m <= k; m++) {
            for (int i = 0; i < 9; i++) {
                for (int j = 0; j < 10; j++) {
                    int ways = getWays(dp, i + 1, j + 2, m - 1);
                    ways += getWays(dp, i + 2, j + 1, m - 1);
                    ways += getWays(dp, i + 2, j - 1, m - 1);
                    ways += getWays(dp, i + 1, j - 2, m - 1);
                    ways += getWays(dp, i - 1, j - 2, m - 1);
                    ways += getWays(dp, i - 2, j - 1, m - 1);
                    ways += getWays(dp, i - 2, j + 1, m - 1);
                    ways += getWays(dp, i - 1, j + 2, m - 1);
                    dp[i][j][m] = ways;
                }
            }
        }
        return dp[0][0][k];
    }

    public static int getWays(int[][][] dp, int a, int b, int rest) {
        if (a < 0 || b < 0 || a > 8 || b > 9) {
            return 0;
        }
        return dp[a][b][rest];
    }

    public static void main(String[] args) {
        int x = 7;
        int y = 7;
        int step = 10;
        System.out.println(jumpKinds1(x, y, step));
        System.out.println(jumpKinds2(x, y, step));
    }
}
