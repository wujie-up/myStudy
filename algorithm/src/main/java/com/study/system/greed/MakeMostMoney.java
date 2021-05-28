package com.study.system.greed;

import java.util.Comparator;
import java.util.PriorityQueue;

/**
 * @description: 项目利益最大化问题
 * @author: wj2wml@qq.com
 * @date: 2021-05-28 23:50
 **/
public class MakeMostMoney {
    static class Program {
        int cost;
        int profit;

        public Program(int cost, int profit) {
            this.cost = cost;
            this.profit = profit;
        }
    }

    /**
     * @param costs   i 位置项目花费钱数
     * @param profits i 位置项目获得利润
     * @param k       最多做 K 个项目
     * @param m       创业基金
     * @return
     */
    public static int makeMostMoney1(int[] costs, int[] profits, int k, int m) {
        if (null == costs || null == profits || costs.length != profits.length || k <= 0 || m < 0) {
            // 无效的参数
            return 0;
        }

        PriorityQueue<Program> cQueue = new PriorityQueue<>(Comparator.comparingInt(o -> o.cost));
        PriorityQueue<Program> pQueue = new PriorityQueue<>((o1, o2) -> o2.profit - o1.profit);

        for (int i = 0; i < costs.length; i++) {
            cQueue.add(new Program(costs[i], profits[i]));
        }

        for (int i = 0; i < k; i++) {
            // 将所有能做的项目 从花费堆 移到 利润堆中
            while (!cQueue.isEmpty() && cQueue.peek().cost <= m) {
                pQueue.add(cQueue.poll());
            }
            // 利润堆中没有，说明没有项目可做了
            if (pQueue.isEmpty()) {
                return m;
            }
            // 资金 == 可做项目最大利润 加 上次资金
            m += pQueue.poll().profit;
        }

        return m;
    }

    /**
     * 暴力解法，列出所有的可能性
     */
    public static int makeMostMoney2(int[] costs, int[] profits, int k, int m) {
        if (null == costs || null == profits || costs.length != profits.length || k <= 0 || m < 0) {
            // 无效的参数
            return 0;
        }
        Program[] programs = new Program[costs.length];
        for (int i = 0; i < costs.length; i++) {
            programs[i] = new Program(costs[i], profits[i]);
        }

        return process(programs, k, m);
    }

    /**
     * @param rest  还可以做rest个项目
     * @param money 有多少可用资金
     * @return
     */
    private static int process(Program[] programs, int rest, int money) {
        if (programs.length == 0 || rest == 0) {
            return money;
        }

        int max = money;
        for (int i = 0; i < programs.length; i++) {
            if (programs[i].cost <= money) {
                max = Math.max(max, process(excludedPro(programs, i), rest - 1, money + programs[i].profit));
            }
        }
        return max;
    }

    private static Program[] excludedPro(Program[] programs, int i) {
        Program[] pms = new Program[programs.length - 1];
        int index = 0;
        for (int j = 0; j < programs.length; j++) {
            if (j != i) {
                pms[index++] = programs[j];
            }
        }
        return pms;
    }

    public static void main(String[] args) {
        // 最多的项目花费
        int maxC = 5000;
        // 最大的利润
        int maxP = 600;
        // 最多做的项目个数
        int maxK = 10;
        // 最大的启动资金
        int maxM = 3000;
        // 最多的可选择项目个数
        int maxSize = 10;

        int times = 100000;
        for (int i = 0; i < times; i++) {
            int size = (int) (Math.random() * maxSize);
            int[] cArr = makeCPs(maxC, size);
            int[] pArr = makeCPs(maxP, size);
            int k = (int) (Math.random() * maxK) + 1;
            int m = (int) (Math.random() * maxM);
            int m1 = makeMostMoney1(cArr, pArr, k, m);
            int m2 = makeMostMoney2(cArr, pArr, k, m);
            if (m1 != m2) {
                System.out.println("完犊子了！");
                break;
            }
        }
        System.out.println("success!!!");
    }


    private static int[] makeCPs(int max, int size) {

        int[] arr = new int[size];

        for (int i = 0; i < size; i++) {
            arr[i] = (int) (Math.random() * max);
        }

        return arr;
    }
}
