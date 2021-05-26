package com.study.system.tree.recursion;

import java.util.ArrayList;
import java.util.List;

/**
 * @description: 最大二叉搜索树头节点
 * @author: wj2wml@qq.com
 * @date: 2021-05-24 21:35
 **/
public class GetMaxBSTHead {
    static class Node {
        int value;
        Node left;
        Node right;

        public Node(int value) {
            this.value = value;
        }
    }

    static class Info {
        int size;
        int maxBSTSize;
        Node bstHead;
        int maxValue;
        int minValue;

        public Info(int size, int maxBSTSize, Node bstHead, int maxValue, int minValue) {
            this.size = size;
            this.maxBSTSize = maxBSTSize;
            this.bstHead = bstHead;
            this.maxValue = maxValue;
            this.minValue = minValue;
        }
    }

    public static Node getMaxBSTHead(Node head) {
        if (null == head) {
            return null;
        }
        return process(head).bstHead;
    }

    private static Info process(Node cur) {
        if (null == cur) {
            return null;
        }
        Info left = process(cur.left);
        Info right = process(cur.right);

        int maxValue = cur.value;
        if (null != left) {
            maxValue = Math.max(left.maxValue, maxValue);
        }
        if (null != right) {
            maxValue = Math.max(right.maxValue, maxValue);
        }
        int minValue = cur.value;
        if (null != left) {
            minValue = Math.min(left.minValue, minValue);
        }
        if (null != right) {
            minValue = Math.min(right.minValue, minValue);
        }

        int lSize = left != null ? left.size : 0;
        int rSize = right != null ? right.size : 0;
        int size = lSize + rSize + 1;

        int lMaxSize = left != null ? left.maxBSTSize : 0;
        int rMaxSize = right != null ? right.maxBSTSize : 0;

        boolean isLBST = left == null || left.maxBSTSize == left.size;
        boolean isRBST = right == null || right.maxBSTSize == right.size;
        // 当前节点为搜索二叉树
        int p1 = -1;
        boolean isLMin = left == null || left.maxValue < cur.value;
        boolean isRMax = right == null || right.minValue > cur.value;
        if (isLBST && isRBST && isLMin && isRMax) {
            p1 = size;
        }
        int p2 = lMaxSize;
        int p3 = rMaxSize;

        int maxBSTSize = Math.max(p1, Math.max(p2, p3));

        Node bstHead;
        // 这里优先判断左边，所以当左右一致时，优先会选择左边，比较函数需要同样的选择逻辑
        if (maxBSTSize == p1) {
            bstHead = cur;
        } else if (maxBSTSize == p2) {
            bstHead = left != null ? left.bstHead : null;
        } else {
            bstHead = right != null ? right.bstHead : null;
        }

        return new Info(size, maxBSTSize, bstHead, maxValue, minValue);
    }

    public static Node compare(Node head) {
        if (null == head) {
            return null;
        }
        if (getBSTSize(head) != 0) {
            return head;
        }
        Node left = compare(head.left);
        Node right = compare(head.right);
        // 必须大于等于，因为上面优先判断 左边
        return getBSTSize(left) >= getBSTSize(right) ? left : right;
    }

    private static int getBSTSize(Node head) {
        // 中序遍历
        List<Node> arr = new ArrayList<>();
        inAdd(head, arr);
        for (int i = 1; i < arr.size(); i++) {
            if (arr.get(i).value < arr.get(i - 1).value) {
                return 0;
            }
        }
        return arr.size();
    }

    private static void inAdd(Node cur, List<Node> arr) {
        if (null == cur) {
            return;
        }
        inAdd(cur.left, arr);
        arr.add(cur);
        inAdd(cur.right, arr);
    }

    public static void main(String[] args) {
        int maxLevel = 15;
        int maxValue = 5000;
        int times = 888888;
        int count = 0;
        for (int i = 0; i < times; i++) {
            Node tree = makeTree(maxLevel, maxValue);
            Node n1 = getMaxBSTHead(tree);
            Node n2 = compare(tree);
            if (n1 != n2) {
                System.out.println("完犊子了!");
                break;
            }
            if (n1 != null) {
                count++;
            }
        }
        System.out.println("success!!!, 二叉搜索树存在的情况有：" + count);
    }

    private static Node makeTree(int maxLevel, int maxValue) {
        maxLevel = (int) (Math.random() * maxLevel);
        if (maxLevel == 0) {
            return null;
        }
        int value = (int) Math.random() * maxValue + 1000;
        return generateNode(1, maxLevel, maxValue);
    }

    private static Node generateNode(int curLevel, int maxLevel, int value) {
        if (curLevel > maxLevel || Math.random() < 0.5) {
            return null;
        }
        Node head = new Node(value);
        boolean b = Math.random() < 0.5;

        int left = value - value / 10;
        int right = value + value / 10;
        if (b) {
            left = value + value / 10;
            right = value - value / 10;
        }
        head.left = generateNode(curLevel + 1, maxLevel, left);
        head.right = generateNode(curLevel + 1, maxLevel, right);
        return head;
    }
}
