package com.study.system.recursion2dp;

/**
 * @description: 最短距离
 * @author: wj2wml@qq.com
 * @date: 2021-06-20 21:50
 **/
public class Q10_MinDistance {

    public static int min1(int[][] arr) {
        if (null == arr || arr.length == 0 || null == arr[0] || arr[0].length == 0) {
            return 0;
        }
        return process1(arr, 0, 0);
    }

    // x 当前来到的行数
    // y 当前来到的列数
    public static int process1(int[][] arr, int x, int y) {
        if (x == arr.length - 1 && y == arr[0].length - 1) {
            return arr[x][y];
        }
        // 往下
        int dis1 = Integer.MAX_VALUE;
        if (x + 1 < arr.length) {
            dis1 = arr[x][y] + process1(arr, x + 1, y);
        }
        // 往右
        int dis2 = Integer.MAX_VALUE;
        if (y + 1 < arr[0].length) {
            dis2 = arr[x][y] + process1(arr, x, y + 1);
        }
        return Math.min(dis1, dis2);
    }

    public static int min2(int[][] arr) {
        if (null == arr || arr.length == 0 || null == arr[0] || arr[0].length == 0) {
            return 0;
        }
        int row = arr.length;
        int col = arr[0].length;

        int[][] dp = new int[row][col];

        dp[row - 1][col - 1] = arr[row - 1][col - 1];

        // 只能往下 或者 往右。所以最后一行 和最好一列可以直接填出
        for (int i = row - 2; i >= 0; i--) {
            dp[i][col - 1] = dp[i + 1][col - 1] + arr[i][col - 1];
        }

        for (int i = col - 2; i >= 0; i--) {
            dp[row - 1][i] = dp[row - 1][i + 1] + arr[row - 1][i];
        }

        for (int i = row - 2; i >= 0; i--) {
            for (int j = col - 2; j >= 0; j--) {
                dp[i][j] = arr[i][j] + Math.min(dp[i + 1][j], dp[i][j + 1]);
            }
        }
        return dp[0][0];
    }

    public static int min3(int[][] arr) {
        if (null == arr || arr.length == 0 || null == arr[0] || arr[0].length == 0) {
            return 0;
        }
        int row = arr.length;
        int col = arr[0].length;

        int[][] dp = new int[row][col];

        dp[0][0] = arr[0][0];

        // 只能往下 或者 往右。所以最后一行 和最好一列可以直接填出
        for (int i = 1; i < row; i++) {
            dp[i][0] = dp[i - 1][0] + arr[i][0];
        }

        for (int i = 1; i < col; i++) {
            dp[0][i] = dp[0][i - 1] + arr[0][i];
        }
        for (int i = 1; i < row; i++) {
            for (int j = 1; j < col; j++) {
                dp[i][j] = arr[i][j] + Math.min(dp[i - 1][j], dp[i][j - 1]);
            }
        }
        return dp[row - 1][col - 1];
    }


    public static int min4(int[][] arr) {
        if (null == arr || arr.length == 0 || null == arr[0] || arr[0].length == 0) {
            return 0;
        }
        int row = arr.length;
        int col = arr[0].length;

        // 用一维数组来表示
        int[] dp = new int[col];

        dp[0] = arr[0][0];

        // 填充第一行数据
        for (int i = 1; i < col; i++) {
            dp[i] = dp[i - 1] + arr[0][i];
        }
        for (int i = 1; i < row; i++) {
            // 0 列位置单独计算
            dp[0] += arr[i][0];
            for (int j = 1; j < col; j++) {
                // 后面dp[j]代表上面的 距离
                // dp[j-1] 代表左面的距离
                dp[j] = arr[i][j] + Math.min(dp[j], dp[j - 1]);
            }
        }
        return dp[col - 1];
    }


    public static void main(String[] args) {
        int maxLen1 = 10;
        int maxLen2 = 10;
        int maxDis = 20;
        int times = 200000;
        for (int i = 0; i < times; i++) {
            int[][] arr = makeArr(maxLen1, maxLen2, maxDis);
            int m1 = min1(arr);
            int m2 = min2(arr);
            int m3 = min4(arr);
            if (m1 != m2 || m1 != m3) {
                System.out.println("完犊子了!!!");
                break;
            }
        }
        System.out.println("success!!!");
    }

    private static int[][] makeArr(int maxLen1, int maxLen2, int maxDis) {
        int len1 = (int) (Math.random() * maxLen1);
        int len2 = (int) (Math.random() * maxLen2);
        int[][] arr = new int[len1][len2];
        for (int i = 0; i < len1; i++) {
            for (int j = 0; j < len2; j++) {
                arr[i][j] = (int) (Math.random() * maxDis);
            }
        }
        return arr;
    }
}
