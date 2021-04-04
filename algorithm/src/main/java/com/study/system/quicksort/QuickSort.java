package com.study.system.quicksort;

import java.util.Arrays;

public class QuickSort {

    public static void main(String[] args) {
        int maxLen = 10;
        int maxValue = 2000;
        int times = 888888;
        for (int i = 0; i < times; i++) {
            int[] arr = randomArr(maxLen, maxValue);
            sort(arr);
            if (!test(arr)) {
                System.out.println("oops !!!");
                System.out.println(Arrays.toString(arr));
                break;
            }
        }
    }

    private static boolean test(int[] arr) {
        if (null == arr || arr.length < 1) {
            return true;
        }
        int max = arr[0];
        for (int i = 1; i < arr.length; i++) {
            if (arr[i] >= max) {
                max = arr[i];
            } else {
                return false;
            }
        }
        return true;
    }

    public static int[] randomArr(int maxLen, int maxValue) {
        int len = (int) (Math.random() * maxLen);
        int[] arr = new int[len];
        for (int i = 0; i < len; i++) {
            arr[i] = (int) (Math.random() * maxValue);
        }
        return arr;
    }

    public static void sort(int[] arr) {
        if (null == arr || arr.length < 1) {
            return;
        }
        process3(arr, 0, arr.length - 1);
    }

    public static void process1(int[] arr, int L, int R) {
        if (L >= R) {
            return;
        }
        // 将arr 最右边的数看成num，使用荷兰国旗问题解决方法找到n小于num的边界
        int M = getLessPartition(arr, L, R);
        process1(arr, L, M - 1);
        process1(arr, M + 1, R);
    }

    public static void process2(int[] arr, int L, int R) {
        if (L >= R) {
            return;
        }
        // 将arr 最右边的数看成num，使用荷兰国旗问题解决方法找到n小于num的边界
        int[] M = getEqualRange(arr, L, R);
        process2(arr, L, M[0] - 1);
        process2(arr, M[1] + 1, R);
    }

    public static void process3(int[] arr, int L, int R) {
        if (L >= R) {
            return;
        }
        // 随机指定比较数
        swap(arr, L + (int) (Math.random() * (R - L + 1)), R);
        int[] M = getEqualRange(arr, L, R);
        process3(arr, L, M[0] - 1);
        process3(arr, M[1] + 1, R);
    }

    private static int[] getEqualRange(int[] arr, int L, int R) {
        if (L > R) {
            return new int[]{-1, -1};
        }
        if (L == R) {
            return new int[] {L, L};
        }
        int less = L - 1;
        int more = R;
        int cur = L;

        while (cur < more) {
            if (arr[cur] < arr[R]) {
                swap(arr, cur++, ++less);
            } else if (arr[cur] == arr[R]) {
                cur++;
            } else {
                swap(arr, cur, --more);
            }
        }

        swap(arr, R, more);
        return new int[] {less + 1, more - 1};
    }

    private static int getLessPartition(int[] arr, int L, int R) {
        if (L > R) {
            return -1;
        }
        if (L ==R) {
            return L;
        }

        int less = L - 1;
        int cur = L;

        while (cur < R) {
            if (arr[cur] <= arr[R]) {
                swap(arr, cur, ++less);
            }
            cur++;
        }
        // 将R位置与 more位置交换
        swap(arr, R, ++less);
        return less;
    }

    private static void swap(int[] arr, int a, int b) {
        if (a == b) {
            return;
        }
        int temp = arr[a];
        arr[a] = arr[b];
        arr[b] = temp;
    }
}
