package com.study.system.linklist;

import java.util.*;

/**
 * 赋值随机链表
 */
public class CopyRandomLinkList {
    static class Node {
        int value;
        Node next;
        Node rand;

        Node(int val) {
            this.value = val;
        }
    }

    /**
     * 使用容器
     */
    public static Node copy1(Node head) {
        if (null == head) {
            return null;
        }

        // map<old, new>
        Map<Node, Node> map = new HashMap<>();
        Node cur = head;
        while (null != cur) {
            map.put(cur, new Node(cur.value));
            cur = cur.next;
        }

        cur = head;
        while (null != cur) {
            Node newNode = map.get(cur);
            Node newNext = map.get(cur.next);
            Node newRand = map.get(cur.rand);
            newNode.next = newNext;
            newNode.rand = newRand;
            cur = cur.next;
        }
        return map.get(head);
    }

    /**
     * 不使用容器
     * 1 -> 2 -> 3
     * 1 -> a -> 2 -> b -> 3 -> c
     */
    public static Node copy2(Node head) {
        if (null == head) {
            return null;
        }

        // 1 -> 2 -> 3
        // 1 -> a -> 2 -> b -> 3 -> c
        Node cur = head;
        while (null != cur) {
            Node next = cur.next;
            cur.next = new Node(cur.value);
            cur = next;
        }

        cur = head;
        while (null != cur) {
            cur.next.rand = cur.rand == null ? null : cur.rand.next;
            cur = cur.next.next;
        }

        Node newHead = head.next;
        head.next = null;
        Node newTail = newHead;

        while (newTail.next != null) {
            newTail.next = newTail.next.next;
            newTail = newTail.next;
        }

        return newHead;
    }

    /*------------------------------------------ 测试代码 ------------------------------------------------*/
    public static void main(String[] args) {
        int maxLen = 20;
        int maxValue = 500;
        int times = 666666;
        for (int i = 0; i < times; i++) {
            Node head = makeNode(maxLen, maxValue);
            Node h1 = copy1(head);
            Node h2 = copy2(head);
            if (!test(head, h1)) {
                System.out.println("oops1!!!");
                break;
            }
            if (!test(head, h2)) {
                System.out.println("oops2!!!");
                break;
            }
        }
        System.out.println("success!!!");
    }

    private static boolean test(Node head, Node cp) {
        if (head == null && null == cp) {
            return true;
        }

        Node c1 = head;
        Node c2 = cp;

        while (c1 != null) {
            if (c1.value != c2.value) {
                return false;
            }

            if (c1.rand == null && c2.rand != null) {
                return false;
            }

            if (c1.rand != null && c2.rand == null) {
                return false;
            }

            if (c1.rand != null && c2.rand != null && c1.rand.value != c2.rand.value) {
                return false;
            }
            c1 = c1.next;
            c2 = c2.next;
        }
        return true;
    }

    private static Node makeNode(int maxLen, int maxValue) {
        Node head = null;
        Node tail = null;
        int len = (int) (Math.random() * maxLen);
        List<Node> list = new ArrayList<>(len);
        for (int i = 0; i < len; i++) {
            Node node = new Node((int) (Math.random() * maxValue));
            if (head == null) {
                head = tail = node;
            } else {
                tail.next = node;
                tail = tail.next;
            }
            list.add(node);
        }

        if (len < 1) {
            return head;
        }

        // 随机节点生成
        Node cur = head;
        Random r = new Random();
        while (null != cur) {
            // 20% 几率为空
            if (Math.random() < 0.2) {
                cur.rand = null;
            } else {
                int i = r.nextInt(len);
                cur.rand = list.get(i);
            }
            cur = cur.next;
        }
        return head;
    }
}
