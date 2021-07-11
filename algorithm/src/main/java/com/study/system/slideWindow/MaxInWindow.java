package com.study.system.slideWindow;

import java.util.LinkedList;

/**
 * @description: 求滑动窗口内最大值
 * @author: wj2wml@qq.com
 * @date: 2021-07-07 21:47
 **/
public class MaxInWindow {

    /**
     * ()[4,3,5,4,3,3,6,7]  w = 3
     * ([4,3,5),4,3,3,6,7]  5
     * [4,(3,5,4),3,3,6,7]  5
     * [4,3,(5,4,3),3,6,7]  5
     * [4,3,5,(4,3,3),6,7]  4
     * [4,3,5,4,(3,3,6),7]  6
     * [4,3,5,4,3,(3,6,7])  7
     */
    public static int[] max1(int[] arr, int w) {
        if (null == arr || arr.length < w) {
            return null;
        }
        int max = arr[0];
        for (int i = 1; i < w; i++) {
            max = Math.max(max, arr[i]);
        }

        int N = arr.length;
        int[] ans = new int[N - w + 1];
        ans[0] = max;
        int index = 1;
        int R = w;

        while (R < N) {
            max = arr[R];
            // 遍历求出窗口内最大值
            for (int i = R - 1; i > R - w; i--) {
                max = Math.max(max, arr[i]);
            }
            ans[index++] = max;
            R++;
            ;
        }
        return ans;
    }

    public static int[] max2(int[] arr, int w) {
        if (null == arr || arr.length < w) {
            return null;
        }
        // 双端队列 用来存放 数组下标  大 -> 小
        LinkedList<Integer> window = new LinkedList<>();
        int N = arr.length;
        int R = 0;
        int index = 0;
        int[] ans = new int[N - w + 1];
        while (R < N) {
            // 如果窗口往右边扩时来到的数arr[R]，比窗口中最末尾的数大，则弹出窗口末尾中的数
            // 直到窗口为空，或窗口中的数＞arr[R]
            while (!window.isEmpty() && arr[window.peekLast()] <= arr[R]) {
                window.pollLast();
            }
            // 扩展的数加入窗口
            window.add(R);
            // 左边往右边扩时，有个数组下标需要出窗口，需要判断这个下标是否是窗口的第一个位置
            // 窗口内的下标都是从左往右添加的，只可能是队列最左边的数先出窗口
            if (R - w == window.peekFirst()) {
                window.pollFirst();
            }
            // 当R来到 w-1 位置时，开始计算最大值
            if (R >= w - 1) {
                ans[index++] = arr[window.peekFirst()];
            }
        }
        return ans;
    }

    public static void main(String[] args) {
        int maxLen = 10;
        int maxValue = 20;
        int maxWin = 10;
        int times = 500000;
        for (int i = 0; i < times; i++) {
            int[] arr = makeArr(maxLen, maxValue);
            int w = (int) (Math.random() * maxWin) + 1;
            int[] a1 = max1(arr, w);
            int[] a2 = max1(arr, w);

            if (a1 == null && a2 == null) {
                continue;
            }
            if (a1.length != a2.length) {
                System.out.println("完犊子了!");
                return;
            }
            for (int j = 0; j < a1.length; j++) {
                if (a1[j] != a2[j]) {
                    System.out.println("完犊子了!");
                    return;
                }
            }
        }
        System.out.println("success!!!");
    }

    private static int[] makeArr(int maxLen, int maxValue) {
        int len = (int) (Math.random() * maxLen) + 1;
        int[] arr = new int[len];
        for (int i = 0; i < len; i++) {
            arr[i] = (int) (Math.random() * maxValue);
        }
        return arr;
    }
}
