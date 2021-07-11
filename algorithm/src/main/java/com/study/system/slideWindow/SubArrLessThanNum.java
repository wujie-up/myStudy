package com.study.system.slideWindow;

import java.util.LinkedList;

/**
 * @description: 求数组中小于目标数的子数组个数
 * @author: wj2wml@qq.com
 * @date: 2021-07-07 22:30
 **/
public class SubArrLessThanNum {

    public static int count1(int[] arr, int m) {
        if (null == arr || arr.length < 1 || m < 0) {
            return 0;
        }
        int N = arr.length;
        int count = 0;
        for (int L = 0; L < N; L++) {
            for (int R = L; R < N; R++) {
                int max = arr[L];
                int min = arr[L];
                for (int i = L + 1; i <= R; i++) {
                    max = Math.max(max, arr[i]);
                    min = Math.min(min, arr[i]);
                }
                if (max - min <= m) {
                    count++;
                }
            }
        }
        return count;
    }

    /**
     * [5,3,1,8]  m = 5
     */
    public static int count2(int[] arr, int m) {
        if (null == arr || arr.length < 1 || m < 0) {
            return 0;
        }
        int N = arr.length;
        // 最大值和最小值窗口
        LinkedList<Integer> maxWin = new LinkedList<>();
        LinkedList<Integer> minWin = new LinkedList<>();
        int count = 0;
        int R = 0;
        // [0....R(首次不满足)  [1.....R [2.....R
        for (int L = 0; L < N; L++) {
            while (R < N) {
                // 大 -> 小
                while (!maxWin.isEmpty() && arr[maxWin.peekLast()] <= arr[R]) {
                    maxWin.pollLast();
                }
                maxWin.add(R);
                // 小 -> 大
                while (!minWin.isEmpty() && arr[minWin.peekLast()] >= arr[R]) {
                    minWin.pollLast();
                }
                minWin.add(R);
                if (arr[maxWin.peekFirst()] - arr[minWin.peekFirst()] <= m) {
                    // 满足条件，R往右滑动
                    R++;
                } else {
                    // 不满足停住
                    break;
                }
            }
            count += R - L;
            // L往右滑时，判断出去的下标是否是窗口中第一个
            if (L == maxWin.peekFirst()) {
                maxWin.pollFirst();
            }
            if (L == minWin.peekFirst()) {
                minWin.pollFirst();
            }
        }
        return count;
    }
    public static void main(String[] args) {
        int maxLen = 10;
        int maxValue = 20;
        int times = 50000;
        for (int i = 0; i < times; i++) {
            int[] arr = makeArr(maxLen, maxValue);
            int m = (int) (Math.random() * maxValue) + 1;
            int a1 = count1(arr, m);
            int a2 = count2(arr, m);

            if (a1 != a2) {
                System.out.println("完犊子了!");
                return;
            }
        }
        System.out.println("success!!!");
    }

    private static int[] makeArr(int maxLen, int maxValue) {
        int len = (int) (Math.random() * maxLen);
        int[] arr = new int[len];
        for (int i = 0; i < len; i++) {
            arr[i] = (int) (Math.random() * maxValue);
        }
        return arr;
    }
}
