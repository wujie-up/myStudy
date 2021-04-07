package com.study.system.heep;

import java.util.PriorityQueue;

public class Heap {
    private int[] arr;
    private int heapSize;
    private int limit;

    public Heap(int size) {
        this.arr = new int[size];
        this.heapSize = 0;
        this.limit = size;
    }

    public boolean isEmpty() {
        return heapSize == 0;
    }

    public boolean isFull() {
        return heapSize == limit;
    }

    public int size() {
        return heapSize;
    }

    public void push(int data) {
        if (isFull()) {
            throw new RuntimeException("满了");
        }
        arr[heapSize] = data;
        heapInsert(arr, heapSize++);
    }

    public int pop() {
        if (isEmpty()) {
            throw new RuntimeException("没了");
        }
        int ans = arr[0];
        // 将最后一个数 补充到 头位置，然后堆长度减1
        swap(arr, 0, --heapSize);
        // 从头做一次堆化
        heapify(arr, 0, heapSize);
        return ans;
    }

    /**
     * 自下而上（与自己的父节点比较）
     */
    public void heapInsert(int[] arr, int index) {
        //注意：(0 - 1) / 2 == 0， 所以 (index - 1) / 2 和index 最小都为 0，为0时不满足循环条件
        while (arr[index] < arr[(index - 1) / 2]) {
            swap(arr, index, (index - 1) / 2);
            index = (index - 1) / 2;
        }
    }

    /**
     * 自上而下（与自己的左右儿子比较）
     *  适用于整个数组做一次完整的堆化
     */
    public void heapify(int[] arr, int index, int heapSize) {
        int left = index * 2 + 1;
        // 左节点位置不能超过堆大小
        while (left < heapSize) {
            // 算出左右节点最小值（需要判断是否有右节点）
            int smaller = left + 1 < heapSize && arr[left + 1] < arr[left] ? left + 1 : left;
            if (arr[index] > arr[smaller]) {
                swap(arr, smaller, index);
                index = smaller;
                left = index * 2 + 1;
            } else {
                break;
            }
        }
    }

    private void swap(int[] arr, int a, int b) {
        int temp = arr[a];
        arr[a] = arr[b];
        arr[b] = temp;
    }


    public static void main(String[] args) {
        int maxLen = 15;
        int maxValue = 5000;
        int times = 8888888;


        for (int i = 0; i < times; i++) {
            int[] arr =  randomArr(maxLen, maxValue);
            test(arr);
        }
    }

    private static void test(int[] arr) {
        PriorityQueue<Integer> heap = new PriorityQueue<>();
        Heap myHeap = new Heap(arr.length);
        for (int i = 0; i < arr.length; i++) {
            heap.add(arr[i]);
            myHeap.push(arr[i]);
        }

        if (heap.size() != myHeap.size()) {
            System.out.println("oops1 !!!");
        }

        for (int i = 0; i < arr.length; i++) {
            int a = heap.poll();
            int b = myHeap.pop();
            if (a!= b) {
                System.out.println("oops !!!");
                return;
            }
        }
    }

    private static int[] randomArr(int maxLen, int maxValue) {
        int len = (int) (Math.random() * maxLen);
        int[] arr = new int[len];
        for (int i = 0; i < len; i++) {
            arr[i] = (int) (Math.random() * maxValue);
        }
        return arr;
    }
}
