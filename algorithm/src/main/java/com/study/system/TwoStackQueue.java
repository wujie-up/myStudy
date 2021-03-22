package com.study.system;

import java.util.Stack;

public class TwoStackQueue<E> {
    Stack<E> pushStack = new Stack<>();
    Stack<E> popStack = new Stack<>();

    public void add(E e) {
        pushStack.push(e);
        transfor();
    }

    public E peek() {
        if (pushStack.isEmpty() && popStack.isEmpty()) {
            throw new RuntimeException("queue is empty");
        }
        transfor();
        E e = popStack.peek();
        return e;
    }

    public E poll() {
        if (pushStack.isEmpty() && popStack.isEmpty()) {
            throw new RuntimeException("queue is empty");
        }
        transfor();
        E e = popStack.pop();
        return e;
    }

    private void transfor() {
        if (popStack.isEmpty()) {
            while (!pushStack.isEmpty()) {
                E e = pushStack.pop();
                popStack.push(e);
            }
        }
    }

    public boolean isEmpty() {
        return pushStack.isEmpty() && popStack.isEmpty();
    }
}
