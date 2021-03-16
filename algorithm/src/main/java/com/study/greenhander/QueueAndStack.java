package com.study.greenhander;

import java.util.LinkedList;
import java.util.Queue;
import java.util.Stack;

public class QueueAndStack {
    public static class Node<V> {
        public V value;
        public Node<V> next;

        public Node(V v) {
            value = v;
            next = null;
        }
    }

    public static class Stack<V> {
        Node<V> head;
        int size;

        public void push(V data) {
            Node add = new Node(data);
            if (head == null) {
                head = add;
            } else {
                add.next = head;
                head = add;
            }
            size++;
        }

        public V pop() {
            V data = null;
            if (head != null) {
                data = head.value;
                head = head.next;
                size--;
            }
            return data;
        }

        public boolean isEmpty() {
            return size == 0;
        }
    }

    public static class Queue<V> {
        Node<V> head;
        Node<V> tail;
        int size;

        public void put(V data) {
            Node add = new Node(data);
            if (head == null) {
                head = tail = add;
            } else {
                tail.next = add;
                tail = add;
            }
            size++;
        }

        public V poll() {
            V data = null;
            if (head != null) {
                data = head.value;
                head = head.next;
                size--;
            }
            if (head == null) {
                tail = null; // 拿取最后一个节点时，需要释放tail引用
            }
            return data;
        }

        public boolean isEmpty() {
            return size == 0;
        }
    }


    public static void main(String[] args) {
        testQueue();
        testStack();
    }

    private static void testStack() {
        int maxValue = 600000;
        int times = 60000;
        Stack<Integer> myStack = new Stack<>();
        java.util.Stack<Integer> test = new java.util.Stack<>();

        System.out.println("栈测试开始！");
        for (int i = 0; i < times; i++) {
            double decide = Math.random();
            if (decide < 0.33) {
                int num = (int) (Math.random() * maxValue);
                myStack.push(num);
                test.push(num);
            } else {
                if (!myStack.isEmpty()) {
                    int num1 = myStack.pop();
                    int num2 = test.pop();
                    if (num1 != num2) {
                        System.out.println("Oops!");
                    }
                }
            }
        }
        if (myStack.size != test.size()) {
            System.out.println("Oops!");
        }
        System.out.println("栈测试结束");
    }

    public static void testQueue() {
        int maxValue = 500000;
        int times = 50000;
        Queue<Integer> myQueue = new Queue<>();
        java.util.Queue<Integer> test = new LinkedList<>();
        System.out.println("队列测试开始！");
        for (int i = 0; i < times; i++) {
            double decide = Math.random();
            if (decide < 0.33) {
                int num = (int) (Math.random() * maxValue);
                myQueue.put(num);
                test.offer(num);
            } else {
                if (!myQueue.isEmpty()) {
                    int num1 = myQueue.poll();
                    int num2 = test.poll();
                    if (num1 != num2) {
                        System.out.println("Oops!");
                    }
                }
            }
        }
        if (myQueue.size != test.size()) {
            System.out.println("Oops!");
        }
        System.out.println("队列测试结束");
    }
}
