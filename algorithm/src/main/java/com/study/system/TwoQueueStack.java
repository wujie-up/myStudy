package com.study.system;

import java.util.LinkedList;
import java.util.Queue;

public class TwoQueueStack<E> {
    Queue<E> queue = new LinkedList<>();
    Queue<E> help = new LinkedList<>();

    public void push(E e) {
        queue.add(e);
    }

    public E pop() {
        while (queue.size() > 1) {
            E e = queue.poll();
            help.add(e);
        }
        E ans = queue.poll();
        // 交换 辅助队列和 数据队列的引用
        Queue<E> temp = queue;
        queue = help;
        help = temp;
        return ans;
    }

    public boolean isEmpty() {
        return queue.isEmpty();
    }
}
