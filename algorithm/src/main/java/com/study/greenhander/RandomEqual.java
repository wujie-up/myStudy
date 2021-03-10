package com.study.greenhander;

public class RandomEqual {
    public static void main(String[] args) {
        int time = 10000000;
        int[] counts = new int[8];
        for (int i = 0; i < time; i++) {
            int a = rand1_7();
            counts[a]++;
        }
        for (int i = 1; i < counts.length; i++) {
            System.out.println(i + "这个数，出现了 " + counts[i] + " 次");
        }
    }

    // 使用二进制表示 0~6等概率
    // 0-3 00-11 0-7 000-111 0-15 0000-1111 0-31 00000-11111
    private static int rand0_6() {
        int r;
        do {
            r = (rand0_1() << 2) + (rand0_1() << 1) + rand0_1(); // 每个位上的数都是等概率的
        } while (r == 7);
        return r;
    }
    private static int rand1_7() {
        return rand0_6() + 1;
    }

    // 实现等概率返回[0,1]
    // f() = 1-2 -> 0; f() = 4-5 -> 1; f() = 3 则丢弃循环
    private static int rand0_1() {
        int r;
        do {
            r = f();
        } while (r == 3);
        return r < 3 ? 0 : 1;
    }

    // 等概率返回 [1,5]
    public static int f() {
        return (int) (Math.random() * 5) + 1;
    }
}
