package com.study.system.merge;

import java.util.Arrays;

public class CountOfRangeSum {

    public static void main(String[] args) {
        int maxLen = 5;
        int maxValue = 20;
        int times = 888888;
        for (int i = 0; i < times; i++) {
            int low = (int) (Math.random() * (maxValue / 2));
            int up = (int) (Math.random() * (maxValue / 2)) + maxValue / 2;
            int[] arr = randomArr(maxLen, maxValue);
            int sum1 = test(arr, low, up);
            int sum2 = count(arr, low, up);

            if (sum1 != sum2) {
                System.out.println("oops !!!!");
                System.out.println("low:" + low +"; up:" + up);
                System.out.println(sum1 +"  "+ sum2);
                System.out.println(Arrays.toString(arr));
                break;
            }
        }

    }

    private static int  test(int[] arr, int low, int up) {
        int ans = 0;
        int[] preSum = new int[arr.length];
        preSum[0] = arr[0];
        for (int i = 1; i < preSum.length; i++) {
            preSum[i] = preSum[i - 1] + arr[i];
        }

        for (int i = 0; i < preSum.length; i++) {
            if (preSum[i] >= low && preSum[i] <= up) {
                ans++;
            }
            for (int j = i + 1; j < preSum.length; j++) {
                int s = preSum[j] - preSum[i];
                if (s >= low && s <= up) {
                    ans++;
                }
            }
        }
        return ans;
    }


    private static int[] randomArr(int maxLen, int maxValue) {
        int len = (int) (Math.random() * maxLen) + 1;
        int[] arr = new int[len];
        for (int i = 0; i < len; i++) {
            arr[i] = (int) (Math.random() * maxValue) + 1;
        }
        return arr;
    }


    public static int count(int[] arr, int low, int up) {

        if (arr == null || arr.length == 0) {
            return 0;
        }

        int[] sum = new int[arr.length];
        sum[0] = arr[0];
        for (int i = 1; i < sum.length; i++) {
            sum[i] = sum[i - 1] + arr[i];
        }

        return process(sum, 0, sum.length - 1, low, up);
    }

    private static int process(int[] sum, int l, int r, int low, int up) {
        if (l == r) {
            return sum[l] >= low && sum[l] <= up ? 1 : 0;
        }
        int m = (l + r) / 2;
        return process(sum, l, m, low, up) +
                process(sum, m + 1, r, low, up) +
                merge(sum, l, m, r, low, up);
    }

    private static int merge(int[] sum, int l, int m, int r, int low, int up) {
        int ans = 0;
        int windowL = l;
        int windowR = l;

        for (int i = m + 1; i <= r; i++) {
            int min = sum[i] - up;
            int max = sum[i] - low;

            while (windowL <= m && sum[windowL] < min) {
                windowL++;
            }
            while (windowR <= m && sum[windowR] <= max) {
                windowR++;
            }
            ans += windowR - windowL;
        }

        int[] help = new int[r - l + 1];
        int p1 = l;
        int p2 = m + 1;
        int j = 0;
        while (p1 <= m && p2 <= r) {
            help[j++] = sum[p1] < sum[p2] ? sum[p1++] : sum[p2++];
        }
        while (p1 <= m) {
            help[j++] = sum[p1++];
        }
        while (p2 <= r) {
            help[j++] = sum[p2++];
        }

        for (int k = 0; k < help.length; k++) {
            sum[l + k] = help[k];
        }
        return ans;
    }
}
