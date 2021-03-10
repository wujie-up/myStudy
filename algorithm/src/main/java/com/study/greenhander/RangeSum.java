package com.study.greenhander;

public class RangeSum {
    public static void main(String[] args) {
        int arr[] = {2, 8, 9, 6, 10, 8};
//        int[][] dArr = initDArr(arr);
//        int l = 1;
//        int r = 4;
//        System.out.println(dArr[l][r]);

        int[] preSum = preSum(arr);
        int l = 1;
        int r = 4;
        System.out.println(preSum[r] - preSum[l - 1]);
    }

    public static int[][] initDArr(int[] arr) {
        int len = arr.length;
        int[][] dArr = new int[len][len];
        for (int i = 0; i < len; i++) {
            for (int j = i; j < len; j++) {
                if (i == j) {
                    dArr[i][j] = arr[i];
                } else {
                    dArr[i][j] = dArr[i][j - 1] + arr[j];
                }
            }
        }
        return dArr;
    }

    public static int[] preSum(int[] arr) {
        int len = arr.length;
        int[] preSum = new int[len];
        preSum[0] = arr[0];
        for (int i = 1; i < len; i++) {
            preSum[i] = preSum[i - 1] + arr[i];
        }
        return preSum;
    }
}
