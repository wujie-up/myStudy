package com.study.system.recursion2dp;

import java.util.*;

/**
 * @description: 贴纸拼字符串
 * @author: wj2wml@qq.com
 * @date: 2021-06-17 20:27
 **/
public class Q5_StickerSpellingStr {

    public static int minSpell1(String[] stickers, String str) {
        if (null == str || str.length() == 0) {
            return 0;
        }

        if (null == stickers || stickers.length == 0) {
            return 0;
        }
        int ans = process1(stickers, str);
        return ans == Integer.MAX_VALUE ? -1 : ans;
    }

    // stickers 剩余所有的贴纸
    // restStr  剩余的字符
    public static int process1(String[] stickers, String target) {
        if (target.length() == 0) {
            return 0;
        }
        int min = Integer.MAX_VALUE;
        for (String sticker : stickers) {
            String rest = minus1(target, sticker);
            if (!rest.equals(target)) { // 当前贴纸 有目标字符
                min = Math.min(min, process1(stickers, rest));
            }
        }
        return min + (min == Integer.MAX_VALUE ? 0 : 1);
    }

    private static String minus1(String target, String sticker) {
        char[] c1 = target.toCharArray();
        char[] c2 = sticker.toCharArray();
        int[] count = new int[26];
        for (char c : c1) {
            count[c - 'a']++;
        }
        for (char c : c2) {
            count[c - 'a']--;
        }

        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 26; i++) {
            if (count[i] > 0) {
                for (int j = 0; j < count[i]; j++) {
                    sb.append((char) (i + 'a'));
                }
            }
        }
        return sb.toString();
    }


    public static int minSpell2(String[] stickers, String str) {
        if (null == str || str.length() == 0) {
            return 0;
        }

        if (null == stickers || stickers.length == 0) {
            return 0;
        }

        int[][] arr = new int[stickers.length][26];

        for (int i = 0; i < stickers.length; i++) {
            arr[i] = strToArr(stickers[i]);
        }

        int ans = process2(arr, str);
        return ans == Integer.MAX_VALUE ? -1 : ans;
    }

    private static int process2(int[][] stickers, String target) {
        if (target.length() == 0) {
            return 0;
        }
        char[] targets = target.toCharArray();
        int[] tArr = new int[26];
        for (char c : targets) {
            tArr[c - 'a']++;
        }

        int min = Integer.MAX_VALUE;

        for (int i = 0; i < stickers.length; i++) {
            int[] sticker = stickers[i];
            // 只有 含有 target 中第一个字符的 贴纸才进行计算。【可能】 避免了个别 没用贴纸 参与运算的过程
            if (sticker[targets[0] - 'a'] > 0) {
                String rest = minus2(sticker, tArr);
                min = Math.min(min, process2(stickers, rest));
            }
        }
        return min + (min == Integer.MAX_VALUE ? 0 : 1);
    }

    private static String minus2(int[] sticker, int[] tArr) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 26; i++) {
            int nums = tArr[i] - sticker[i];
            for (int j = 0; j < nums; j++) {
                sb.append((char) (i + 'a'));
            }
        }
        return sb.toString();
    }

    private static int[] strToArr(String str) {
        int[] arr = new int[26];
        char[] chars = str.toCharArray();
        for (char c : chars) {
            arr[c - 'a']++;
        }
        return arr;
    }


    public static int minSpell3(String[] stickers, String str) {
        if (null == str || str.length() == 0) {
            return 0;
        }

        if (null == stickers || stickers.length == 0) {
            return 0;
        }

        int[][] arr = new int[stickers.length][26];

        for (int i = 0; i < stickers.length; i++) {
            arr[i] = strToArr(stickers[i]);
        }

        Map<String, Integer> dp = new HashMap<>();
        dp.put("", 0);
        int ans = process3(arr, str, dp);
        return ans == Integer.MAX_VALUE ? -1 : ans;
    }

    private static int process3(int[][] stickers, String target, Map<String, Integer> dp) {
        if (dp.containsKey(target)) {
            return dp.get(target);
        }

        char[] targets = target.toCharArray();
        int[] tArr = new int[26];
        for (char c : targets) {
            tArr[c - 'a']++;
        }

        int min = Integer.MAX_VALUE;
        for (int i = 0; i < stickers.length; i++) {
            int[] sticker = stickers[i];
            if (sticker[targets[0] - 'a'] > 0) {
                String rest = minus3(sticker, tArr);
                min = Math.min(min, process3(stickers, rest, dp));
            }
        }
        min = min + (min == Integer.MAX_VALUE ? 0 : 1);
        dp.put(target, min);
        return min;
    }

    private static String minus3(int[] sticker, int[] tArr) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 26; i++) {
            int nums = tArr[i] - sticker[i];
            for (int j = 0; j < nums; j++) {
                sb.append((char) (i + 'a'));
            }
        }
        return sb.toString();
    }
}
