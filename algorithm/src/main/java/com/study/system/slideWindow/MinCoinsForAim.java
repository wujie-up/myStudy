package com.study.system.slideWindow;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;

/**
 * @description: 得到目标数的最少货币
 * @author: wj2wml@qq.com
 * @date: 2021-07-08 21:58
 **/
public class MinCoinsForAim {

    public static int less1(int[] arr, int aim) {
        if (null == arr || arr.length < 1 || aim <= 0) {
            return -1;
        }
        return process(arr, 0, aim);
    }

    /**
     * 当前位置货币要或者不要，得到的最少张数
     *
     * @param arr:
     * @param index: 当前来到的位置
     * @param rest:  剩余的钱数
     **/
    private static int process(int[] arr, int index, int rest) {
        if (index == arr.length) {
            return rest == 0 ? 0 : -1;
        }
        if (rest < 0) {
            return -1;
        }
        // 当前位置不要
        int p1 = process(arr, index + 1, rest);
        int p2 = -1;
        int next = process(arr, index + 1, rest - arr[index]);
        if (next != -1) {
            p2 = 1 + next;
        }
        p1 = p1 == -1 ? Integer.MAX_VALUE : p1;
        p2 = p2 == -1 ? Integer.MAX_VALUE : p2;
        int ans  = Math.min(p1, p2);
        return ans == Integer.MAX_VALUE ? -1 : ans;
    }

    public static int less2(int[] arr, int aim) {
        if (null == arr || arr.length < 1 || aim <= 0) {
            return -1;
        }
        // 用map存储货币值，和货币的数量
        Map<Integer, Integer> coinMap = new HashMap<>();
        for (int c : arr) {
            if (coinMap.containsKey(c)) {
                coinMap.put(c, coinMap.get(c) + 1);
            } else {
                coinMap.put(c, 1);
            }
        }


        return process2(coinMap, coinMap.keySet().toArray(new Integer[]{}), 0, aim);
    }

    private static int process2(Map<Integer, Integer> coinMap,
                                Integer[] coins, int index, int rest) {
        if (index == coins.length) {
            return rest == 0 ? 0 : -1;
        }
        // 当前位置的钱数，可以要0 - Math.min(rest/coin.get[index], coinMap.get(c))张
        int N = Math.min(rest / coins[index], coinMap.get(coins[index]));
        int min = Integer.MAX_VALUE;
        for (int zhang = 0; zhang <= N; zhang++) {
            int next = process2(coinMap, coins, index + 1, rest - zhang * coins[index]);
            if (next != -1) {
                min = Math.min(min, next + zhang);
            }
        }
        return min == Integer.MAX_VALUE ? -1 : min;
    }

    public static int dp1(int[] arr, int aim) {
        if (null == arr || arr.length < 1 || aim <= 0) {
            return -1;
        }
        // 用map存储货币值，和货币的数量
        Map<Integer, Integer> coinMap = new HashMap<>();
        for (int c : arr) {
            if (coinMap.containsKey(c)) {
                coinMap.put(c, coinMap.get(c) + 1);
            } else {
                coinMap.put(c, 1);
            }
        }

        Integer[] coins = coinMap.keySet().toArray(new Integer[]{});
        int N = coins.length;
        int[][] dp = new int[N + 1][aim + 1];

        for (int rest = 1; rest <= aim; rest++) {
            dp[N][rest] = -1;
        }
        dp[N][0] = 0;

        for (int index = N - 1; index >= 0; index--) {
            for (int rest = 0; rest <= aim; rest++) {
                int maxZhang = Math.min(rest / coins[index], coinMap.get(coins[index]));
                int min = Integer.MAX_VALUE;
                for (int zhang = 0; zhang <= maxZhang; zhang++) {
                    int next = dp[index + 1][rest - zhang * coins[index]];
                    if (next != -1) {
                        min = Math.min(min, next + zhang);
                    }
                }
                dp[index][rest] = min == Integer.MAX_VALUE ? -1 : min;
            }
        }
        return dp[0][aim];
    }

