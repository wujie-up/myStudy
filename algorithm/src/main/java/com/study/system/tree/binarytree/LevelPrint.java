package com.study.system.tree.binarytree;

import java.util.LinkedList;

/**
 * @description: 层序遍历
 * @author: wj2wml@qq.com
 * @date: 2021-05-02 17:39
 **/
public class LevelPrint {
    static class Node {
        int value;
        Node left;
        Node right;

        public Node(int value) {
            this.value = value;
        }
    }
    public static void process(Node head) {
        if (null == head) {
            return;
        }
        LinkedList<Node> queue = new LinkedList<>();
        queue.add(head);

        while (!queue.isEmpty()) {
            Node cur = queue.poll();
            System.out.print(cur.value + " ");
            if (null != cur.left) {
                queue.add(cur.left);
            }
            if (null != cur.right) {
                queue.add(cur.right);
            }
        }
    }

    public static void main(String[] args) {
        Node head = new Node(1);
        head.left = new Node(2);
        head.right = new Node(3);
        head.left.left = new Node(4);
        head.left.right = new Node(5);
        head.right.left = new Node(6);
        head.right.right = new Node(7);

        process(head);
        System.out.println("");
    }
}
