package com.study.system.recursion2dp;

import java.util.Random;

/**
 * @description: 数字转换字符问题
 * @author: wj2wml@qq.com
 * @date: 2021-06-15 22:01
 **/
public class Q4_NumberStr2CharStr {

    public static int number1(String s) {
        if (null == s || s.length() == 0) {
            return 0;
        }
        return process1(s.toCharArray(), 0);
    }

    private static int process1(char[] arr, int index) {
        if (index == arr.length) {
            return 1;
        }
        if (arr[index] == '0') { // 0不能和任何字符组合, 之前的选择是错误的
            return 0;
        }
        // 当前位置单独转
        int num = process1(arr, index + 1);
        // 当前位置和 后一个位置一起转, 需要判断 是否越界, 是否能转
        if (index + 1 < arr.length && ((arr[index] - '0') * 10 + (arr[index + 1] - '0') < 27)) {
            num += process1(arr, index + 2);
        }
        return num;
    }

    public static int number2(String s) {
        if (null == s || s.length() == 0) {
            return 0;
        }
        int N = s.length();
        int[] fmap = new int[N];
        for (int i = 0; i < N; i++) {
            fmap[i] = -1;
        }
        return process2(s.toCharArray(), 0, fmap);
    }

    private static int process2(char[] arr, int index, int[] fmap) {
        if (index == arr.length) {
            return 1;
        } else if (arr[index] == '0') {
            return 0;
        } else {
            if (fmap[index] != -1) {
                return fmap[index];
            }
            int ans = process2(arr, index + 1, fmap);
            if (index + 1 < arr.length && ((arr[index] - '0') * 10 + (arr[index + 1] - '0') < 27)) {
                ans += process1(arr, index + 2);
            }
            fmap[index] = ans;
            return ans;
        }
    }


    public static int number3(String s) {
        if (null == s || s.length() == 0) {
            return 0;
        }

        char[] arr = s.toCharArray();

        int N = s.length();
        int[] dp = new int[N + 1];
        dp[N] = 1;
        for (int i = N - 1; i >= 0; i--) {
            if (arr[i] != '0') {
                int num = dp[i + 1];
                if (i + 1 < arr.length && ((arr[i] - '0') * 10 + (arr[i + 1] - '0') < 27)) {
                    num += dp[i+2];
                }
                dp[i] = num;
            }

//            if (arr[index] == '0') {
//                return 0;
//            }
//            int num = process1(arr, index + 1);
//            if (index + 1 < arr.length && ((arr[index] - '0') * 10 + (arr[index + 1] - '0') < 27)) {
//                num += process1(arr, index + 2);
//            }
        }
        return dp[0];
    }

    public static void main(String[] args) {
        int maxLength = 20;
        int times = 200000;
        for (int i = 0; i < times; i++) {
            String s = generateStr(maxLength);
            int n1 = number1(s);
            int n2 = number2(s);
            int n3 = number3(s);
            if (n1 != n2 || n2 != n3) {
                System.out.println("完犊子了!!!");
                break;
            }
        }
        System.out.println("success!!!");
    }

    private static String generateStr(int maxLength) {
        int len = (int) (Math.random() * maxLength);
        StringBuilder sb = new StringBuilder();
        Random r = new Random();
        for (int i = 0; i < len; i++) {
            sb.append(r.nextInt(27));
        }
        return sb.toString();
    }

}
