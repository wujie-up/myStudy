package com.study.system.tree.binarytree;

import java.util.Queue;
import java.util.Stack;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class UnRecursiveTraversalTree {
    static class Node {
        int value;
        Node left;
        Node right;

        public Node(int value) {
            this.value = value;
        }
    }

    /**
     * 前序 ：头左右
     */
    public static void pre(Node root) {
        if (null != root) {
            Stack<Node> stack = new Stack<>();
            stack.push(root);
            while (!stack.isEmpty()) {
                Node node = stack.pop();
                System.out.print(node.value + " ");
                if (node.right != null) {
                    stack.add(node.right);
                }
                if (node.left != null) {
                    stack.add(node.left);
                }
            }
        }
        System.out.println();
    }

    /**
     * 中序 ：左头右
     */
    public static void in(Node cur) {
        if (cur != null) {
            Stack<Node> stack = new Stack<>();
            while (!stack.isEmpty() || cur != null) {
                if (cur != null) {
                    stack.push(cur);
                    cur = cur.left;
                } else {
                    Node node = stack.pop();
                    System.out.print(node.value + " ");
                    cur = node.right;
                }
            }
        }
        System.out.println();
    }

    /**
     * 后序 ：左右头
     */
    public static void post1(Node head) {
        if (null != head) {
            Stack<Node> s1 = new Stack<>();
            Stack<Node> s2 = new Stack<>();
            s1.push(head);

            while (!s1.isEmpty()) {
                head = s1.pop(); // 依次弹出 头 右 左
                s2.push(head); // 依次加入 头 右 左，弹出则是 左 右 头
                if (null != head.left) {
                    s1.push(head.left);
                }
                if (null != head.right) {
                    s1.push(head.right);
                }
            }

            while (!s2.isEmpty()) {
                System.out.print(s2.pop().value +" ");
            }
        }
        System.out.println();
    }

    public static void post2(Node h) {
        if (h != null) {
            Stack<Node> stack = new Stack<Node>();
            stack.push(h);
            Node c = null;
            while (!stack.isEmpty()) {
                c = stack.peek();
                if (c.left != null && h != c.left && h != c.right) {
                    stack.push(c.left);
                } else if (c.right != null && h != c.right) {
                    stack.push(c.right);
                } else {
                    System.out.print(stack.pop().value + " ");
                    h = c;
                }
            }
        }
        System.out.println();
    }

    public static void main(String[] args) {
        Node head = new Node(1);
        head.left = new Node(2);
        head.right = new Node(3);
        head.left.left = new Node(4);
        head.left.right = new Node(5);
        head.right.left = new Node(6);
        head.right.right = new Node(7);

        pre(head);
        System.out.println("========");
        in(head);
        System.out.println("========");
        post1(head);
        System.out.println("========");
        post2(head);
    }
}
