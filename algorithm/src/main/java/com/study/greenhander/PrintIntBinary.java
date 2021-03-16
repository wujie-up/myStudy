package com.study.greenhander;

/**
 * 打印Int数对应的二进制数据, int 32位
 * 负数：除了符号位，其他取反 后 + 1
 */
public class PrintIntBinary {
    public static void main(String[] args) {
        int a = 5133222;
        int b = 150335;
//        print(a);
//        print(b);
//        System.out.println("===================================");
//        print(a | b);
//        print(a & b);
//        print(a ^ b);
        print(1<<5);


        // 11111111111111111111111111110101
        // 10000000000000000000000000001011
    }

    public static void print(int num) {
        for (int i = 31; i >= 0; i--) {
            System.out.print((num & (1 << i)) == 0 ? "0" : "1");
        }
        System.out.println();
    }
}
