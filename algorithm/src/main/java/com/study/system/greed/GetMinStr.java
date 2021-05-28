package com.study.system.greed;

import java.util.Arrays;
import java.util.Random;
import java.util.TreeSet;

/**
 * @description: 获得字典序最小字符串
 * @author: wj2wml@qq.com
 * @date: 2021-05-26 20:59
 **/
public class GetMinStr {
    private static Random r = new Random();

    public static String getMinStr1(String[] strs) {
        if (null == strs || strs.length == 0) {
            return "";
        }
        Arrays.sort(strs, (a, b) -> (a + b).compareTo(b + a));
        String ans = "";
        for (String str : strs) {
            ans += str;
        }
        return ans;
    }

    /**
     * 暴力解：将所有结合情况都列出，添加到TreeSet容器中
     */
    public static String getMinStr2(String[] strs) {
        if (null == strs || strs.length == 0) {
            return "";
        }

        TreeSet<String> set = process(strs);
        return set.first();
    }

    private static TreeSet<String> process(String[] strs) {
        TreeSet<String> set = new TreeSet<>();
        if (strs.length == 0) {
            set.add("");
            return set;
        }

        for (int i = 0; i < strs.length; i++) {
            String cur = strs[i]; // 当前字符串
            String[] others = getLeftStrs(strs, i);
            // 剩余的字符串
            TreeSet<String> otherSet = process(others);
            for (String other : otherSet) {
                set.add(cur + other);
            }
        }
        return set;
    }

    private static String[] getLeftStrs(String[] strs, int index) {
        String[] newStrs = new String[strs.length - 1];
        int newIndex = 0;
        for (int i = 0; i < strs.length; i++) {
            if (i != index) {
                newStrs[newIndex++] = strs[i];
            }
        }
        return newStrs;
    }


    public static void main(String[] args) {
        // 字符串最大长度
        int maxLength = 6;
        // 数组最大长度
        int maxSize = 6;
        int times = 10000;
        for (int i = 0; i < times; i++) {
            String[] strs = makeStringArr(maxLength, maxSize);
            String[] strs2 = copyStrs(strs);
            String s1 = getMinStr1(strs);
            String s2 = getMinStr2(strs2);
            if (!s1.equals(s2)) {
                System.out.println("完犊子了！");
                break;
            }
        }
        System.out.println("success!!!");
    }

    private static String[] copyStrs(String[] strs) {
        String[] strs2 = new String[strs.length];
        for (int i = 0; i < strs.length; i++) {
            strs2[i] = strs[i];
        }
        return strs2;
    }

    private static String[] makeStringArr(int maxLength, int maxSize) {
        maxSize = (int) (Math.random() * maxSize);
        if (maxSize == 0) {
            return new String[0];
        }


        String[] ans = new String[maxSize];
        for (int i = 0; i < maxSize; i++) {
            ans[i] = newString(maxLength);
        }
        return ans;
    }

    private static String newString(int maxLength) {
       int length = (int) (Math.random() * (maxLength - 1)) + 1;
       char[] chars = new char[length];
        for (int i = 0; i < length; i++) {
            int value = (int) (Math.random() * 5);
            chars[i] = Math.random() < 0.5 ? (char) (65 + value) : (char) (97 + value);
        }
        return new String(chars);
    }
}
