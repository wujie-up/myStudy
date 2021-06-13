package com.study.system.recursion;

import java.util.Stack;

/**
 * @description: 逆序一个栈
 * @author: wj2wml@qq.com
 * @date: 2021-06-13 21:23
 **/
public class ReverseStack {

    public static void reverse(Stack<Integer> stack) {
        if (stack.isEmpty()) {
            return;
        }
        int i = getBottom(stack);
        reverse(stack);
        stack.push(i);
    }

    /**
     * 获取栈底元素
     */
    public static Integer getBottom(Stack<Integer> stack) {
        if (stack.size() == 1) {
            return stack.pop();
        }
        Integer cur = stack.pop();
        Integer last = getBottom(stack);
        stack.push(cur);
        return last;
    }

    public static void main(String[] args) {
        Stack<Integer> stack = new Stack<>();
        stack.push(3);
        stack.push(2);
        stack.push(1);
        reverse(stack);
        while (!stack.isEmpty()) {
            System.out.println(stack.pop());
        }
    }
}
