package com.study.system.recursion2dp;

/**
 * @description: 最长公共子序列
 * @author: wj2wml@qq.com
 * @date: 2021-06-17 22:04
 **/
public class Q6_LongestCommonSubsequence {

    public static int longestCommonSubsequence1(String str1, String str2) {
        if (null == str1 || str1.length() == 0 || null == str2 || str2.length() == 0) {
            return 0;
        }
        // 得到str1[0...i] str2[0...j]上的公共子序列
        char[] c1 = str1.toCharArray();
        char[] c2 = str2.toCharArray();
        return process1(c1, c2, c1.length - 1, c2.length - 1);
    }

    private static int process1(char[] c1, char[] c2, int i, int j) {
        if (i == 0 && j == 0) {
            return c1[i] == c2[i] ? 1 : 0;
        }
        if (i == 0) {
            if (c1[i] == c2[j]) {
                return 1;
            } else {
                return process1(c1, c2, i, j - 1);
            }
        }

        if (j == 0) {
            if (c1[i] == c2[j]) {
                return 1;
            } else {
                return process1(c1, c2, i - 1, j);
            }
        }

        // 中间位置情况分析：
        // 1、c1[i] 位置不可能是 [0..i]上公共子序列中的末尾
        int p1 = process1(c1, c2, i - 1, j);
        // 2、c2[j] 位置不可能是 [0..j]公共子序列中的末尾
        int p2 = process1(c1, c2, i, j - 1);
        // 3、c1[i] c2[j] 都是 公共子序列中末尾
        int p3 = c1[i] == c2[j] ? 1 + process1(c1, c2, i - 1, j - 1) : 0;

        return Math.max(p1, Math.max(p2, p3));
    }


    public static int longestCommonSubsequence2(String str1, String str2) {
        if (null == str1 || str1.length() == 0 || null == str2 || str2.length() == 0) {
            return 0;
        }
        char[] c1 = str1.toCharArray();
        char[] c2 = str2.toCharArray();
        int N = str1.length();
        int M = str2.length();

        int[][] dp = new int[N][M];

//        if (i == 0 && j == 0) {
//            return c1[i] == c2[i] ? 1 : 0;
//        }
        dp[0][0] = c1[0] == c2[0] ? 1 : 0;

//        if (i == 0) {
//            if (c1[i] == c2[j]) {
//                return 1;
//            } else {
//                return process1(c1, c2, i, j - 1);
//            }
//        }
        // i == 0
        for (int j = 1; j < M; j++) {
            dp[0][j] = c1[0] == c2[j] ? 1 : dp[0][j-1];
        }

//        if (j == 0) {
//            if (c1[i] == c2[j]) {
//                return 1;
//            } else {
//                return process1(c1, c2, i - 1, j);
//            }
//        }
        // j == 0
        for (int i = 1; i < N; i++) {
            dp[i][0] = c1[i] == c2[0] ? 1 : dp[i-1][0];
        }

//        int p1 = process1(c1, c2, i - 1, j);
//        int p2 = process1(c1, c2, i, j - 1);
//        int p3 = c1[i] == c2[j] ? 1 + process1(c1, c2, i - 1, j - 1) : 0;
//        return Math.max(p1, Math.max(p2, p3));
        for (int i = 1; i < N; i++) {
            for (int j = 1; j < M; j++) {
                int p1 = dp[i-1][j];
                int p2 = dp[i][j-1];
                int p3 = c1[i] == c2[j] ? 1+ dp[i-1][j-1] : 0;
                dp[i][j] =  Math.max(p1, Math.max(p2, p3));
            }
        }

        return dp[N-1][M-1];
    }
}
