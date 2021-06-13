package com.study.system.recursion;

import java.util.ArrayList;
import java.util.List;

/**
 * @description: 打印字符串全部序列
 * @author: wj2wml@qq.com
 * @date: 2021-06-13 20:15
 **/
public class PrintAllPermutations {

    public static List<String> permutation1(String s) {
        List<String> ans = new ArrayList<>();
        if (s == null || s.length() < 1) {
            return ans;
        }

        char[] chars = s.toCharArray();

        List<Character> charList = new ArrayList<>();
        for (char c : chars) {
            charList.add(c);
        }

        func1(charList, "", ans);
        return ans;
    }

    /**
     * @param rest 剩下的字符集
     * @param path 前面作出选择后的结果
     * @param ans
     */
    private static void func1(List<Character> rest, String path, List<String> ans) {
        if (rest.isEmpty()) {
            ans.add(path);
        } else {
            int size = rest.size();
            for (int i = 0; i < size; i++) {
                Character cur = rest.get(i);
                rest.remove(cur);
                func1(rest, path + cur, ans);
                // 恢复现场，删除只能对当前分支后面的计算产生影响，不能影响所有分支
                rest.add(cur);
            }
        }
    }


    private static List<String> permutation2(String s) {
        List<String> ans = new ArrayList<>();
        if (s == null || s.length() < 1) {
            return ans;
        }
        char[] chars = s.toCharArray();
        func2(chars, 0, ans);
        return ans;
    }

    private static void func2(char[] chars, int index, List<String> ans) {
        if (index == chars.length) {
            ans.add(new String(chars));
        } else {
            // index前面是已经做出选择了的
            // index位置 可以是 后面 任意的一个元素
            for (int i = index; i < chars.length; i++) {
                swap(chars, i, index);
                func2(chars, index + 1, ans);
                // 恢复现场
                swap(chars, i, index);
            }
        }
    }

    private static void swap(char[] chars, int a, int b) {
        char temp = chars[a];
        chars[a] = chars[b];
        chars[b] = temp;
    }

    public static void main(String[] args) {
        String s = "acc";
        List<String> ans1 = permutation1(s);
        System.out.println(ans1);

        List<String> ans2 = permutation2(s);
        System.out.println(ans2);
    }

}
