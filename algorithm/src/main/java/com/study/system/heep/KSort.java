package com.study.system.heep;


import java.util.Arrays;

public class KSort {
    public static void sort(int[] arr, int k) {
        if (k == 0) {
            return;
        }
        Heap heap = new Heap(k + 1);
        int j = 0;
        for (int i = 0; i < arr.length; i++) {
            heap.push(arr[i]);
            if (heap.isFull()) {
                int num = heap.pop();
                arr[j++] = num;
            }
        }

        while (!heap.isEmpty()) {
            arr[j++] = heap.pop();
        }
    }

    public static void main(String[] args) {
        int maxLen = 30;
        int maxValue = 1000;
        int times = 888888;
        for (int i = 0; i < times; i++) {
            int k = (int) (Math.random() * maxLen) + 1;
            int[] arr = randomArr(maxLen, maxValue, k);
            int[] cpArr = copyArr(arr);
            sort(arr, k);
            Arrays.sort(cpArr);
            if (!compare(arr, cpArr)) {
                System.out.println("oops !!!");
                System.out.println(Arrays.toString(arr));
                break;
            }
        }
    }

    private static int[] randomArr(int maxLen, int maxValue, int k) {
        int len = (int)(Math.random() * maxLen) + k;
        int[] arr = new int[len];
        for (int i = 0; i < len; i++) {
            arr[i] = (int) ((maxValue + 1) * Math.random()) - (int) (maxValue * Math.random());
        }
        // 排序
        Arrays.sort(arr);
        // 打乱顺序
        // 然后开始随意交换，但是保证每个数距离不超过K
        // swap[i] == true, 表示i位置已经参与过交换
        // swap[i] == false, 表示i位置没有参与过交换
        boolean[] isSwap = new boolean[arr.length];
        for (int i = 0; i < len; i++) {
            int j = Math.min(len - 1, (int)(Math.random() * (k + 1)) + i) ;
            if (!isSwap[i] && !isSwap[j]) {
                int temp = arr[i];
                arr[i] = arr[j];
                arr[j] = temp;
                isSwap[i] = true;
                isSwap[j] = true;
            }
        }
        return arr;
    }

    private static int[] copyArr(int[] arr) {
        int[] cpArr = new int[arr.length];
        for (int i = 0; i < arr.length; i++) {
            cpArr[i] = arr[i];
        }
        return cpArr;
    }

    private static boolean compare(int[] arr, int[] cpArr) {
        if (arr.length != cpArr.length) {
            return false;
        }
        for (int i = 0; i < arr.length; i++) {
            if (arr[i] != cpArr[i]) {
                return false;
            }
        }
        return true;
    }
}
