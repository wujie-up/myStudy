package com.study.system.slideWindow;

import java.util.LinkedList;

/**
 * @description: 加油站
 * @author: wj2wml@qq.com
 * @date: 2021-07-08 20:30
 **/
public class GasStation {
    public static int canCompleteCircuit(int[] gas, int[] cost) {
        if (null == gas || null == cost || gas.length == 0 || gas.length != cost.length) {
            return -1;
        }
        int N = gas.length;
        int M = N << 1;
        // 使用两倍长度的数组，来表示每个站出发跑一圈的情况
        int[] arr = new int[M];
        for (int i = 0; i < N; i++) {
            arr[i + N] = arr[i] = gas[i] - cost[i];
        }
        // 算出前缀和，方便后面快速计算以某位置为起点，中途所有站点的剩余油量情况
        for (int i = 1; i < M; i++) {
            arr[i] += arr[i - 1];
        }
        // 准备最小值窗口
        LinkedList<Integer> win = new LinkedList<>();
        // 初始化窗口，得到[0,N)位置上的窗口情况
        for (int i = 0; i < N; i++) {
            while (!win.isEmpty() && arr[win.peekLast()] >= arr[i]) {
                win.pollLast();
            }
            win.add(i);
        }
        // ans[i] = true,表示从i站出发可以行驶一圈
        boolean[] ans = new boolean[N];
        // 滑动窗口,判断 [N,M)位置上的窗口最小值 是否 >= 0
        // offset表示arr[i-1]，用于前缀和 计算 真实数据情况
        for (int i = 0, offset = 0, j = N; j < M; offset=arr[i++], j++) {
            // 得到窗口内真实的 剩余油量 最小值
            if (arr[win.peekFirst()] - offset >= 0) {
                ans[i] = true;
            }
            // 更新窗口最小值
            while (!win.isEmpty() && arr[win.peekFirst()] >= arr[j]) {
                win.pollLast();
            }
            win.add(j);
            // 左边出窗口位置判断
            if (win.peekFirst() == i) {
                win.pollFirst();
            }
        }

        for (int i = 0; i < N; i++) {
            if (ans[i]) {
                return i;
            }
        }
        return -1;
    }
}
