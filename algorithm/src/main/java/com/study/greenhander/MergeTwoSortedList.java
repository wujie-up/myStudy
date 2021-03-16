package com.study.greenhander;

public class MergeTwoSortedList {
    static class Node {
        int value;
        Node next;

        public Node(int value) {
            this.value = value;
        }
    }

    public static void main(String[] args) {
        Node n1 = new Node(2);
        n1.next = new Node(3);
        n1.next.next = new Node(8);

        Node n2 = new Node(5);
        n2.next = new Node(6);
        n2.next.next = new Node(7);
        n2.next.next.next = new Node(9);
        Node n = merger2(n1, n2);
        while (n != null) {
            System.out.print(n.value + " ");
            n = n.next;
        }
    }

    private static Node merger2(Node n1, Node n2) {
        if (n1 == null) {
            return n2;
        }
        if (n2 == null) {
            return n1;
        }
        if (n1.value > n2.value) {
            n2.next = merger2(n1, n2.next);
            return n2;
        } else {
            n1.next = merger2(n1.next, n2);
            return n1;
        }
    }

    private static Node merger(Node n1, Node n2) {
        if (n1 == null) {
            return n2;
        }
        if (n2 == null) {
            return n1;
        }

        Node head = n1.value > n2.value ? n2 : n1;

        Node L = head.next;
        Node N = head == n1 ? n2 : n1;
        Node pre = head;

        while (L != null && N != null) {
            if (L.value > N.value) {
                pre.next = N;
                N = N.next;
            } else {
                pre.next = L;
                L = L.next;
            }
            pre = pre.next;
        }
        pre.next = L != null ? L : N; // L 和 N 有一个不为null时，要延续后续节点
        return head;
    }
}
