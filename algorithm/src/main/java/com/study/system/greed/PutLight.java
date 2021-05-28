package com.study.system.greed;

import java.util.HashSet;

/**
 * @description: 放灯问题
 * @author: wj2wml@qq.com
 * @date: 2021-05-27 20:41
 **/
public class PutLight {

    public static int minLight1(String str) {
        if (null == str || str.length() == 0) {
            return 0;
        }
        char[] chars = str.toCharArray();
        int count = 0;
        int cur = 0;
        int N = chars.length;
        for (; cur < N; ) {
            if (chars[cur] == 'X') {
                cur++;
            } else {
                count++;
                // 当前已经是最后一个位置
                if (cur + 1 == N) {
                    break;
                }
                // 如果下个位置是居民点，则在下个位置点灯
                if (chars[cur + 1] == '.') {
                    cur += 3;
                } else {
                    // 在当前位置放灯，那么下个位置会把照亮，则不需要考虑
                    cur += 2;
                }

            }
        }
        return count;
    }

    /**
     * 暴力递归解法, 求出所有的可能性，最后返回最小值
     */
    public static int minLight2(String str) {
        if (null == str || str.length() == 0) {
            return 0;
        }
        return process(str.toCharArray(), 0, new HashSet<>());
    }

    /**
     * @param lights 用来存放已经放灯的位置
     */
    private static int process(char[] arr, int index, HashSet<Integer> lights) {
        if (index == arr.length) {
            // 处理完了（注意这里已经越界了），然后遍历数组，查看每个位置的点灯情况
            for (int i = 0; i < arr.length; i++) {
                // 如果某个位置 是点 ，即需要点灯，而其前后都没有点灯，
                // 则这种结果是错误的，返回一个最大值以便上层淘汰
                if (arr[i] != 'X') {
                    if (!lights.contains(i - 1) && !lights.contains(i) && !lights.contains(i + 1)) {
                        return Integer.MAX_VALUE;
                    }
                }
            }
            return lights.size();
        } else { // 还没有处理完
            // 当前位置不放灯，递归处理后面的位置
            int no = process(arr, index + 1, lights);
            // 当前位置放灯, 必须满足 . 才能放灯
            int yes = Integer.MAX_VALUE;
            if (arr[index] == '.') {
                lights.add(index);
                yes = process(arr, index + 1, lights);
                // 递归回到这里，需要从已点亮位置中去掉当前位置 ，
                // 因为当前位置加入只能算在当前这个递归分支情况下，lights是所有分支共享的
                // 如果不移除，则影响其他分支的校验
                lights.remove(index);
            }
            // 返回两种情况的最小值
            return Math.min(no, yes);
        }
    }

    public static void main(String[] args) {
        // 字符串最大长度
        int maxLength = 15;
        int times = 555555;
        for (int i = 0; i < times; i++) {
            String str = makeStr(maxLength);
            int i1 = minLight1(str);
            int i2 = minLight2(str);
            if (i1 != i2) {
                System.out.println("完犊子了！");
                break;
            }
        }
        System.out.println("success!!!");
    }

    private static String makeStr(int maxLength) {
        int len = (int) (Math.random() * maxLength);
        String str = "";
        for (int i = 0; i < len; i++) {
            str += Math.random() < 0.5 ? "." : "X";
        }
        return str;
    }
}
