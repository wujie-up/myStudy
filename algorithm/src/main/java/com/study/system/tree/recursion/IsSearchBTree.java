package com.study.system.tree.recursion;

import java.util.ArrayList;
import java.util.List;

/**
 * @description: 是否搜索二叉树
 * @author: wj2wml@qq.com
 * @date: 2021-05-20 21:21
 **/
public class IsSearchBTree {
    static class Node {
        int value;
        Node left;
        Node right;

        public Node(int value) {
            this.value = value;
        }
    }

    static class Info {
        boolean isSBT;
        int maxValue;
        int minValue;

        public Info(boolean isSBT, int maxValue, int minValue) {
            this.isSBT = isSBT;
            this.maxValue = maxValue;
            this.minValue = minValue;
        }
    }

    public static boolean isSBT(Node head) {
        if (null == head) {
            return true;
        }
        return process(head).isSBT;
    }

    private static Info process(Node cur) {
        // 递归的出口 base case
        if (cur == null) {
            // 不好构建返回信息，直接返回null，在上一层递归中进行处理
            return null;
        }
        Info left = process(cur.left);
        Info right = process(cur.right);
        int maxValue = cur.value;
        if (null != left) {
            maxValue = Math.max(maxValue, left.maxValue);
        }

        if (null != right) {
            maxValue = Math.max(maxValue, right.maxValue);
        }

        int minValue = cur.value;
        if (null != left) {
            minValue = Math.min(minValue, left.minValue);
        }

        if (null != right) {
            minValue = Math.min(minValue, right.minValue);
        }

        boolean isSBT = true;
        boolean leftIsSBT = null == left || left.isSBT;
        boolean rightIsSBT = null == right || right.isSBT;

        if (null != left && left.maxValue >= cur.value) {
            isSBT = false;
        }
        if (null != right && right.minValue <= cur.value) {
            isSBT = false;
        }

        if (!leftIsSBT || !rightIsSBT) {
            isSBT = false;
        }
        return new Info(isSBT, maxValue, minValue);
    }

    public static boolean compare(Node head) {
        if (null == head) {
            return true;
        }

        // 使用中序遍历，看结果是否是排好序的
        List<Node> list = new ArrayList<>();
        inTraversal(head, list);
        int max = Integer.MIN_VALUE;
        for (Node node : list) {
            if (max < node.value) {
                max = node.value;
            } else {
                return false;
            }
        }
        return true;
    }

    private static void inTraversal(Node cur, List<Node> list) {
        if (null == cur) {
            return;
        }
        inTraversal(cur.left, list);
        list.add(cur);
        inTraversal(cur.right, list);
    }


    public static void main(String[] args) {
        int maxLevel = 10;
        int maxValue = 5000;
        int times = 555555;
        for (int i = 0; i < times; i++) {
            Node head = makeNode(maxLevel, maxValue);
            boolean b1 = isSBT(head);
            boolean b2 = compare(head);
            if (b1 != b2) {
                System.out.println("完犊子了!!!");
                break;
            }
        }
        System.out.println("success!!!");
    }

    private static Node makeNode(int maxLevel, int maxValue) {
        int level = (int) (Math.random() * maxLevel);
        if (level == 0) {
            return null;
        }
        // 50% 几率生成非搜索树
        boolean d = Math.random() < 0.5;
        return generate(1, level, (int) (Math.random() * maxValue), d);
    }

    private static Node generate(int curLevel, int maxLevel, int value, boolean d) {
        if (curLevel > maxLevel) {
            return null;
        }
        Node head = new Node(value);
        int left = d ? value - (int) (Math.random() * value + 1) : value + (int) (Math.random() * value + 1);
        int right = d ? value + (int) (Math.random() * value + 1) : value - (int) (Math.random() * value + 1);
        head.left = generate(curLevel + 1, maxLevel, left, d);
        head.right = generate(curLevel + 1, maxLevel, right, d);
        return head;
    }
}
