package com.study.system.recursion;

import java.util.ArrayList;
import java.util.List;

/**
 * @description: 打印字符串全部序列，无重复
 * @author: wj2wml@qq.com
 * @date: 2021-06-13 20:54
 **/
public class PrintAllPermutationsNoRepeat {
    public static List<String> permutation3(String s) {
        List<String> ans = new ArrayList<>();
        if (null == s || s.length() < 1) {
            return ans;
        }
        func(s.toCharArray(), 0, ans);
        return ans;
    }

    private static void func(char[] array, int index, List<String> ans) {
        if (index == array.length) {
            ans.add(new String(array));
        } else {
            // 当前的分支下已经被选择计算过的字符, 剪枝操作
            boolean[] chosenChar = new boolean[256];
            for (int i = index; i < array.length; i++) {
                if (!chosenChar[array[i]]) {
                    chosenChar[array[i]] = true;
                    swap(array, index, i);
                    func(array, index + 1, ans);
                    swap(array, index, i);
                }
            }
        }
    }

    private static void swap(char[] array, int a, int b) {
        char temp = array[a];
        array[a] = array[b];
        array[b] = temp;
    }

    public static void main(String[] args) {
        String s = "acc";
        List<String> ans = permutation3(s);
        System.out.println(ans);
    }
}
