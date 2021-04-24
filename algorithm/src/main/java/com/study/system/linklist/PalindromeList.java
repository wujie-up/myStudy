package com.study.system.linklist;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

/**
 * 判断链表是否是回文结构
 */
public class PalindromeList {
    static class Node {
        int value;
        Node next;

        public Node(int value) {
            this.value = value;
        }
    }

    // 空间复杂度：O(N)
    public static boolean check1(Node head) {
        if (head == null || head.next == null) {
            return true;
        }

        Stack<Node> stack = new Stack<>();
        Node cur = head;
        while (cur != null) {
            stack.push(cur);
            cur = cur.next;
        }

        cur = head;
        while (cur != null) {
            if (stack.pop().value != cur.value) {
                return false;
            }
            cur = cur.next;
        }

        return true;
    }

    // 空间复杂度：O(N/2)
    public static boolean check2(Node head) {
        if (head == null || head.next == null) {
            return true;
        }

        // 求出奇数重点 和 偶数的下中点
        // 1 2 3 4 5 6
        // 1 2 2 3 3 4
        Node fast = head;
        Node slow = head.next;

        while (fast.next != null && fast.next.next != null) {
            fast = fast.next.next;
            slow = slow.next;
        }

        Stack<Node> stack = new Stack<>();
        // 将中点和后面的节点全部放入栈中
        while (slow != null) {
            stack.push(slow);
            slow = slow.next;
        }

        Node cur = head;
        while (!stack.isEmpty()) {
            if (stack.pop().value != cur.value) {
                return false;
            }
            cur = cur.next;
        }
        return true;
    }

    // 空间复杂度：O(1) 将链表的左或右反转
    public static boolean check3(Node head) {
        if (head == null || head.next == null) {
            return true;
        }
        // 找到中点  偶数上中点
        // 1 2 3 4 5 6
        Node fast = head;
        Node slow = head;
        while (fast.next != null && fast.next.next != null) {
            fast = fast.next.next;
            slow = slow.next;
        }
        // 18 16 42 25 30 39
        Node rightH = slow.next; // 中点右边第一个点
        slow.next = null;
        // 反转右边的链表
        Node pre = null;
        Node next;
        while (rightH.next != null) {
            next = rightH.next; // 先保存下个节点指针
            rightH.next = pre; // next指向上一个节点
            pre = rightH;
            rightH = next;
        }
        rightH.next = pre;

        // 比较左右链表
        Node left = head;
        Node right = rightH;
        while (left != null && right != null) {
            if (left.value != right.value) {
                return false;
            }
            left = left.next;
            right = right.next;
        }
        // 还原链表
        pre = null;
        while (rightH.next != null) {
            next = rightH.next;
            rightH.next = pre;
            rightH = next;
        }
        rightH.next = pre;
        slow.next = rightH;

        return true;
    }

    /*-----------------------------------------------  测试代码  -----------------------------------------------------*/
    public static void main(String[] args) {
        int maxLen = 15;
        int maxValue = 500;
        int times = 588888;

        for (int i = 0; i < times; i++) {
            Node h1 = makeNode(maxLen, maxValue);
            Node h2 = copy(h1);
            Node h3 = copy(h1);

            boolean a = check1(h1);
            boolean b = check2(h2);
            boolean c = check3(h3);

            if (a != b || b != c) {
                System.out.println("oops!!!");
                break;
            }
        }
        System.out.println("success!!!");
    }

    private static Node copy(Node head) {
        Node h = null;
        Node hTail = null;
        Node cur = head;
        while (cur != null) {
            if (h == null) {
                h = hTail = new Node(cur.value);
            } else {
                hTail.next = new Node(cur.value);
                hTail = hTail.next;
            }
            cur = cur.next;
        }
        return h;
    }

    private static Node makeNode(int maxLen, int maxValue) {
        Node head = null;
        Node tail = null;
        int len = (int) (Math.random() * maxLen);

        if (len <= 1) {
            for (int i = 0; i < len ; i++) {
                head = new Node((int) (Math.random() * maxValue));
            }
            return head;
        }

        // 1 2 3 4 5 6
        // 0 1 2 3 4 5
        // 赋值到奇数个的中点，或者偶数个上中点
        List<Node> lefts = new ArrayList<>();
        for (int i = 0; i < len /2; i++) {
            Node node = new Node((int) (Math.random() * maxValue));
            if (null == head) {
                head = tail = node;
            } else {
                tail.next = node;
                tail = tail.next;
            }
            lefts.add(node);
        }

        // 50%几率生成回文链表
        boolean b = Math.random() < 0.5;
        int begin = len / 2;
        if (b) {
            // 如果是奇数个，需要移除中点
            if (len % 2 != 0) {
                lefts.remove(len / 2 - 1);
            }
            for (int i = lefts.size() - 1; i >= 0; i--) {
                tail.next = new Node(lefts.get(i).value);
                tail = tail.next;
            }
        } else {
            for (int i = begin; i < len; i++) {
                tail.next = new Node((int) (Math.random() * maxValue));
                tail = tail.next;
            }
        }

        return head;
    }
}
