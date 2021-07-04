package com.study.system.recursion2dp;

/**
 * @description: 将数裂开
 * @author: wj2wml@qq.com
 * @date: 2021-07-01 22:03
 **/
public class Q17_SplitNumber {

    /**
     * 求num裂开的方法数，后面的数不能比前面的数小
     * 4 -> 1+1+2+2、 1+1+3、1+3、2+2、4
     *
     * @param num 正数
     */
    public static int ways1(int num) {
        return process(num, 1);
    }

    /**
     * @param rest 剩余的数值
     * @param pre  前一个裂开的数
     */
    private static int process(int rest, int pre) {
        // 走到rest==0 说明，已经裂开完了，并且中途的抉择都是对的
        // rest == pre 则最后一个数只能 为pre，无法再分
        if (rest == 0 || rest == pre) {
            return 1;
        }
        // 剩余的数 < 前一个数，不成立
        if (rest < pre) {
            return 0;
        }

        int ways = 0;
        for (int i = pre; i <= rest; i++) {
            ways += process(rest - i, i);
        }
        return ways;
    }

    public static int dp1(int num) {
        int[][] dp = new int[num + 1][num + 1];
        for (int pre = 1; pre <= num; pre++) {
            dp[0][pre] = 1;
            dp[pre][pre] = 1;
        }

        for (int rest = 2; rest <= num; rest++) {
            // rest == pre 位置已经填了，rest < pre的位置没有意义
            for (int pre = 1; pre < rest; pre++) {
                int ways = 0;
                for (int i = pre; i <= rest; i++) {
                    ways += dp[rest - i][i];
                }
                dp[rest][pre] = ways;
            }
        }

//        for (int pre = num - 1; pre >= 1; pre--) {
//            for (int rest = pre + 1; rest <= num; rest++) { // rest > pre才有意义
//                int ways = 0;
//                for (int first = pre; first <= rest; first++) {
//                    ways += dp[rest - first][first];
//                }
//                dp[rest][pre] = ways;
//            }
//        }
        return dp[num][1];
    }

    public static int dp2(int num) {
        int[][] dp = new int[num + 1][num + 1];
        for (int pre = 1; pre <= num; pre++) {
            dp[0][pre] = 1;
            dp[pre][pre] = 1;
        }

        for (int pre = num - 1; pre >= 1; pre--) {
            for (int rest = pre + 1; rest <= num; rest++) { // rest > pre才有意义
                dp[rest][pre] = dp[rest][pre + 1] + dp[rest -pre][pre];
            }
        }
        return dp[num][1];
    }


    public static void main(String[] args) {
        int maxNum = 50;
        int times = 50000;
        for (int i = 0; i < times; i++) {
            int num = (int) (Math.random() * maxNum) + 1;
            int w1 = ways1(num);
            int w2 = dp1(num);
            int w3 = dp2(num);

            if (w1 != w2 || w1 != w3) {
                System.out.println("完犊子了！");
                break;
            }
        }
        System.out.println("success!!!");
    }
}
