package com.study.system.tree.recursion;

import java.util.LinkedList;
import java.util.Queue;

/**
 * @description: 是否完全二叉树
 * @author: wj2wml@qq.com
 * @date: 2021-05-22 21:09
 **/
public class IsCompleteBTree {
    static class Node {
        int value;
        Node left;
        Node right;

        public Node(int value) {
            this.value = value;
        }
    }

    static class Info {
        boolean isC; // 是否完全
        boolean isF; // 是否满
        int height;

        public Info(boolean isC, boolean isF, int height) {
            this.isC = isC;
            this.isF = isF;
            this.height = height;
        }
    }

    public static boolean isBBT(Node head) {
        if (null == head) {
            return true;
        }
        return process1(head).isC;
    }

    private static Info process1(Node cur) {
        if (null == cur) {
            return new Info(true, true, 0);
        }
        Info left = process1(cur.left);
        Info right = process1(cur.right);

        boolean isF = left.isF && right.isF && left.height == right.height;
        boolean isC = false;
        int height = Math.max(left.height, right.height) + 1;

        // 1、左子树是满的，右子树是满的，并且0 <= 左子树高度 - 右子树的高度 < 2
        if (left.isF && right.isF && left.height == right.height) {
            isC = true;
        } else if (left.isF && right.isF && left.height == right.height + 1) {
            isC = true;
        } else if (left.isF && right.isC && left.height == right.height) {
            // 2、左子树是满的、右子树完全，并且 左子树高度 = 右子树高度
            isC = true;
        } else if (right.isF && left.isC && left.height == right.height + 1) {
            // 右子树是满的，左子树是完全，并且  左子树高度 - 右子树高度 = 1
            isC = true;
        }
        return new Info(isC, isF, height);
    }

    /**
     * 除了叶子节点外，只允许有一个非叶子节点的 子节点不满
     * <p>
     * 如果前面出现了不全的情况。则后面节点 应该满足都没有子节点
     * 并且左子树为空，右子树不为空肯定不满足
     */
    public static boolean compare(Node head) {
        if (null == head) {
            return true;
        }
        // 是否 遇到子节点不全的标记
        boolean nullNodeTag = false;
        boolean isC = true;
        Queue<Node> queue = new LinkedList<>();
        queue.add(head);

        while (!queue.isEmpty()) {
            Node cur = queue.poll();

            // 前面已经有节点的子节点 不全，如果当前节点  有子节点，则不完全
            if ((null != cur.left || null != cur.right) && nullNodeTag) {
                isC = false;
                break;
            }
            // 左子树为空，右子树不为空，肯定不满足
            if (null == cur.left && null != cur.right) {
                isC = false;
                break;
            }

            // 发现 有一个节点的子节点 不全，
            if (null == cur.left || null == cur.right) {
                nullNodeTag = true;
            }

            if (null != cur.left) {
                queue.add(cur.left);
            }

            if (null != cur.right) {
                queue.add(cur.right);
            }
        }

        return isC;
    }


    public static void main(String[] args) {
        int maxLevel = 10;
        int maxValue = 5000;
        int times = 888888;
        int count = 0;
        for (int i = 0; i < times; i++) {
            Node tree = makeTree(maxLevel, maxValue);
            boolean b1 = isBBT(tree);
            boolean b2 = compare(tree);
            if (b1 != b2) {
                isBBT(tree);
                System.out.println("完犊子了!");
                break;
            }
            if (b1 && b2) {
                count++;
            }
        }
        System.out.println("success!!! 完全二叉树个数: " + count);
    }

    private static Node makeTree(int maxLevel, int maxValue) {
        maxLevel = (int) (Math.random() * maxLevel);
        if (maxLevel == 0) {
            return null;
        }
        return generateNode(1, maxLevel, maxValue);
    }

    private static Node generateNode(int curLevel, int maxLevel, int maxValue) {
        if (curLevel > maxLevel || Math.random() < 0.5) {
            return null;
        }
        Node head = new Node((int) (Math.random() * maxValue));
        head.left = generateNode(curLevel + 1, maxLevel, maxValue);
        head.right = generateNode(curLevel + 1, maxLevel, maxValue);
        return head;
    }
}
