package com.study.greenhander;

import org.omg.CORBA.TIMEOUT;

public class ReverseListInKGroup {

    static class Node {
        int value;
        Node next;

        public Node(int value) {
            this.value = value;
        }
    }

    public static Node reverseKGroup(Node head, int k) {
        Node start = head;
        Node end = getReverseEnd(head, k);

        if (end == null) { // 不够k个，不需要反转
            return head;
        }
        head = end;
        reverse(start, end);
        Node lastEnd = start; // 前一组的最后一个节点
        while (lastEnd.next != null) {
            start = lastEnd.next;
            end = getReverseEnd(start, k);
            if (end == null) {
                break;
            } else {
                reverse(start, end);
                lastEnd.next = end;
                lastEnd = start;
            }
        }
        return head;
    }

    private static void reverse(Node start, Node end) {
        Node next;
        Node pre = null;
        Node cur = start;
        end = end.next;
        while (cur != end) {
            next = cur.next;
            cur.next = pre;
            pre = cur;
            cur = next;
        }
        start.next = end;
    }

    private static Node getReverseEnd(Node head, int k) {
        Node end = head;
        while (--k != 0 && end != null) {
            end = end.next;
        }
        return end;
    }

    public static void check() {
        int maxValue = 50000;
        int maxLen = 15;
        int times = 500000;
        for (int i = 0; i < times; i++) {
            int k = (int) (Math.random() * 5);
            Node source = randomList(maxLen, maxValue);
            Node reverseNode = reverseKGroup(source, k);
            System.out.println();
        }

    }

    private static Node randomList(int maxLen, int maxValue) {
        int len = (int) (Math.random() * maxLen) + 1;
        Node head = new Node((int) (Math.random() * (maxValue + 1)));
        Node next = head;
        while (--len != 0) {
            next = next.next = new Node((int) (Math.random() * (maxValue + 1)));
        }
        return head;
    }

    public static void main(String[] args) {
        check();
    }
}
