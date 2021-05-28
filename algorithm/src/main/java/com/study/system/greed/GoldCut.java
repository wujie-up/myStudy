package com.study.system.greed;

import java.util.PriorityQueue;

/**
 * @description: 黄金切割问题
 * @author: wj2wml@qq.com
 * @date: 2021-05-28 21:25
 **/
public class GoldCut {

    public static int takeMin1(int[] arr) {
        if (null == arr || arr.length == 0) {
            return 0;
        }

        int N = arr.length;
        // 使用小根堆来实现哈夫曼
        PriorityQueue<Integer> queue = new PriorityQueue<>();
        for (int i = 0; i < N; i++) {
            queue.add(arr[i]);
        }
        int count = 0;
        while (queue.size() > 1) {
            int sum = queue.poll() + queue.poll();
            count += sum;
            queue.add(sum);
        }
        return count;
    }

    public static int takeMin2(int[] arr) {
        if (null == arr || arr.length == 0) {
            return 0;
        }

        return process(arr, 0);
    }

    /**
     * 暴力递归，得出所有可能中的最小值
     * @param count 花费的铜板数
     */
    private static int process(int[] arr, int count) {
        // 只剩最后一个，说明已经切完了，直接返回，最后一个数 是前面一层两个数的和
        if (arr.length == 1) {
            return count;
        }
        // 数组中的数 两两相加，求出所有的结果
        int ans = Integer.MAX_VALUE;
        for (int i = 0; i < arr.length; i++) {
            for (int j = i + 1; j < arr.length; j++) {
                int sum = process(mergeArray(arr, i, j), count + arr[i] + arr[j]);
                ans = Math.min(ans, sum);
            }
        }
        return ans;
    }

    // 将 i 和 j 位置的数合并，并从数组中删除，合并的数 添加到新的数组中
    private static int[] mergeArray(int[] arr, int i, int j) {
        int[] newArr = new int[arr.length - 1];
        int index = 0;
        for (int k = 0; k < arr.length; k++) {
            if (k != i && k != j) {
                newArr[index++] = arr[k];
            }
        }
        newArr[index] = arr[i] + arr[j];
        return newArr;
    }


    public static void main(String[] args) {
        int maxLength = 8;
        int maxValue = 300;
        int times = 100000;
        for (int i = 0; i < times; i++) {
            int[] arr = makeArr(maxLength, maxValue);
            int m1 = takeMin1(arr);
            int m2 = takeMin2(arr);
            if (m1 != m2) {
                System.out.println("完犊子了!");
                break;
            }
        }
        System.out.println("success!!!");
    }

    private static int[] makeArr(int maxLength, int maxValue) {
        int len = (int) (Math.random() * maxLength);
        int[] arr = new int[len];
        for (int i = 0; i < len; i++) {
            arr[i] = (int) (Math.random() * maxValue) + 10;
        }
        return arr;
    }
}
