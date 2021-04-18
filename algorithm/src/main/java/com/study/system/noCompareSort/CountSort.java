package com.study.system.noCompareSort;

import java.util.Arrays;

public class CountSort {

    public void sort(int[] arr) {

        if (null == arr || arr.length < 2) {
            return;
        }

        int max = Integer.MIN_VALUE;

        for (int i = 0; i < arr.length; i++) {
            max = Math.max(max, arr[i]);
        }

        int[] help = new int[max + 1];

        for (int i = 0; i < arr.length; i++) {
            help[arr[i]]++;
        }

        int i = 0;
        // 从大到小
/*        for (int j = help.length - 1; j >= 0; j--) {
            while (help[j] > 0) {
                arr[i] = j;
                i++;
                help[j]--;
            }
        }*/
        // 从小到大
        for (int j = 0; j < help.length; j++) {
            while (help[j]-- > 0) {
                arr[i++] = j;
            }
        }
    }

    /*------------------------------------  测试代码 ----------------------------------------------*/
    public static void main(String[] args) {
        int maxLen = 20;
        int maxValue = 200;
        int times = 300000;
        for (int i = 0; i < times; i++) {
            int[] arr = randomArr(maxLen, maxValue);
            int[] cpArr = copyArr(arr);
            CountSort countSort = new CountSort();
            countSort.sort(arr);
            Arrays.sort(cpArr);
            if (!test(arr, cpArr)) {
                System.out.println("oops!!!");
                System.out.println(Arrays.toString(arr));
                System.out.println(Arrays.toString(cpArr));
                break;
            }
        }
    }

    public static boolean test(int[] arr, int[] cp) {
        if (arr.length != cp.length) {
            return false;
        }
        for (int i = 0; i < arr.length; i++) {
            if (arr[i] != cp[i]) {
                return false;
            }
        }
        return true;
    }

    public static int[] copyArr(int[] arr) {
        int[] newArr = new int[arr.length];
        for (int i = 0; i < arr.length; i++) {
            newArr[i] = arr[i];
        }
        return newArr;
    }

    public static int[] randomArr(int maxLen, int maxValue) {
        int len = (int) (Math.random() * maxLen);
        int[] arr = new int[len];
        for (int i = 0; i < len; i++) {
            arr[i] =  (int) (Math.random() * maxValue);
        }
        return arr;
    }
}
