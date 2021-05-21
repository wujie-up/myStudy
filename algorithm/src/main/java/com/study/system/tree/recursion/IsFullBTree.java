package com.study.system.tree.recursion;

/**
 * @description: 是否满二叉树
 * @author: wj2wml@qq.com
 * @date: 2021-05-21 21:42
 **/
public class IsFullBTree {
    static class Node {
        int value;
        Node left;
        Node right;

        public Node(int value) {
            this.value = value;
        }
    }

    static class Info {
        int height;
        int size;

        public Info(int height, int size) {
            this.height = height;
            this.size = size;
        }
    }

    public static boolean isFull(Node head) {
        if (null == head) {
            return true;
        }
        Info info = process1(head);
        return (1 << info.height) - 1== info.size;
    }

    private static Info process1(Node cur) {
        if (null == cur) {
            return new Info(0, 0);
        }
        Info left = process1(cur.left);
        Info right = process1(cur.right);
        int height = Math.max(left.height, right.height) + 1;
        int size = left.size + right.size + 1;
        return new Info(height, size);
    }

    public static boolean compare(Node head) {
        if (head == null) {
            return true;
        }
        boolean[] ans = new boolean[1];
        ans[0] = true;
        int i = process2(head, ans);
        return ans[0];
    }

    private static int process2(Node cur, boolean[] ans) {
        if (!ans[0] || null == cur) {
            return 0;
        }
        int lSize = process2(cur.left, ans);
        int rSize = process2(cur.right, ans);
        if (lSize != rSize) {
            ans[0] = false;
        }
        return lSize + rSize + 1;
    }

    public static void main(String[] args) {
        int maxLevel = 15;
        int maxValue = 5000;
        int times = 888888;
        for (int i = 0; i < times; i++) {
            Node head = makeTree(maxLevel, maxValue);
            boolean b1 = isFull(head);
            boolean b2 = compare(head);
            if (b1 != b2) {
                System.out.println("完犊子了!");
            }
        }
        System.out.println("success!!!");
    }

    private static Node makeTree(int maxLevel, int maxValue) {
        maxLevel = (int) (Math.random() * maxLevel);
        if (maxLevel == 0) {
            return null;
        }
        return generate(1, maxLevel, maxValue);
    }

    private static Node generate(int curLevel, int maxLevel, int maxValue) {
        if (curLevel > maxLevel || Math.random() > 0.5) {
            return null;
        }
        Node head = new Node((int) (Math.random() * maxValue));
        head.left = generate(curLevel + 1, maxLevel, maxValue);
        head.right = generate(curLevel + 1, maxLevel, maxValue);
        return head;
    }
}
