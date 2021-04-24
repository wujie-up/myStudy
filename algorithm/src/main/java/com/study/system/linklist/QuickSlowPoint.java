package com.study.system.linklist;

import java.util.ArrayList;

/**
 * 快慢指针的应用
 */
public class QuickSlowPoint {
    static class Node<V> {
        V v;
        Node<V> next;

        public Node(V v) {
            this.v = v;
        }
    }

    // 奇数长度返回中点，偶数长度返回上中点
    public Node getMiddle1(Node head) {
        if (head == null || head.next == null || head.next.next == null) {
            return head;
        }
        Node quick = head.next.next;
        Node slow = head.next;

        while (quick.next != null && quick.next.next != null) {
            quick = quick.next.next;
            slow = slow.next;
        }
        return slow;
    }

    // 奇数长度返回中点，偶数长度返回下中点
    public Node getMiddle2(Node head) {
        if (head == null || head.next == null) {
            return head;
        }
        Node quick = head.next;
        Node slow = head.next;

        while (quick.next != null && quick.next.next != null) {
            quick = quick.next.next;
            slow = slow.next;
        }
        return slow;
    }

    // 奇数长度返回中点前一个，偶数长度返回上中点前一个
    public Node getMiddle3(Node head) {
        if (head == null || head.next == null || head.next.next == null) {
            return null;
        }
        Node quick = head.next.next;
        Node slow = head;

        while (quick.next != null && quick.next.next != null) {
            quick = quick.next.next;
            slow = slow.next;
        }
        return slow;
    }


    // 奇数长度返回中点前一个，偶数长度返回下中点前一个
    public Node getMiddle4(Node head) {
        if (head == null || head.next == null) {
            return null;
        }
        Node quick = head.next;
        Node slow = head;

        while (quick.next != null && quick.next.next != null) {
            quick = quick.next.next;
            slow = slow.next;
        }
        return slow;
    }

    /*-----------------------------------------  测试代码  -----------------------------------------------*/
    public static void main(String[] args) {
        int maxLen = 15;
        int times = 300000;
        for (int i = 0; i < times; i++) {
            int len = (int) (Math.random() * maxLen);
            Node<Integer> head = makeList(len);
            QuickSlowPoint qsp = new QuickSlowPoint();
            if (qsp.getMiddle1(head) != test1(head)) {
                System.out.println("oops1!!!");
                print(head);
                return;
            }
            if (qsp.getMiddle2(head) != test2(head)) {
                System.out.println("oops2!!!");
                print(head);
                return;
            }
            if (qsp.getMiddle3(head) != test3(head)) {
                System.out.println("oops3!!!");
                print(head);
                return;
            }
            if (qsp.getMiddle4(head) != test4(head)) {
                System.out.println("oops4!!!");
                print(head);
                return;
            }
        }

    }

    private static void print(Node head) {
        while (head != null) {
            System.out.print(head.v + " ");
            head = head.next;
        }
        System.out.println();
    }

    private static Node test4(Node<Integer> head) {
        // 奇数长度返回中点前一个，偶数长度返回下中点前一个
        if (head == null || head.next == null) {
            return null;
        }
        Node cur = head;
        ArrayList<Node> arr = new ArrayList<>();
        while (cur != null) {
            arr.add(cur);
            cur = cur.next;
        }
        return arr.get((arr.size() - 2) / 2);
    }

    private static Node test3(Node<Integer> head) {
        // 奇数长度返回中点前一个，偶数长度返回上中点前一个
        if (head == null || head.next == null || head.next.next == null) {
            return null;
        }
        Node cur = head;
        ArrayList<Node> arr = new ArrayList<>();
        while (cur != null) {
            arr.add(cur);
            cur = cur.next;
        }
        return arr.get((arr.size() - 3) / 2);
    }

    private static Node test2(Node<Integer> head) {
        // 奇数长度返回中点，偶数长度返回下中点
        if (null == head) {
            return head;
        }
        Node cur = head;
        ArrayList<Node> arr = new ArrayList<>();
        while (cur != null) {
            arr.add(cur);
            cur = cur.next;
        }
        return arr.get(arr.size() / 2);
    }

    private static Node test1(Node<Integer> head) {
        if (null == head) {
            return head;
        }
        // 奇数长度返回中点，偶数长度返回上中点
        Node cur = head;
        ArrayList<Node> arr = new ArrayList<>();
        while (cur != null) {
            arr.add(cur);
            cur = cur.next;
        }
        return arr.get((arr.size() - 1) / 2);
    }

    private static Node<Integer> makeList(int len) {
        Node<Integer> head = null;
        Node<Integer> cur = null;
        for (int i = 0; i < len; i++) {
            if (null == head) {
                head = new Node<>(i + 1);
                cur = head;
            } else {
                cur.next = new Node<>(i + 1);
                cur = cur.next;
            }
        }
        return head;
    }
}
