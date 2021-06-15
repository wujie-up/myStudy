package com.study.system.recursion2dp;

import com.sun.org.apache.bcel.internal.generic.IF_ACMPEQ;

/**
 * @description: 抽牌问题
 * @author: wj2wml@qq.com
 * @date: 2021-06-14 17:07
 **/
public class Q2_CardPick {

    public static int maxScore1(int[] arr) {
        if (null == arr || arr.length == 0) {
            return 0;
        }
        int first = f1(arr, 0, arr.length - 1);
        int second = g1(arr, 0, arr.length - 1);
        return Math.max(first, second);
    }

    // 先手拿牌, 获得的最好分数
    private static int f1(int[] arr, int L, int R) {
        if (L == R) {
            return arr[L];
        }
        // 拿左
        int p1 = arr[L] + g1(arr, L + 1, R);
        // 拿右
        int p2 = arr[R] + g1(arr, L, R - 1);
        // 先手肯定的拿最大
        return Math.max(p1, p2);
    }

    // 后手拿牌，获得的最好分数
    private static int g1(int[] arr, int L, int R) {
        if (L == R) {
            // L/R 已经被先手拿了，没牌了
            return 0;
        }
        // 先手拿走了L位置的牌, 则在[L-1,R]上，后手 成为了 先手
        int p1 = f1(arr, L + 1, R);
        int p2 = f1(arr, L, R - 1);
        // 先手肯定会把最小的 留给后手，所以返回min
        return Math.min(p1, p2);
    }

    public static int maxScore2(int[] arr) {
        if (null == arr || arr.length == 0) {
            return 0;
        }
        int N = arr.length;
        int[][] fmap = new int[N][N];
        int[][] gmap = new int[N][N];
        for (int i = 0; i < N; i++) {
            for (int j = 0; j < N; j++) {
                fmap[i][j] = -1;
                gmap[i][j] = -1;
            }
        }
        int first = f2(arr, 0, N - 1, fmap, gmap);
        int second = g2(arr, 0, N - 1, fmap, gmap);
        return Math.max(first, second);
    }

    private static int f2(int[] arr, int L, int R, int[][] fmap, int[][] gmap) {
        if (fmap[L][R] != -1) {
            return fmap[L][R];
        }
        int ans = 0;
        if (L == R) {
            ans = arr[L];
        } else {
            int p1 = arr[L] + g2(arr, L + 1, R, fmap, gmap);
            int p2 = arr[R] + g2(arr, L, R - 1, fmap, gmap);
            ans = Math.max(p1, p2);
        }
        fmap[L][R] = ans;
        return ans;
    }

    private static int g2(int[] arr, int L, int R, int[][] fmap, int[][] gmap) {
        if (gmap[L][R] != -1) {
            return gmap[L][R];
        }
        int ans = 0;
        if (L == R) {
            ans = 0;
        } else {
            int p1 = f2(arr, L + 1, R, fmap, gmap);
            int p2 = f2(arr, L, R - 1, fmap, gmap);
            ans = Math.min(p1, p2);
        }
        gmap[L][R] = ans;
        return ans;
    }

    public static int maxScore3(int[] arr) {
        if (null == arr || arr.length == 0) {
            return 0;
        }
        int N = arr.length;
        int[][] fmap = new int[N][N];
        int[][] gmap = new int[N][N];
        // 初始化对角线数据, gmap[i][i] 默认就是0，不需要初始化
        for (int i = 0; i < N; i++) {
            fmap[i][i] = arr[i];
        }

        // 填充其他位置, 需要填充的对角线 为  1-(N-1)  位置
        for (int startCol = 1; startCol < N; startCol++) {
            int L = 0; // 行
            int R = startCol; // 列
            while (R < N) { // 对角线中，列最先越界

                // 根据暴力递归函数，改动态规划表
//                int p1 = arr[L] + g1(arr, L + 1, R);
//                int p2 = arr[R] + g1(arr, L, R - 1);
//                return Math.max(p1, p2);
                fmap[L][R] = Math.max(arr[L] + gmap[L+1][R], arr[R] + gmap[L][R-1]);

//                int p1 = f1(arr, L + 1, R);
//                int p2 = f1(arr, L, R - 1);
//                return Math.min(p1, p2);
                gmap[L][R] = Math.min(fmap[L+1][R], fmap[L][R-1]);

                // 行、列++
                L++;
                R++;
            }
        }

        // 返回结果，根据递归函数修改
//        int first = f1(arr, 0, arr.length - 1);
//        int second = g1(arr, 0, arr.length - 1);
//        return Math.max(first, second);
        return Math.max(fmap[0][N-1], gmap[0][N-1]);
    }

    public static void main(String[] args) {
        int[] arr = {1, 10, 6, 5, 100, 88, 7, 66};
        System.out.println(maxScore1(arr));
        System.out.println(maxScore2(arr));
        System.out.println(maxScore3(arr));
    }

}