    public static int dp2(int[] arr, int aim) {
        if (null == arr || arr.length < 1 || aim <= 0) {
            return -1;
        }

        Map<Integer, Integer> coinMap = new HashMap<>();
        for (int c : arr) {
            if (coinMap.containsKey(c)) {
                coinMap.put(c, coinMap.get(c) + 1);
            } else {
                coinMap.put(c, 1);
            }
        }

        Integer[] coins = coinMap.keySet().toArray(new Integer[]{});
        int N = coins.length;
        int[][] dp = new int[N + 1][aim + 1];

        for (int rest = 1; rest <= aim; rest++) {
            dp[N][rest] = Integer.MAX_VALUE; // 最小值滑动窗口不能用-1，会影响判断
        }
        dp[N][0] = 0;

        // 时间复杂度为O(货币种数 * aim)
        for (int index = N - 1; index >= 0; index--) {
            // 进行分组，跳着计算，这样就能使用滑动窗口，快速求出最小值
            for (int group = 0; group < Math.min(coins[index], aim + 1); group++) {
                // 因为group < coins[index]，所以group前面不可能还有 dp[index][group - coins[i]]
                dp[index][group] = dp[index + 1][group];

                // 准备最小值窗口  小 -> 大
                LinkedList<Integer> win = new LinkedList<>();
                win.add(group);

                // 开始滑动窗口，每次滑动coins[index]
                // 滑动轨迹：group + x, group + 2x, group + 3x...
                for (int rest = group + coins[index]; rest <= aim; rest += coins[index]) {
                    // 窗口更新时，无效的值需要从窗口中去掉
                    while (!win.isEmpty() &&
                            (dp[index + 1][win.peekLast()] == Integer.MAX_VALUE
                                    || (dp[index + 1][win.peekLast()] + compensate(coins[index], rest, win.peekLast())) >= dp[index + 1][rest]
                            )) {
                        win.pollLast();
                    }
                    win.add(rest);
                    // 判断最小值是否从左侧出窗口
                    // 计算超过钱数的索引位置
                    // i = group + 3x 此时计算了3张，但是x只有2张
                    // 所以i位置依赖 ={0 + dp[index+1][group + 3x], 1 + dp[index+1][group + 2x], 2 + dp[index+1][group + x]}
                    // dp[index + 1][group] 不参与依赖，所以窗口中group位置过期
                    // 推出公式 i - 3* x --> i - (zhang + 1) * coin
                    int overIndex = rest - coins[index] * (coinMap.get(coins[index]) + 1);
                    if (overIndex == win.peekFirst()) {
                        win.pollFirst();
                    }
                    dp[index][rest] = dp[index + 1][win.peekFirst()] + compensate(coins[index], rest, win.peekFirst());
                }
            }
        }
        return dp[0][aim] == Integer.MAX_VALUE ? -1 : dp[0][aim];
    }

    /**
     *  得到补偿的张数
     * @param coin      当前面值
     * @param index     当前计算aim位置
     * @param winIndex  下一排中 窗口中最小数的 aim位置
     */
    private static int compensate(int coin, int index, int winIndex) {
        return (index - winIndex) / coin;
    }

    public static void main(String[] args) {
        int maxLen = 10;
        int maxValue = 30;
        int maxAim = 50;
        int times = 50000;
        int count = 0;
        for (int i = 0; i < times; i++) {
            int[] arr = makeArr(maxLen, maxValue);
            int aim = (int) (Math.random() * maxAim) + 1;
            int m1 = less1(arr, aim);
            int m2 = less2(arr, aim);
            int m3 = dp1(arr, aim);
            int m4 = dp2(arr, aim);
            if (m1 !=m2 || m1 != m3 || m1 != m4) {
                System.out.println("完犊子了!");
                return;
            }
            if (m1 != -1) {
                count++;
            }
        }
        System.out.println("success!!! 成功次数 " + count);
    }

    private static int[] makeArr(int maxLen, int maxValue) {
        int len = (int) (Math.random() * maxLen);
        int[] arr = new int[len];
        for (int i = 0; i < len; i++) {
            arr[i] = (int) (Math.random() * maxValue) + 1;
        }
        return arr;
    }
}
