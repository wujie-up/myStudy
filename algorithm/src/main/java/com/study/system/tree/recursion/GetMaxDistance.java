package com.study.system.tree.recursion;

import java.util.*;

/**
 * @description: 求最大距离
 * @author: wj2wml@qq.com
 * @date: 2021-05-21 23:02
 **/
public class GetMaxDistance {
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
        int maxDis;

        public Info(int height, int maxDis) {
            this.height = height;
            this.maxDis = maxDis;
        }
    }

    public static int getMaxDis(Node head) {
        if (null == head) {
            return 0;
        }
        return process1(head).maxDis;
    }

    private static Info process1(Node cur) {
        if (null == cur) {
            return new Info(0, 0);
        }
        Info left = process1(cur.left);
        Info right = process1(cur.right);

        int height = Math.max(left.height, right.height) + 1;
        int maxDis;

        // 最长距离经过X
        int p1 = left.height + right.height + 1;
        // 最长距离不经过X
        int p2 = left.maxDis;
        int p3 = right.maxDis;

        maxDis = Math.max(p1, Math.max(p2, p3));
        return new Info(height, maxDis);
    }

    /**
     * 穷举 所有节点之间的距离
     *
     * @param head
     * @return
     */
    public static int compare(Node head) {
        if (null == head) {
            return 0;
        }
        // 拿到所有节点
        List<Node> list = getNodeList(head);
        // 拿到所有节点的父亲节点映射表
        Map<Node, Node> parentMap = getParentMap(head);
        int max = 0;
        for (int i = 0; i < list.size(); i++) {
            for (int j = i; j < list.size(); j++) {
                max = Math.max(max, getDis(list.get(i), list.get(j), parentMap));
            }
        }
        return max;
    }

    private static Map<Node, Node> getParentMap(Node head) {
        Map<Node, Node> map = new HashMap<>();
        map.put(head, null);
        parentMap(head, map);
        return map;
    }

    private static void parentMap(Node cur, Map<Node, Node> map) {
        if (cur.left != null) {
            map.put(cur.left, cur);
            parentMap(cur.left, map);
        }
        if (cur.right != null) {
            map.put(cur.right, cur);
            parentMap(cur.right, map);
        }
    }

    private static List<Node> getNodeList(Node head) {
        List<Node> list = new ArrayList<>();
        preAdd(head, list);
        return list;
    }

    private static void preAdd(Node head, List<Node> list) {
        if (null == head) {
            return;
        }
        list.add(head);
        preAdd(head.left, list);
        preAdd(head.right, list);
    }

    private static int getDis(Node n1, Node n2, Map<Node, Node> parentMap) {
        // 先找到 n1 和 n2 的最底层的公共祖先
        Node commonParent = findLowestParent(n1, n2, parentMap);
        // 求出 n1、n2 到 parent的距离
        int l1 = getDistance(n1, commonParent, parentMap);
        int l2 = getDistance(n2, commonParent, parentMap);
        return l1 + l2 - 1;
    }

    private static int getDistance(Node node, Node commonParent, Map<Node, Node> parentMap) {
        Node cur = node;
        int l = 1;
        while (commonParent != cur) {
            l++;
            cur = parentMap.get(cur);
        }
        return l;
    }

    private static Node findLowestParent(Node n1, Node n2, Map<Node, Node> parentMap) {
        // 拿到n1 和他 所有的祖先节点
        Set<Node> pSet = new HashSet<>();
        Node cur = n1;
        pSet.add(cur);
        while (parentMap.get(cur) != null) {
            cur = parentMap.get(cur);
            pSet.add(cur);
        }
        // 找到 n1 n2 的第一个公共祖先节点
        cur = n2;
        while (!pSet.contains(cur)) {
            cur = parentMap.get(cur);
        }
        return cur;
    }


    public static void main(String[] args) {
        int maxLevel = 10;
        int maxValue = 50000;
        int times = 888888;
        for (int i = 0; i < times; i++) {
            Node tree = makeTree(maxLevel, maxValue);
            int m1 = getMaxDis(tree);
            int m2 = compare(tree);
            if (m1 != m2) {
                System.out.println("完犊子了!");
                break;
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
        if (curLevel > maxLevel || Math.random() < 0.5) {
            return null;
        }
        Node head = new Node((int) (Math.random() * maxValue));
        head.left = generate(curLevel + 1, maxLevel, maxLevel);
        head.right = generate(curLevel + 1, maxLevel, maxLevel);
        return head;
    }
}
