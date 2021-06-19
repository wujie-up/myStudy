package com.study.system.recursion2dp;

import java.util.Comparator;
import java.util.PriorityQueue;

/**
 * @description: 咖啡问题
 * @author: wj2wml@qq.com
 * @date: 2021-06-19 16:00
 **/
public class Q9_Coffee {

    public static class Machine {
        int availableTimePoint;
        int workTime;

        public Machine(int availableTimePoint, int workTime) {
            this.availableTimePoint = availableTimePoint;
            this.workTime = workTime;
        }
    }

    // arr 第i号咖啡机泡一杯咖啡的时间
    // N 排队和咖啡的人数
    // a 洗一个咖啡杯需要的时间
    // b 挥发干净的时间
    public static int min1(int[] arr, int N, int a, int b) {
        if (null == arr || arr.length == 0 || N < 0) {
            return -1;
        }
        // 求出所有人喝完咖啡的最快时间
        int[] drinks = fastDrink(arr, N);
        return wash1(drinks, 0, 0, a, b);
    }

    // arr 杯子写的时间点
    // index 当前的杯子
    // free 洗的机器空闲的时间点
    // a 洗的时间
    // b 挥发的时间
    private static int wash1(int[] arr, int index, int free, int wash, int air) {
        if (index == arr.length) {
            return 0;
        }
        // 1、决定洗, 洗完的时间 =  Max(洗咖啡机可用的时间点，咖啡杯要洗的时间点) + 洗的时间
        int curDoneTime1 = Math.max(free, arr[index]) + wash;
        // 1.1 剩下杯子洗完的时间
        int restDoneTime1 = wash1(arr, index + 1, curDoneTime1, wash, air);
        // 1.2 求出所有杯子都洗完的时间，最大值
        int time1 = Math.max(curDoneTime1, restDoneTime1);
        // 2、决定挥发
        int curDoneTime2 = arr[index] + air;
        int restDoneTime2 = wash1(arr, index + 1, free, wash, air);
        int time2 = Math.max(curDoneTime2, restDoneTime2);

        // 洗和挥发选择用时最小的
        return Math.min(time1, time2);
    }

    private static int[] fastDrink(int[] arr, int N) {
        PriorityQueue<Machine> heap =
                new PriorityQueue<>(Comparator.comparingInt(o -> (o.availableTimePoint + o.workTime)));

        for (int m : arr) {
            heap.add(new Machine(0, m));
        }

        int[] drinks = new int[N];
        int index = 0;
        while (index < N) {
            Machine machine = heap.poll();
            machine.availableTimePoint += machine.workTime;
            drinks[index++] = machine.availableTimePoint;
            heap.add(machine);
        }
        return drinks;
    }

    public static int min2(int[] arr, int N, int a, int b) {
        if (null == arr || arr.length == 0 || N < 0) {
            return -1;
        }
        // 求出所有人喝完咖啡的最快时间
        int[] drinks = fastDrink(arr, N);
        return dp(drinks, a, b);

    }

    public static int dp(int[] arr, int wash, int air) {
        int maxFree = getMaxFree(arr, wash);
        int N = arr.length;
        int[][] dp = new int[N + 1][maxFree + 1];

        for (int index = N - 1; index >= 0; index--) {
            for (int free = 0; free <= maxFree; free++) {
                int curWashDoneTime = Math.max(free, arr[index]) + wash;
                if (curWashDoneTime > maxFree) {
                    //  当前杯子洗完的时间 > 咖啡机最大可能的空闲时间，这个是不可能发生的，说明这些表格的数据是无意义的，不需要填写
                    break;
                }
                int restDoneTime1 = dp[index + 1][curWashDoneTime];
                int time1 = Math.max(curWashDoneTime, restDoneTime1);

                int curAirDoneTime = arr[index] + air;
                int restDoneTime2 = dp[index + 1][free];
                int time2 = Math.max(curAirDoneTime, restDoneTime2);
                dp[index][free] =  Math.min(time1, time2);
            }
        }
        return dp[0][0];
    }


    /**
     * {1,3,100} wash = 5
     * 6 11 105  -> 105
     */
    private static int getMaxFree(int[] arr, int wash) {
        int maxFree = 0;
        for (int m : arr) {
            maxFree = Math.max(maxFree, m) + wash;
        }
        return maxFree;
    }


    public static void main(String[] args) {
        // 泡咖啡机的最大数量
        int maxNum = 10;
        // 泡咖啡的最大时间
        int maxCoffee = 20;
        // 排队的最大人数
        int maxManNum = 10;
        // 挥发的最大时间
        int maxAirTime = 20;
        // 洗咖啡杯的最大时间
        int maxWashTime = 10;

        int times = 200000;
        for (int i = 0; i < times; i++) {
            int[] arr = makeArr(maxNum, maxCoffee);
            int N = (int) (Math.random() * maxManNum) + 1;
            int a = (int) (Math.random() * maxWashTime) + 1;
            int b = (int) (Math.random() * maxAirTime) + 1;

            int m1 = min1(arr, N, a, b);
            int m2 = min2(arr, N, a, b);

            if (m1 != m2) {
                System.out.println("完犊子了！！！");
                break;
            }
        }
        System.out.println("success!!!");
    }

    private static int[] makeArr(int maxNum, int maxCoffee) {
        int len = (int) (Math.random() * maxNum);
        int[] arr = new int[len];
        for (int i = 0; i < len; i++) {
            arr[i] = (int) (Math.random() * maxCoffee) + 1;
        }
        return arr;
    }
}
