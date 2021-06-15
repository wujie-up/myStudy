package com.study.system.recursion2dp;

/**
 * @description: 背包问题
 * @author: wj2wml@qq.com
 * @date: 2021-06-14 22:37
 **/
public class Q3_Backpack {

    public static int maxValue1(int[] weights, int[] values, int bag) {
        if (null == weights || null == values || weights.length == 0 || weights.length != values.length) {
            return 0;
        }
        return f1(weights, values, 0, bag);
    }

    // index 当前物品下标
    // rest  剩余的背包容量
    public static int f1(int[] weights, int[] values, int index, int rest) {
        // 表示上一次放入后，背包已经超重了，上一步的操作无效
        if (rest < 0) {
            return -1;
        }
        if (index == weights.length) {
            return 0;
        }
        // 当前位置要 或 不要
        int p1 = f1(weights, values, index + 1, rest);
        int p2 = 0;
        int v = f1(weights, values, index + 1, rest - weights[index]);
        if (v != -1) { // v == -1 说明背包超重了，此次装入物品操作无效
            p2 = values[index] + v;
        }
        return Math.max(p1, p2);
    }


    public static int maxValue2(int[] weights, int[] values, int bag) {
        if (null == weights || null == values || weights.length == 0 || weights.length != values.length) {
            return 0;
        }
        int N = weights.length;
        int[][] fmap = new int[N][bag + 1];
        for (int i = 0; i < N; i++) {
            for (int j = 0; j <= bag; j++) {
                fmap[i][j] = -1;
            }
        }

        return f2(weights, values, 0, bag, fmap);
    }

    private static int f2(int[] w, int[] v, int index, int rest, int[][] fmap) {
        if (rest < 0) {
            return -1;
        } else if (index == w.length) {
            return 0;
        } else {
            if (fmap[index][rest] != -1) {
                return fmap[index][rest];
            }
            int ans = 0;
            int p1 = f2(w, v, index + 1, rest, fmap);
            int p2 = 0;
            int v2 = f2(w, v, index+1,rest - w[index], fmap);
            if (v2 != -1) {
                p2 = v[index] + v2;
            }
            ans = Math.max(p1, p2);
            fmap[index][rest] = ans;
            return ans;
        }
    }

    public static int maxValue3(int[] weights, int[] values, int bag) {
        if (null == weights || null == values || weights.length == 0 || weights.length != values.length) {
            return 0;
        }
        int N = weights.length;
        int[][] dp = new int[N + 1][bag + 1];
        // dp[N][] 整行都为 0，默认值就是0，不需要赋值
        // 从下往上填表
        for (int i = N - 1; i >= 0; i--) {
            for (int rest = 0; rest <= bag; rest++) {
                 int p1 = dp[i+1][rest];
                 int p2 = 0;
                 int v2 = rest - weights[i] < 0 ? -1 : dp[i + 1][rest- weights[i]];
                if (v2 != -1) {
                    p2 = values[i] + v2;
                }
                 dp[i][rest] = Math.max(p1, p2);

//                int p1 = f1(weights, values, index + 1, rest);
//                int p2 = 0;
//                int v = f1(weights, values, index + 1, rest - weights[index]);
//                if (v != -1) {
//                    p2 = values[index] + v;
//                }
//                return Math.max(p1, p2);
            }
        }

//        return f1(weights, values, 0, bag);
        return dp[0][bag];
    }

    public static void main(String[] args) {
        int[] weights = {5, 2, 3, 1, 2, 3};
        int[] values = {100, 40, 60, 80, 70, 30};
        int bag = 10;
        System.out.println(maxValue1(weights, values, bag));
        System.out.println(maxValue2(weights, values, bag));
        System.out.println(maxValue3(weights, values, bag));

    }
}
