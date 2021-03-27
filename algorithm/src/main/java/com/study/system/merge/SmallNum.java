package com.study.system.merge;

import java.util.Arrays;

public class SmallNum {

    public static void main(String[] args) {
        int maxValue = 10000;
        int maxLen = 10;
        int times = 888888;
        for (int i = 0; i < times; i++) {
           int[] arr = randomArray(maxLen, maxValue);
            int[] copyArray = copyArray(arr);
            int sum1 = normalSmallSum(copyArray);
           int sum2 = smallNum(arr);
           if (sum1 != sum2) {
               System.out.println("oops !!!");
               System.out.println(Arrays.toString(copyArray));
               System.out.println(Arrays.toString(arr));
               System.out.println(sum1);
               System.out.println(sum2);
               break;
           }
        }
    }

    public static int[] copyArray(int[] arr) {
        if (arr == null) {
            return null;
        }
        int[] res = new int[arr.length];
        for (int i = 0; i < arr.length; i++) {
            res[i] = arr[i];
        }
        return res;
    }

    private static int normalSmallSum(int[] arr) {
        int ans = 0;
        for (int i = 1; i < arr.length; i++) {
            for (int j = 0; j < i; j++) {
                ans += arr[j] < arr[i] ? arr[j] : 0;
            }
        }
        return ans;
    }

    private static int[] randomArray(int maxLen, int maxValue) {
        int len = (int) (Math.random() * maxLen) + 3;
        int[] arr = new int[len];
        for (int i = 0; i < len; i++) {
            arr[i] = (int) (Math.random() * maxValue) + 1;
        }
        return arr;
    }


    public static int smallNum(int[] arr) {
        if (null == arr || arr.length < 2) {
            return 0;
        }
        return process(arr, 0, arr.length - 1);
    }

    private static int process(int[] arr, int L, int R) {
        // 递归的出口
        if (L == R) {
            return 0;
        }
        int M = (L + R) / 2;
        return process(arr, L, M) + process(arr, M + 1, R) + merge(arr, L, M, R);
    }

    private static int merge(int[] arr, int L, int M, int R) {
        int[] help = new int[R - L + 1];
        int p1 = L;
        int p2 = M + 1;
        int ans = 0;
        int i = 0;
        while (p1 <= M && p2 <= R) {
            // 如果左边 arr[p1] < arr[p2], 则p2右边的数全部比arr[p1]大，因为有序了
            // 算出后面有几个大于 p1, 再乘以 p1 的值，就得出 右边 对于左边 的所有小和
            ans += arr[p1] < arr[p2] ? (R - p2 + 1) * arr[p1] : 0;
            help[i++] = arr[p1] < arr[p2] ? arr[p1++] : arr[p2++];
        }

        while (p1 <= M) {
            help[i++] = arr[p1++];
        }
        while (p2 <= R) {
            help[i++] = arr[p2++];
        }

        for (int j = 0; j < help.length; j++) {
            arr[L + j] = help[j];
        }
        return ans;
    }
}
