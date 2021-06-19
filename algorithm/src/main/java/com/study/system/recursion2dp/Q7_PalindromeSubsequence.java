package com.study.system.recursion2dp;


/**
 * @description: 最长回文子序列
 * @author: wj2wml@qq.com
 * @date: 2021-06-18 22:24
 **/
public class Q7_PalindromeSubsequence {

    public static int longestPalindromeSubseq1(String s) {
        if (null == s || s.length() == 0) {
            return 0;
        }
        char[] arr = s.toCharArray();
        return process1(arr, 0, arr.length - 1);
    }

    // arr 字符数组
    // 返回L-R中 回文子序列的个数
    public static int process1(char[] arr, int L, int R) {
        // 21ds12f
        // 还剩下一个的时候
        if (L == R) {
            return 1;
        }
        // 还剩下两个字符
        if (L == R - 1) {
            return arr[L] == arr[R] ? 2 : 1;
        }

        // 1、L 位置肯定不在回文序列中
        int p1 = process1(arr, L + 1, R);
        // 2、R 位置肯定不在回文序列中
        int p2 = process1(arr, L, R - 1);
        // 3、L R 都不再回文序列中
        int p3 = process1(arr, L + 1, R - 1);
        // 4、L R 都在回文序列中
        int p4 = arr[L] == arr[R] ? 2 + process1(arr, L + 1, R - 1) : 0;
        return Math.max(Math.max(p1, p2), Math.max(p3, p4));
    }


    public static int longestPalindromeSubseq2(String s) {
        if (null == s || s.length() == 0) {
            return 0;
        }
        char[] arr = s.toCharArray();
        int N = arr.length;
        int[][] dp = new int[N][N];

        dp[N - 1][N - 1] = 1;
        for (int L = 0; L < N - 1; L++) {
            // L == R 时，等于1
            dp[L][L] = 1;
            // L == R-1 时
            dp[L][L + 1] = arr[L] == arr[L + 1] ? 2 : 1;
        }

        for (int L = N - 3; L >= 0; L--) {
            for (int R = L + 2; R < N; R++) {
                int p1 = dp[L + 1][R];
                int p2 = dp[L][R - 1];
                int p3 = dp[L + 1][R - 1];
                int p4 = arr[L] == arr[R] ? 2 + dp[L + 1][R - 1] : 0;
                dp[L][R] = Math.max(Math.max(p1, p2), Math.max(p3, p4));
            }
        }
        return dp[0][N - 1];
    }

    public static int longestPalindromeSubseq3(String s) {
        if (null == s || s.length() == 0) {
            return 0;
        }
        char[] arr = s.toCharArray();
        int N = arr.length;
        int[][] dp = new int[N][N];

        dp[N - 1][N - 1] = 1;
        for (int L = 0; L < N - 1; L++) {
            // L == R 时，等于1
            dp[L][L] = 1;
            // L == R-1 时
            dp[L][L + 1] = arr[L] == arr[L + 1] ? 2 : 1;
        }

        for (int L = N - 3; L >= 0; L--) {
            for (int R = L + 2; R < N; R++) {
                dp[L][R] = Math.max(dp[L + 1][R], dp[L][R - 1]);
                if (arr[L] == arr[R]) {
                    dp[L][R] = Math.max(dp[L][R], 2 + dp[L + 1][R - 1]);
                }
            }
        }
        return dp[0][N - 1];
    }
}
