package com.study.system.tree.recursion;

import java.util.ArrayList;
import java.util.List;

/**
 * @description: 最大搜索二叉子树
 * @author: wj2wml@qq.com
 * @date: 2021-05-21 22:17
 **/
public class GetMaxBBTSize {
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
        int maxSize;
        int min;
        int max;

        public Info(int size, int maxSize, int min, int max) {
            this.size = size;
            this.maxSize = maxSize;
            this.min = min;
            this.max = max;
        }
    }

    public static int getMaxBBTSize(Node head) {
        if (null == head) {
            return 0;
        }
        return process1(head).maxSize;
    }

    private static Info process1(Node cur) {
        if (null == cur) {
            return null;
        }
        Info left = process1(cur.left);
        Info right = process1(cur.right);

        int min = cur.value;
        int max = cur.value;

        if (null != left) {
            max = Math.max(max, left.max);
        }

        if (null != right) {
            max = Math.max(max, right.max);
        }

        if (null != left) {
            min = Math.min(min, left.min);
        }

        if (null != right) {
            min = Math.min(min, right.min);
        }


        boolean isBBT = true;
        if (null != left && (left.max >= cur.value || left.maxSize != left.size)) {
            isBBT = false;
        }

        if (null != right && (right.min <= cur.value || right.maxSize != right.size)) {
            isBBT = false;
        }
        int lSize = null != left ? left.size : 0;
        int rSize = null != right ? right.size : 0;
        int size = lSize + rSize + 1;

        int lMaxSize = null != left ? left.maxSize : 0;
        int rMaxSize = null != right ? right.maxSize : 0;
        int maxSize = isBBT ? size : Math.max(lMaxSize, rMaxSize);
        return new Info(size, maxSize, min, max);
    }

    public static int compare(Node head) {
        if (null == head) {
            return 0;
        }
        int size = process2(head);
        if (size != 0) {
            return size;
        }
        return Math.max(compare(head.left), compare(head.right));
    }

    private static int process2(Node head) {
        List<Node> list = new ArrayList<>();
        // 使用中序遍历，添加到集合，如果是搜索，则list里面应该是从小到大排列
        in(head, list);
        for (int i = 1; i < list.size(); i++) {
            // 不满足排序情况 则不是搜索子树
            if (list.get(i).value <= list.get(i - 1).value) {
                return 0;
            }
        }
        return list.size();
    }

    private static void in(Node cur, List<Node> list) {
        if (cur == null) {
            return;
        }
        in(cur.left, list);
        list.add(cur);
        in(cur.right, list);
    }

    public static void main(String[] args) {
        int maxLevel = 5;
        int maxValue = 5000;
        int times = 8888888;
        int count = 0;
        for (int i = 0; i < times; i++) {
            Node tree = makeTree(maxLevel, maxValue);
            int m1 = getMaxBBTSize(tree);
            int m2 = compare(tree);
            if (m1 != m2) {
                System.out.println("完犊子了！");
            }
            if (m1 > 0) {
                count++;
            }
        }
        System.out.println("success!!!, 其中" + count + "个的搜索子树个数 > 0");
    }

    private static Node makeTree(int maxLevel, int maxValue) {
        maxLevel = (int) (Math.random() * maxLevel);
        if (maxLevel == 0) {
            return null;
        }
        int value = (int) (Math.random() * maxValue) + 1000;
        return generateNode(1, maxLevel, value);
    }

    private static Node generateNode(int curLevel, int maxLevel, int value) {
        if (curLevel > maxLevel || Math.random() < 0.5) {
            return null;
        }
        Node head = new Node(value);
        // 50% 几率会导致左子树值比当前值大 , 右子树 比当前 小
        boolean b = Math.random() < 1;
        int left = value - (int)(Math.random() * value) / 10;
        if (b) {
            left = value + (int)(Math.random() * value) / 10;
        }

        int right = value + (int)(Math.random() * value) / 10;
        if (b) {
            right = value - (int)(Math.random() * value) / 10;
        }
        head.left = generateNode(curLevel + 1, maxLevel, left);
        head.right = generateNode(curLevel + 1, maxLevel, right);
        return head;
    }
}
