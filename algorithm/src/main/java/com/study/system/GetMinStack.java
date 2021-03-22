package com.study.system;

import java.util.Stack;

public class GetMinStack {
    public static class MinStack {
        Stack<Integer> dataStack;
        Stack<Integer> minStack;

        public MinStack() {
            this.dataStack = new Stack<>();
            this.minStack = new Stack<>();
        }

        public void push(int data) {
            dataStack.push(data);
            if (minStack.isEmpty()) {
                minStack.push(data);
            } else {
                // 这里可以优化，如果data > topNum 可以不存，pop的时候则需要做出相应修改
                Integer topNum = minStack.peek();
                int add = data < topNum ? data : topNum;
                minStack.push(add);
            }
        }

        public int pop() {
            if (dataStack.isEmpty()) {
                throw new RuntimeException();
            }

            minStack.pop();
            return dataStack.pop();
        }

        public int peek() {
            if (dataStack.isEmpty()) {
                throw new RuntimeException();
            }
            return dataStack.peek();
        }

        public int getMin() {
            if (dataStack.isEmpty()) {
                throw new RuntimeException();
            }
            return minStack.peek();
        }
    }
}
