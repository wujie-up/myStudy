package com.study.greenhander;

public class AddTowSingleLinkList {
    static class Node {
        int value;
        Node next;

        public Node(int value) {
            this.value = value;
        }
    }

    public static void main(String[] args) {
        Node n1 = new Node(2);
        n1.next = new Node(8);
        n1.next.next = new Node(3);

        Node n2 = new Node(7);
        n2.next = new Node(5);
        n2.next.next = new Node(6);
        n2.next.next.next = new Node(1);

        Node add = addLinkList(n1, n2);
        while (add != null) {
            System.out.print(add.value + " ");
            add = add.next;
        }
        System.out.println();
    }

    private static Node addLinkList(Node n1, Node n2) {
        int len1 = getLength(n1);
        int len2 = getLength(n2);
        Node L = len1 > len2 ? n1 : n2;
        Node S = L == n1 ? n2 : n1;
        Node curL = L;
        Node lastL = L;

        int carry = 0;
        // L S 都存在
        while (S != null) {
            int num = curL.value + S.value + carry;
            carry = num / 10;
            curL.value = num % 10;
            S = S.next;
            lastL = curL;
            curL = curL.next;
        }
        // S 加完了，剩下L
        while (curL != null) {
            int sum = curL.value + carry;
            carry = sum / 10;
            curL.value = sum % 10;
            lastL = curL;
            curL = curL.next;
        }
        // 剩下进位数
        if (carry > 0) {
            lastL.next = new Node(carry);
        }
        return L;
    }

    private static int getLength(Node head) {
        int len = 0;
        while (head != null) {
            head = head.next;
            len++;
        }
        return len;
    }
}
