package com.study.system.tree.binarytree;

/**
 * @description: 打印B树
 * @author: wj2wml@qq.com
 * @date: 2021-05-05 20:49
 **/
public class PrintBtree {
    static class Node {
        int value;
        Node left;
        Node right;

        public Node(int value) {
            this.value = value;
        }
    }

    public static void print(Node head) {
        printInOrder(head, 0, "H", 17);
    }

    private static void printInOrder(Node head, int height, String to, int len) {
        if (head == null) {
            return;
        }
        printInOrder(head.right, height + 1, "v", len);
        String val = to + head.value + to;
        int lenM = val.length();
        int lenL = (len - lenM) / 2;
        int lenR = len - lenM - lenL;
        val = getSpace(lenL) + val + getSpace(lenR);
        System.out.println(getSpace(height * len) + val);
        printInOrder(head.left, height + 1 ,"^", len);
    }

    private static String getSpace(int num) {
        StringBuilder sb = new StringBuilder();
        String space = " ";
        for (int i = 0; i < num; i++) {
            sb.append(space);
        }
        return sb.toString();
    }

    public static void main(String[] args) {
        Node head = new Node(1);
        head.left = new Node(2);
        head.right = new Node(3);
        head.left.left = new Node(4);
        head.left.right = new Node(5);
        head.right.left = new Node(6);
        head.left.left.right = new Node(7);
        print(head);
    }
}
