package com.study.system.tree.recursion;

/**
 * @description: 是否平衡二叉树
 * @author: wj2wml@qq.com
 * @date: 2021-05-21 21:24
 **/
public class IsBanlanceBTree {
    static class Node {
        int value;
        Node left;
        Node right;

        public Node(int value) {
            this.value = value;
        }
    }

    static class Info {
        boolean isBBT;
        int height;

        public Info(boolean isBBT, int height) {
            this.isBBT = isBBT;
            this.height = height;
        }
    }


    public static boolean isBBT(Node head) {
        if (null == head) {
            return true;
        }
        return process(head).isBBT;
    }

    private static Info process(Node cur) {
        if (null == cur) {
            return new Info(true, 0);
        }
        Info left = process(cur.left);
        Info right = process(cur.right);

        boolean isBBT = true;
        int height = Math.max(left.height, right.height) + 1;
        if (!left.isBBT || !right.isBBT) {
            isBBT = false;
        }
        if (Math.abs(left.height - right.height) > 1) {
            isBBT = false;
        }
        return new Info(isBBT, height);
    }


    public static boolean compare(Node head) {
        if (null == head) {
            return true;
        }
        boolean[] ans = new boolean[1];
        ans[0] = true;
        process2(head, ans);
        return ans[0];
    }

    private static int process2(Node cur, boolean[] ans) {
        if (!ans[0] || cur == null) {
            return 0;
        }
        int leftH = process2(cur.left, ans);
        int rightH = process2(cur.right, ans);

        if (Math.abs(leftH - rightH) > 1) {
            ans[0] = false;
        }
        return Math.max(leftH, rightH) + 1;
    }

    public static void main(String[] args) {
        int maxLevel = 15;
        int maxValue = 50000;
        int times = 888888;
        for (int i = 0; i < times; i++) {
            Node head = makeNode(maxLevel, maxValue);
            boolean b1 = isBBT(head);
            boolean b2 = compare(head);
            if (b1 != b2) {
                System.out.println("完犊子了！");
            }
        }
        System.out.println("success!!!");
    }

    private static Node makeNode(int maxLevel, int maxValue) {
        maxLevel = (int) (Math.random() * maxLevel);
        if (maxLevel == 0) {
            return null;
        }
        return generateNode(1, maxLevel, maxValue);
    }

    private static Node generateNode(int curLevel, int maxLevel, int maxValue) {
        if (curLevel > maxLevel || Math.random() > 0.5) {
            return null;
        }
        Node head = new Node((int) (Math.random() * maxValue));
        head.left = generateNode(curLevel + 1, maxLevel, maxValue);
        head.right = generateNode(curLevel + 1, maxLevel, maxValue);
        return head;
    }
}
