package com.study.system.tree.binarytree;

/**
 * 二叉树遍历-递归方式
 */
public class RecursiveTraversalTree {
    static class Node {
        int value;
        Node left;
        Node right;

        public Node(int value) {
            this.value = value;
        }
    }

    /**
     * 前序遍历 ：头左右
     */
    public static void front(Node root) {
        if (null == root) {
            return;
        }
        System.out.print(root.value +" ");
        front(root.left);
        front(root.right);
    }

    /**
     * 中序遍历 ：左头右
     */
    public static void middle(Node root) {
        if (null == root) {
            return;
        }
        middle(root.left);
        System.out.print(root.value +" ");
        middle(root.right);
    }

    /**
     * 后序遍历 ：左右头
     */
    public static void after(Node root) {
        if (null == root) {
            return;
        }
        after(root.left);
        after(root.right);
        System.out.print(root.value +" ");
    }

    public static void main(String[] args) {
        Node head = new Node(1);
        head.left = new Node(2);
        head.right = new Node(3);
        head.left.left = new Node(4);
        head.left.right = new Node(5);
        head.right.left = new Node(6);
        head.right.right = new Node(7);

        front(head);
        System.out.println();
        middle(head);
        System.out.println();
        after(head);
        System.out.println();
    }
}
