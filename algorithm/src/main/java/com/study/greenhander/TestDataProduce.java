package com.study.greenhander;

public class TestDataProduce {
    public static void main(String[] args) {
       int[] arr =  lenRandomValueRandom(10, 100);
    }

    /**
     * @param maxLen  数组最大长度
     * @param maxValue 数组最大值
     */
    public static int[] lenRandomValueRandom(int maxLen, int maxValue) {
        int len = (int) (Math.random() * maxLen);
        int[] ans = new int[len];
        for (int i = 0; i < len; i++) {
            ans[i] = (int) (Math.random() * maxValue);
        }
        return ans;
    }
}
