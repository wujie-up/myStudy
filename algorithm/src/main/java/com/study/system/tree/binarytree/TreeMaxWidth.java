package com.study.system.tree.binarytree;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;

/**
 * @description: 树的最大宽度
 * @author: wj2wml@qq.com
 * @date: 2021-05-05 21:21
 **/
public class TreeMaxWidth {
    static class Node {
        int value;
        Node left;
        Node right;

        public Node(int value) {
            this.value = value;
        }
    }

    /**
     * 使用容器
     */
    public static int getMaxWidth(Node head) {
        if (null == head) {
            return 0;
        }
        Queue<Node> queue = new LinkedList<>();
        Map<Node, Integer> levelMap = new HashMap<>();
        queue.add(head);
        levelMap.put(head, 1);
        int curLevel = 1; // 当前层数
        int curLevelCount = 0;// 当前层数宽度统计
        int max = 0;
        while (!queue.isEmpty()) {
            Node cur = queue.poll();
            // 得到当前节点的层数
            int curNodeLevel = levelMap.get(cur);
            if (cur.left != null) {
                queue.add(cur.left);
                levelMap.put(cur.left, curNodeLevel + 1);
            }
            if (cur.right != null) {
                queue.add(cur.right);
                levelMap.put(cur.right, curNodeLevel + 1);
            }
            if (curLevel == curNodeLevel) {
                curLevelCount++;
            } else {
                // 表示进入了下一层了
                max = Math.max(max, curLevelCount);
                curLevelCount = 1;
                curLevel++;
            }
        }
        // 最后一层无法再while中进行结算
        max = Math.max(max, curLevelCount);
        return max;
    }

    /**
     * 不使用容器
     */
    public static int getMaxWidth2(Node head) {
        if (null == head) {
            return 0;
        }

        Queue<Node> queue = new LinkedList<>();
        queue.add(head);

        Node curEnd = head;
        Node nextEnd = null;
        int max = 0;
        int curLevelCount = 0;
        while (!queue.isEmpty()) {
            Node cur = queue.poll();
            if (cur.left != null) {
                queue.add(cur.left);
                nextEnd = cur.left;
            }
            if (cur.right != null) {
                queue.add(cur.right);
                nextEnd = cur.right;
            }
            curLevelCount++;
            if (cur == curEnd) {
                max = Math.max(max, curLevelCount);
                curLevelCount = 0;
                curEnd = nextEnd;
            }
        }
        return max;
    }


    /*--------------------------------------------------- 测试代码 ---------------------------------------------------*/
    public static void main(String[] args) {
        int maxLevel = 8;
        int maxValue = 100;
        int times = 888888;
        for (int i = 0; i < times; i++) {
            Node head = generateBTree(maxLevel, maxValue);
            int w1 = getMaxWidth(head);
            int w2 = getMaxWidth2(head);
            if (w1 != w2) {
                System.out.println("oops!!!");
                break;
            }
        }
        System.out.println("success!!!");
    }

    private static Node generateBTree(int maxLevel, int maxValue) {
        int level = (int) (Math.random() * maxLevel);
        if (level == 0) {
            return null;
        }
        return generate(1, level, maxValue);
    }

    private static Node generate(int curLevel, int level, int maxValue) {
        Node head = new Node((int) (Math.random() * maxValue));
        if (curLevel == level) {
            return head;
        }
        head.left = Math.random() > 0.5 ? generate(curLevel + 1, level, maxValue) : null;
        head.right = Math.random() > 0.5 ? generate(curLevel + 1, level, maxValue) : null;
        return head;
    }
}
