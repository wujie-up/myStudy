package com.study.greenhander;

import org.junit.Assert;

import java.util.Deque;
import java.util.LinkedList;

public class DoubleToDeque {
    public static class Node<V> {
        V value;
        Node<V> last;
        Node<V> next;

        public Node(V v) {
            value = v;
        }
    }

    public static class Deque<V> {
        Node<V> head;
        Node<V> tail;
        int size;

        public boolean isEmpty() {
            return size == 0;
        }

        public void pushHead(V data) {
            Node node = new Node(data);
            if (head == null) {
                head = tail = node;
            } else {
                node.next = head;
                head.last = node;
                head = node;
            }
            size++;
        }

        public void pushTail(V data) {
            Node node = new Node(data);
            if (head == null) {
                head = tail = node;
            } else {
                tail.next = node;
                node.last = tail;
                tail = node;
            }
            size++;
        }

        public V popHead() {
            V ans = null;
            if (head != null) {
                ans = head.value;
                if (head == tail) {
                    head = tail = null;
                } else {
                    head = head.next;
                    head.last = null;
                }
                size--;
            }
            return ans;
        }

        public V popTail() {
            V ans = null;
            if (tail != null) {
                ans = tail.value;
                if (head == tail) {
                    head = tail = null;
                } else {
                    tail = tail.last;
                    tail.next = null;
                }
                size--;
            }
            return ans;
        }
    }

    public static void test() {
        Deque<Integer> deque = new Deque<>();
        java.util.Deque<Integer> test = new LinkedList<>();

        int maxValue = 600000;
        int times = 5000000;
        for (int i = 0; i < times; i++) {
            double decide = Math.random();
            if (decide < 0.33) {
                int num = (int) (Math.random() * maxValue);
                if (Math.random() < 0.5) {
                    deque.pushHead(num);
                    test.addFirst(num);
                } else {
                    deque.pushTail(num);
                    test.addLast(num);
                }
            } else {
                if (!deque.isEmpty()) {
                    int num1 = 0;
                    int num2 = 0;
                    if (Math.random() < 0.5) {
                        num1 = deque.popHead();
                        num2 = test.pollFirst();
                    } else {
                        num1 = deque.popTail();
                        num2 = test.pollLast();
                    }
                    if (num1 != num2) {
                        Assert.fail();
                    }
                }
            }
        }

        if (deque.size != test.size()) {
            Assert.fail();
        }
    }

    public static void main(String[] args) {
        test();
    }
}
