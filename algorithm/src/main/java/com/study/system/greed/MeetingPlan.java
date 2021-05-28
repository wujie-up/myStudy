package com.study.system.greed;

import java.util.Arrays;

/**
 * @description: 会议安排问题
 * @author: wj2wml@qq.com
 * @date: 2021-05-28 22:19
 **/
public class MeetingPlan {
    static class Meeting {
        int start;
        int end;

        public Meeting(int start, int end) {
            this.start = start;
            this.end = end;
        }
    }

    public static int getMaxMeeting1(Meeting[] arr) {
        if (arr == null || arr.length == 0) {
            return 0;
        }
        // 考虑(1,5) (5,5) 如果(5,5)排在前面，那么(1,5)就不能排 了
        Arrays.sort(arr, (o1, o2) -> {
            if (o1.end == o2.end) {
                return o1.start - o2.start;
            } else {
                return o1.end - o2.end;
            }
        });
        int end = 0;
        int count = 0;
        for (int i = 0; i < arr.length; i++) {
            if (arr[i].start >= end) {
                count++;
                end = arr[i].end;
            }
        }
        return count;
    }

    /**
     * 暴力解：每个项目都有两种选择，开或者不开，当前项目开，则后面的项目的开始时间 要 大于当前的完成时间才能开
     * 将所有能开的情况都罗列出，然后得出最优解
     */
    public static int getMeetingMax2(Meeting[] arr) {
        if (arr == null || arr.length == 0) {
            return 0;
        }
        return process(arr, 0, 0);
    }

    /**
     * @param count 已经安排的会议次数
     * @param end   上次的结束时间，当前时间
     * @return
     */
    private static int process(Meeting[] arr, int count, int end) {
        if (arr.length == 0) {
            return count;
        }
        int max = count;
        for (int i = 0; i < arr.length; i++) {
            if (arr[i].start >= end) {
                max = Math.max(max, process(excludedArr(arr, i), count + 1, arr[i].end));
            }
        }
        return max;
    }

    // 删除i位置，得到新的数组
    private static Meeting[] excludedArr(Meeting[] arr, int i) {
        Meeting[] newArr = new Meeting[arr.length - 1];
        int index = 0;
        for (int j = 0; j < arr.length; j++) {
            if (j != i) {
                newArr[index++] = arr[j];
            }
        }
        return newArr;
    }

    public static void main(String[] args) {
        int maxLength = 10;
        int maxTime = 1000;
        int times = 5000000;
        for (int i = 0; i < times; i++) {
            Meeting[] meetings = makeMeetings(maxLength, maxTime);
            int m1 = getMaxMeeting1(meetings);
            int m2 = getMeetingMax2(meetings);
            if (m1 != m2) {
                getMaxMeeting1(meetings);
                System.out.println("完犊子了！");
                break;
            }
        }
        System.out.println("success!!!");
    }

    private static Meeting[] makeMeetings(int maxLength, int maxTime) {
        int len = (int) (Math.random() * maxLength);
        Meeting[] arr = new Meeting[len];
        for (int i = 0; i < len; i++) {
            int start = (int) (Math.random() * maxTime);
            int end = (int) (Math.random() * maxTime);
            if (end < start) {
                int temp = start;
                start = end;
                end = temp;
            }
            arr[i] = new Meeting(start, end);
        }
        return arr;
    }
}
