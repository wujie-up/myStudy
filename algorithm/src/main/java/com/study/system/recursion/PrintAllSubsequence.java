package com.study.system.recursion;

/**
 * @description: 打印字符串所有子序列
 * @author: wj2wml@qq.com
 * @date: 2021-06-11 22:52
 **/
public class PrintAllSubsequence {
    public static void print(String s) {
        if (s == null || s.length() < 1) {
            return;
        }
        char[] str = s.toCharArray();
        func(str, 0, "");
    }

    /**
     * @param: str    字符数组
     * @param: index  当前来到的位置
     * @param: path   形成的路径（也就是拼接的字符串）
     **/
    public static void func(char[] str, int index, String path) {
        if (index == str.length) {
            // 越界了，直接打印path
            System.out.println(path);
            return;
        }
        // 当前位置 要
        func(str, index + 1, path + str[index]);
        // 当前位置 不要
        func(str, index + 1, path);
    }

    public static void main(String[] args) {
        print("accc");
    }
}
