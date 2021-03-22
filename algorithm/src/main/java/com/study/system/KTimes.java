package com.study.system;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

public class KTimes {

    public static int getKTimeNum(int[] arr, int k, int m) {
        Map<Integer, Integer> map = new HashMap<>();
        initMap(map);
        // 计算出arr中 所有数 0-31 位上每一位 1 的个数统计
        int[] count_1 = new int[32];
        for (int a : arr) {
            while (a != 0) {
                int rightOne = a & (-a); // 求出最右边1的值 比如：00000000100
                int index = map.get(rightOne); // 对应的数组索引
                count_1[index]++; // 对应索引上的值+1
                a ^= rightOne; // 通过 ^ 方式，去掉最右边的 1，继续找寻下个1的位置，直到 a == 0
            }
        }

        int ans = 0;
        for (int i = 0; i < 32; i++) {
            if (count_1[i] % m != 0) { // 不能被M整除，则必然里面 存在 K个1
                ans |= 1 << i; // 通过 | 添加对应位 上的1
                // 0010010001
                // 0100000000 |
                // 0110010001
            }
        }
        // 考虑arr 中出现k次的 数 是 0的情况
        if (ans == 0) {
            int count = 0;
            for (int a : arr) {
                if (a == 0) {
                    count++;
                }
            }
            // 判断 0 是否出现了k次
            if (count != k) {
                return -1;
            }
        }
        return ans;
    }

    // 求出int每位上为 1时，和数组索引的映射关系
    private static void initMap(Map<Integer, Integer> map) {
        int value = 1;
        for (int i = 0; i < 32; i++) {
            map.put(value, i);
            value = value << 1;
        }
    }

    // 建立随机生成 一个数 K 次 和 其他数M次的数组
    public static int[] randomArray(int maxKinds, int range, int k, int m) {
        int kTimeNum = randomNum(range);
        // 生成数的种类 至少 2种
        int kind = (int) (Math.random() * maxKinds) + 2;
        // 50%几率 没有K次的数 此时K == M
        k = Math.random() < 0.5 ? k : m;
        int[] arr = new int[k + (kind - 1) * m];
        // 生成k次数
        int index = 0;
        for (; index < k; index++) {
            arr[index] = kTimeNum;
        }
        kind--;
        // 生成 其他M次数
        HashSet set = new HashSet(); // 防止数据重复
        set.add(kTimeNum);
        while (kind != 0) {
            int mTimeNum;
            do {
                mTimeNum = randomNum(range);
            } while (set.contains(mTimeNum));
            for (int i = 0; i < m; i++) {
                arr[index++] = mTimeNum;
            }
            kind--;
        }

        // 打乱数组的顺序
        for (int i = 0; i < arr.length; i++) {
            int j = (int) (Math.random() * arr.length); // j = [0, length -1]
            int temp = arr[i];
            arr[i] = arr[j];
            arr[j] = temp;
        }
        return arr;
    }

    // [-rang,range]
    private static int randomNum(int range) {
        return (((int) (Math.random() * range) + 1) - ((int) (Math.random() * range) + 1));
    }


    public static int compareTest(int[] arr, int k, int m) {
        HashMap<Integer, Integer> map = new HashMap<>();
        for (int num : arr) {
            if (map.containsKey(num)) {
                map.put(num, map.get(num) + 1);
            } else {
                map.put(num, 1);
            }
        }

        for (Map.Entry<Integer, Integer> entry : map.entrySet()) {
            if (entry.getValue() == k) {
                return entry.getKey();
            }
        }
        return -1;
    }

    public static void main(String[] args) {
        int maxKind = 30; // 数的种类
        int range = 100; // 数的大小范围 正负
        int times = 50000;
        int max = 9;
        System.out.println("测试开始");

        for (int i = 0; i < times; i++) {
            int a = (int)(Math.random() * max) + 1;
            int b = (int)(Math.random() * max) + 1;
            int k = Math.min(a, b);
            int m = Math.max(a, b);
            // 保证m > k
            if (k == m) {
                m++;
            }
            int[] arr = randomArray(maxKind, range, k, m);
            int k1 = getKTimeNum(arr, k, m);
            int k2 = compareTest(arr, k , m);
            if (k1 != k2) {
                System.out.println(k1);
                System.out.println(k2);
                System.out.println("出错了");
            }
        }
        System.out.println("测试结束");
    }
}
