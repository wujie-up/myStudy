package com.study.system.tree.recursion;

import java.util.*;

/**
 * @description: 最低公共祖先
 * @author: wj2wml@qq.com
 * @date: 2021-05-25 21:16
 **/
public class LowestCommonParent {
    static class Node {
        int value;
        Node left;
        Node right;

        public Node(int value) {
            this.value = value;
        }
    }

    static class Info {
        boolean hasA;
        boolean hasB;
        Node lowestParent;

        public Info(boolean hasA, boolean hasB, Node lowestParent) {
            this.hasA = hasA;
            this.hasB = hasB;
            this.lowestParent = lowestParent;
        }
    }

    public static Node getLowestParent(Node head, Node a, Node b) {
        if (null == head || null == a || null == b) {
            return null;
        }
        return process(head, a, b).lowestParent;
    }

    private static Info process(Node cur, Node a, Node b) {
        if (null == cur) {
            return new Info(false, false, null);
        }
        Info left = process(cur.left, a, b);
        Info right = process(cur.right, a, b);

        boolean hasA = false;
        boolean hasB = false;
        Node lowestParent = null;
        if (left.hasA || right.hasA || cur == a) {
            hasA = true;
        }
        if (left.hasB || right.hasB || cur == b) {
            hasB = true;
        }
        if (left.lowestParent != null) {
            lowestParent = left.lowestParent;
        }else if (right.lowestParent != null) {
            lowestParent = right.lowestParent;
        } else if (hasA && hasB) {
            lowestParent = cur;
        }
        return new Info(hasA, hasB, lowestParent);
    }


    public static Node compare(Node head, Node a, Node b) {
        if (null == head || null == a || null == b) {
            return null;
        }
        Map<Node,Node> parentMap = getParentMap(head);
        Set<Node> aParents = new HashSet<>();
        aParents.add(a);
        Node cur = a;
        while (parentMap.get(cur) != null) {
            Node parent = parentMap.get(cur);
            aParents.add(parent);
            cur = parent;
        }
        cur = b;
        while (!aParents.contains(cur)) {
            cur = parentMap.get(cur);
        }
        return cur;
    }

    private static Map<Node, Node> getParentMap(Node cur) {
        Map<Node,Node> map = new HashMap<>();
        map.put(cur, null);
        putMap(cur, map);
        return map;
    }

    private static void putMap(Node cur, Map<Node, Node> map) {
        if (null != cur.left) {
            map.put(cur.left, cur);
            putMap(cur.left, map);
        }
        if (null != cur.right) {
            map.put(cur.right, cur);
            putMap(cur.right, map);
        }
    }

    public static void main(String[] args) {
        int maxLevel = 15;
        int maxValue = 5000;
        int times = 888888;
        for (int i = 0; i < times; i++) {
            Node tree = makeTree(maxLevel, maxValue);
            Node[] nodes = getRandomNodes(tree);
            Node p1 = getLowestParent(tree, nodes[0], nodes[1]);
            Node p2 = compare(tree, nodes[0], nodes[1]);
            if (p1 != p2) {
                System.out.println("完犊子了！");
                break;
            }
        }
        System.out.println("success!!!");
    }

    private static Node[] getRandomNodes(Node tree) {
        Node[] nodes = new Node[2];
        if (null == tree) {
            return nodes;
        }
        List<Node> list = new ArrayList<>();
        getNodeList(tree, list);
        if (list.size() < 2) {
            nodes[0] = nodes[1] = list.get(0);
        } else {
            Random r = new Random();
            nodes[0] = list.get(r.nextInt(list.size()));
            nodes[1] = list.get(r.nextInt(list.size()));
        }
        return nodes;
    }

    private static void getNodeList(Node cur, List<Node> list) {
        if (null == cur) {
            return;
        }
        list.add(cur);
        getNodeList(cur.left, list);
        getNodeList(cur.right, list);
    }

    private static Node makeTree(int maxLevel, int maxValue) {
        maxLevel = (int) (Math.random() *maxLevel);
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
