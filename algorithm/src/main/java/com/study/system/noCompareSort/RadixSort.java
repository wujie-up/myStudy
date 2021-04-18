package com.study.system.noCompareSort;

import java.util.Arrays;

public class RadixSort {

    public void sort2(int[] arr) {
        if (null == arr || arr.length < 2) {
            return;
        }

        int num = getLen(arr);

        int divide = 1;

        int[] help = new int[arr.length];

        for (int n = 0; n < num; n++, divide *=  10) {
            int[] count = new int[10];

            for (int i = arr.length - 1; i >= 0; i--) {
                // 算出下标
                int index = (arr[i] / divide) % 10;
                count[index]++;
            }

            // 求出前缀和
            for (int i = 1; i < count.length; i++) {
                count[i] = count[i-1] + count[i];
            }

            for (int i = arr.length - 1; i >= 0; i--) {
                // 算出下标
                int index = (arr[i] / divide) % 10;
                help[count[index] - 1] = arr[i];
                count[index]--;
            }

            for (int i = 0; i < arr.length; i++) {
                arr[i] = help[i];
            }
        }
    }

    public void sort(int[] arr) {
        if (null == arr || arr.length < 2) {
            return;
        }

        int num = getLen(arr);

        int divide = 1;

        for (int j = 0; j < num; j++) {
            int[][] help = new int[10][0];

            for (int i = arr.length - 1; i >= 0; i--) {
                // 算出桶的序号
                int bucket = (arr[i] / divide) % 10 ;
                help[bucket] = arrAppend(help[bucket], arr[i]);
            }
            divide *=  10;

            int pos = 0;
            for (int[] bucket : help) {
                if (bucket.length > 0) {
                    for (int k = bucket.length - 1; k >= 0; k--) {
                        arr[pos++] = bucket[k];
                    }
                }
            }
        }
    }

    // 自动扩容, 每次arr增加一个数，都要扩容，可以用ArrayList代替
    private int[] arrAppend(int[] arr, int value) {
        arr = Arrays.copyOf(arr,arr.length + 1);
        arr[arr.length -1] = value;
        return arr;
    }

    private int getLen(int[] arr) {
        // 计算出数组中最大数的有几位
        int max = Integer.MIN_VALUE;
        for (int i = 0; i < arr.length; i++) {
            max = Math.max(max, arr[i]);
        }
        int len = 0;
        while (max != 0) {
            len++;
            max /= 10;
        }
        return len;
    }


    /*----------------------------------- 测试代码 --------------------------------------*/
    public static void main(String[] args) {
        int maxLen = 5;
        int maxValue = 1000;
        int times = 200000;
        RadixSort sort = new RadixSort();
        for (int i = 0; i < times; i++) {
            int[] arr = randomArr(maxLen, maxValue);
            int[] cpArr = copyArr(arr);
            sort.sort2(arr);
            Arrays.sort(cpArr);
            if (!test(arr, cpArr)) {
                System.out.println("oops !!");
                System.out.println(Arrays.toString(arr));
                System.out.println(Arrays.toString(cpArr));
                break;
            }
        }
    }

    private static boolean test(int[] arr, int[] cp) {
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

    private static int[] copyArr(int[] arr) {
        int[] newArr = new int[arr.length];
        for (int i = 0; i < arr.length; i++) {
            newArr[i] = arr[i];
        }
        return newArr;
    }

    private static int[] randomArr(int maxLen, int maxValue) {
        int len = (int) (Math.random() * maxLen);
        int[] arr = new int[len];
        for (int i = 0; i < len; i++) {
            if ( i != 0) {
                arr[i] = 883;
            } else {
                arr[i] = (int) (Math.random() * maxValue);
            }
        }
        return arr;
    }
}
