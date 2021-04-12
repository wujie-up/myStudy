package com.study.system.heep;

import java.util.Arrays;
import java.util.Comparator;
import java.util.PriorityQueue;

public class CoverMax {

    /**
     * 以0.5为间隔，统计重合个数{{0,5},{1,4}}
     */
    public static int cover1(int[][] lines) {
        // 先找到所有线段的最小值和最大值
        int min = Integer.MAX_VALUE;
        int max = Integer.MIN_VALUE;
        for (int i = 0; i < lines.length; i++) {
            min = Math.min(lines[i][0], min);
            max = Math.max(lines[i][1], max);
        }
        int count = 0;

        // min - min + 1, min + 1 - min + 2 统计没有区间的重合线段数量
        for (double p = min + 0.5; p < max; p += 1) {
            int cur = 0;
            for (int i = 0; i < lines.length; i++) {
                if (lines[i][0] < p && lines[i][1] > p) {
                    cur++;
                }
            }
            count = Math.max(count, cur);
        }
        return count;
    }

    public static int cover2(int[][] arr) {
        Line[] lines = new Line[arr.length];
        for (int i = 0; i < lines.length; i++) {
            lines[i] = new Line(arr[i][0], arr[i][1]);
        }
        // 按起始从小到大排序
        Arrays.sort(lines, Comparator.comparingInt(o -> o.start));

        // 准备一个小根堆, 按end排序
        PriorityQueue<Integer> heap = new PriorityQueue<>();

        int count = 0;
        for (int i = 0; i < lines.length; i++) {
            Line line = lines[i];
            // 将 当前 line start 与堆中 line的end 比较, start >= end则弹出堆
            while (!heap.isEmpty() && line.start >= heap.peek()) {
                heap.poll();
            }
            heap.add(line.end);
            count = Math.max(count, heap.size());
        }
        return count;
    }

    static class Line {
        int start;
        int end;

        public Line(int start, int end) {
            this.start = start;
            this.end = end;
        }
    }

    /*----------------------------------------------- 测试代码  ---------------------------*/
    public static void main(String[] args) {
        int maxLen = 10;
        int maxValue = 5000;
        int times = 200000;

        System.out.println("test begin");
        for (int i = 0; i < times; i++) {
            int[][] arr = randomArr2(maxLen, 0, maxValue);
            int c1 = cover1(arr);
            int c2 = cover2(arr);
            if (c1 != c2) {
                System.out.println("oops !!!");
                break;
            }
        }

        System.out.println("test end");
    }

    private static int[][] randomArr2(int maxLen, int L, int R) {
        int len = (int) (Math.random() * maxLen) + 1;
        int[][] arr = new int[len][2];
        for (int i = 0; i < arr.length; i++) {
            int a = L + (int) (Math.random() * (R - L + 1));
            int b = L + (int) (Math.random() * (R - L + 1));
            if (a == b) {
                b++;
            }
            arr[i][0] = Math.min(a, b);
            arr[i][1] = Math.max(a, b);
        }
        return arr;
    }
}
