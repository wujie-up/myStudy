package com.study.system.tree.binarytree;

import java.util.*;

/**
 * @description: 序列化和反序列化
 * @author: wj2wml@qq.com
 * @date: 2021-05-02 17:53
 **/
public class SerializeAndRebuild {
    static class Node<V> {
        V v;
        Node<V> left;
        Node<V> right;

        public Node(V v) {
            this.v = v;
        }
    }

    /**
     * @description: 前序序列化
     * @param: [head]
     * @return: java.util.List<java.lang.Object>
     * @author: wj2wml@qq.com
     * @date: 2021/5/2
     **/
    public static Queue<Object> preEncode(Node head) {
        Queue<Object> ans = new LinkedList<>();
        if (null != head) {
            pre(head, ans);
        }
        return ans;
    }

    private static void pre(Node cur, Queue<Object> ans) {
        if (null == cur) {
            ans.add(null);
        } else {
            ans.add(cur.v);
            pre(cur.left, ans);
            pre(cur.right, ans);
        }
    }

    /**
     * @description: 前序反序列化
     * @param: [list]
     * @return: com.study.system.tree.binarytree.SerializeAndRebuild.Node
     * @author: wj2wml@qq.com
     * @date: 2021/5/2
     **/
    public static Node preDecode(Queue<Object> list) {
        if (null == list || list.size() < 1) {
            return null;
        }
        return preD(list);
    }

    private static Node preD(Queue<Object> list) {
        Object value = list.poll();
        if (null == value) {
            return null;
        }
        Node head = new Node(value);
        head.left = preD(list);
        head.right = preD(list);
        return head;
    }

    /**
     * @description: 后序序列化
     * @param: [head]
     * @return: java.util.List<java.lang.Object>
     * @author: wj2wml@qq.com
     * @date: 2021/5/2
     **/
    public static Queue<Object> postEncode(Node head) {
        Queue<Object> ans = new LinkedList<>();
        if (null != head) {
            post(head, ans);
        }
        return ans;
    }

    private static void post(Node cur, Queue<Object> ans) {
        if (null == cur) {
            ans.add(null);
        } else {
            post(cur.left, ans);
            post(cur.right, ans);
            ans.add(cur.v);
        }
    }

    /**
     * @description: 后序反序列化 左右头
     * @param: [list]
     * @return: com.study.system.tree.binarytree.SerializeAndRebuild.Node
     * @author: wj2wml@qq.com
     * @date: 2021/5/2
     **/
    public static Node postDecode(Queue<Object> list) {
        if (null == list || list.size() < 1) {
            return null;
        }
        // 将左右头 -> 头右左
        Stack<Object> stack = new Stack<>();
        while (!list.isEmpty()) {
            stack.push(list.poll());
        }
        return postD(stack);
    }

    private static Node postD(Stack<Object> stack) {
        Object value = stack.pop();
        if (null == value) {
            return null;
        }
        Node head = new Node(value);
        head.right = postD(stack);
        head.left = postD(stack);
        return head;
    }

    /**
     * @description: 层序序列化
     * @param: [head]
     * @return: java.util.List<java.lang.Object>
     * @author: wj2wml@qq.com
     * @date: 2021/5/2
     **/
    public static Queue<Object> levelEncode(Node head) {
        Queue<Object> ans = new LinkedList<>();
        if (null == head) {
            return ans;
        }
        levelE(head, ans);
        return ans;
    }

    private static void levelE(Node head, Queue<Object> ans) {
        LinkedList<Node> queue = new LinkedList<>();
        queue.add(head);
        ans.add(head.v);
        while (!queue.isEmpty()) {
            Node cur = queue.poll();
            if (null == cur.left) {
                ans.add(null);
            } else {
                ans.add(cur.left.v);
                queue.add(cur.left);
            }
            if (null == cur.right) {
                ans.add(null);
            } else {
                ans.add(cur.right.v);
                queue.add(cur.right);
            }
        }
    }


    /**
     * @description: 层序反序列化
     * @param: [list]
     * @return: com.study.system.tree.binarytree.SerializeAndRebuild.Node
     * @author: wj2wml@qq.com
     * @date: 2021/5/2
     **/
    public static Node levelDecode(Queue<Object> list) {
        if (null == list || list.size() < 1) {
            return null;
        }
        return levelD(list);
    }

    private static Node levelD(Queue<Object> list) {
        Object value = list.poll();
        if (value == null) {
            return null;
        }
        Queue<Node> queue = new LinkedList<>();
        Node head = new Node(value);
        Node cur = null;
        queue.add(head);
        while (!queue.isEmpty()) {
            cur = queue.poll();
            Object leftV = list.poll();
            Object leftR = list.poll();
            if (null == leftV) {
                cur.left = null;
            } else {
                cur.left = new Node(leftV);
                queue.add(cur.left);
            }
            if (null == leftR) {
                cur.right = null;
            } else {
                cur.right = new Node(leftR);
                queue.add(cur.right);
            }
        }
        return head;
    }

    /*------------------------------------------- 测试代码 ---------------------------------------------------*/
    public static void main(String[] args) {
        int maxLevel = 5;
        int maxValue = 50;
        int times = 100000;
        for (int i = 0; i < times; i++) {
            Node head = randomBST(maxLevel, maxValue);
            Queue<Object> preList = preEncode(head);
            Queue<Object> postList = postEncode(head);
            Queue<Object> levelList = levelEncode(head);
            Node preNode = preDecode(preList);
            Node postNode = postDecode(postList);
            Node levelNode = levelDecode(levelList);

            if (!sameBST(preNode, postNode) || !sameBST(postNode, levelNode)) {
                System.out.println("oops!!!");
                break;
            }
        }
        System.out.println("success!!!");
    }

    private static boolean sameBST(Node n1, Node n2) {
        if (n1 == null && n2 == null) {
            return true;
        }

        if (n1 != null && n2 == null) {
            return false;
        }

        if (n1 == null && n2 != null) {
            return false;
        }

        if (n1.v != n2.v) {
            return false;
        }
        return sameBST(n1.left, n2.left) && sameBST(n1.right, n2.right);
    }

    private static Node randomBST(int maxLevel, int maxValue) {
        int level = (int) (Math.random() * maxLevel);
        if (level == 0) {
            return null;
        }
        return generate(1, maxLevel, maxValue);
    }

    private static Node generate(int curLevel, int maxLevel, int maxValue) {
        if (curLevel > maxLevel || Math.random() < 0.5) {
            return null;
        }
        Node cur = new Node((int) (Math.random() * maxValue));
        cur.left = generate(curLevel + 1, maxLevel, maxValue);
        cur.right = generate(curLevel + 1, maxLevel, maxValue);
        return cur;
    }
}
