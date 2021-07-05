package com.study.system.recursion2dp;

import java.util.ArrayList;
import java.util.List;

/**
 * @description: N皇后问题
 * @author: wj2wml@qq.com
 * @date: 2021-07-05 20:29
 **/
public class Q20_NQueen {

    static class Position {
        int x;
        int y;

        public Position(int x, int y) {
            this.x = x;
            this.y = y;
        }
    }

    public static int ways(int n) {
        if (n == 1) {
            return 1;
        }
        boolean[] pick = new boolean[n];
        List<Position> picks = new ArrayList<>();
        return process(picks, pick, 0, n);
    }

    /**
     * @param picks 已经被占用位置集合
     * @param pick  pick[i] = true 第i列被占用了
     * @param row   当前来到的行数
     * @param n     总共的行列数
     * @return
     */
    private static int process(List<Position> picks, boolean[] pick, int row, int n) {
        if (row == n) {
            return 1;
        }
        int ways = 0;
        for (int col = 0; col < n; col++) {
            Position position = new Position(row, col);
            if (!pick[col] && notSlash(picks, position)) {
                pick[col] = true;
                picks.add(position);
                ways += process(picks, pick, row + 1, n);
                pick[col] = false;
                picks.remove(position);
            }
        }
        return ways;
    }

    private static boolean notSlash(List<Position> picks, Position p) {
        for (Position pick : picks) {
            if (Math.abs(pick.x - p.x) == Math.abs(pick.y - p.y)) {
                return false;
            }
        }
        return true;
    }

    public static int ways2(int n) {
        if (n <= 0) {
            return 0;
        }
        // pick[x] = y  表示第x 行的列 在 y 位置
        int[] picks = new int[n];
        return process2(picks, 0, n);
    }

    private static int process2(int[] picks, int row, int n) {
        if (row == n) {
            return 1;
        }
        int ways = 0;
        for (int col = 0; col < n; col++) {
            if (isValid(picks, row, col)) {
                picks[row] = col;
                ways += process2(picks, row + 1, n);
            }
        }
        return ways;
    }

    // 判断当前(row, col) 与 row之前的行做的选择 是否有冲突
    private static boolean isValid(int[] picks, int row, int col) {
        for (int x = 0; x < row; x++) {
            if (picks[x] == col || (Math.abs(x - row) == Math.abs(picks[x] - col))) {
                return false;
            }
        }
        return true;
    }

    public static int ways3(int n) {
        // int 类型最多32位，所以n不能大于32
        if (n < 1 || n > 32) {
            return 0;
        }
        // 如果你是10皇后问题，limit 最右10个1，其他都是0
        // 0...0 0 0 0 0 0 0 0 0 0 1   << 10
        // 0...1 0 0 0 0 0 0 0 0 0 0    - 1
        // 0...0 1 1 1 1 1 1 1 1 1 1
        // 当n = 32时，二进制表示刚好32位都是 1 ，所以值是 - 1
        int limit = n == 32 ? -1 : (1 << n) - 1;
        return process3(limit, 0, 0, 0);
    }

    // limit : 0...0 1 1 1 1 1 1 1 1 1 1
    // 之前皇后的列影响：colLim
    // 之前皇后的左下对角线影响：leftDiaLim
    // 之前皇后的右下对角线影响：rightDiaLim
    public static int process3(int limit, int colLim, int leftDiaLim, int rightDiaLim) {
        if (colLim == limit) {
            return 1;
        }
        /**
         *    (0..) 0 0 0 0 1 0 0 0 0 0 colLim
         *  | (0..) 0 0 0 1 0 0 0 0 0 0 leftDiaLim
         *  | (0..) 0 0 0 0 0 1 0 0 0 0 rightDiaLim
         *  ~ (0..) 0 0 0 1 1 1 0 0 0 0
         *    (1..) 1 1 1 0 0 0 1 1 1 1
         *  & (0..) 1 1 1 1 1 1 1 1 1 1 limit
         *    (0..) 1 1 1 0 0 0 1 1 1 1 pos 1都是可以放皇后的位置
         */
        int pos = limit & (~(colLim | leftDiaLim | rightDiaLim));
        int mostRightOne = 0;
        int res = 0;
        while (pos != 0) {
            /**
             *    (0..) 1 1 1 0 0 0 1 1 1 1 pos
             * ~  (1..) 0 0 0 1 1 1 0 0 0 0
             * +1 (1..) 0 0 0 1 1 1 0 0 0 1
             * &  (0..) 1 1 1 0 0 0 1 1 1 1 pos
             *    (0..) 0 0 0 0 0 0 0 0 0 1 mostRightOne 得到最右边的 1
             */
            mostRightOne = pos & (~pos + 1);
            // 减去最右边的1，也就是最右边的1放皇后
            //   (0..) 1 1 1 0 0 0 1 1 1 1 pos
            //-1 (0..) 1 1 1 0 0 0 1 1 1 0 pos
            pos = pos - mostRightOne;
            /**
             *   (0..) 0 0 0 0 1 0 0 0 0 0 colLim
             * | (0..) 0 0 0 0 0 0 0 0 0 1
             *   (0..) 0 0 0 0 1 0 0 0 0 1 nextColLim
             */
            int nextColLim = colLim | mostRightOne;
            /**
             *      (0..) 0 0 0 1 0 0 0 0 0 0  leftDiaLim
             *  |   (0..) 0 0 0 0 0 0 0 0 0 1  mostRightOne
             * <<1  (0..) 0 0 0 1 0 0 0 0 0 1
             *      (0..) 0 0 1 0 0 0 0 0 1 0  nextLeftDiaLim
             */
            int nextLeftDiaLim = (leftDiaLim | mostRightOne) << 1;
            /**
             *       (0..) 0 0 0 0 0 1 0 0 0 0 rightDiaLim
             * |     (0..) 0 0 0 0 0 0 0 0 0 1 mostRightOne
             * >>>1  (0..) 0 0 0 0 0 1 0 0 0 1
             *       (0..) 0 0 0 0 0 0 1 0 0 0 nextRightDiaLim
             */
            int nextRightDiaLim = (rightDiaLim | mostRightOne) >>> 1;

            /**
             *    (0..) 0 0 0 0 1 0 0 0 0 1 nextColLim
             *  | (0..) 0 0 1 0 0 0 0 0 1 0 nextLeftDiaLim
             *  | (0..) 0 0 0 0 0 0 1 0 0 0 nextRightDiaLim
             *  ~ (0..) 0 0 1 0 1 0 1 0 1 1
             *    (1..) 1 1 0 1 0 1 0 1 0 0
             *  & (0..) 1 1 1 1 1 1 1 1 1 1 limit
             *    (0..) 1 1 0 1 0 1 0 1 0 0 pos 1都是可以放皇后的位置
             */
            res += process3(limit,  nextColLim, nextLeftDiaLim, nextRightDiaLim);
        }
        return res;
    }

    public static void main(String[] args) {
        int n = 7;

        long start = System.currentTimeMillis();
        System.out.println(ways2(n));
        long end = System.currentTimeMillis();
        System.out.println("cost time: " + (end - start) + "ms");

        start = System.currentTimeMillis();
        System.out.println(ways3(n));
        end = System.currentTimeMillis();
        System.out.println("cost time: " + (end - start) + "ms");
    }
}
