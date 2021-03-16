package com.study.greenhander;

import java.util.ArrayList;
import java.util.List;

public class LinkedReverse {
    static class Node {
        Node next;
        int value;

        public Node(int value) {
            this.value = value;
        }
    }

    static class DoubleNode {
        DoubleNode last;
        DoubleNode next;
        int value;

        public DoubleNode(int value) {
            this.value = value;
        }
    }

    /**
     * N(a) -> N(b) -> N(c) -> N(d) -> null
     * null <- N(a) <- N(b) <- N(c) <- N(d)
     */
    public static Node reverseSingleList(Node cur) {
        Node pre = null; // 用来记录 前一个节点的地址
        Node next;// 用来记录 下一个节点地址
        while (cur != null) {
            // 保存下一个节点地址
            next = cur.next;
            // 修改当前节点的下一个节点地址
            cur.next = pre;
            // 移动指针
            pre = cur;
            cur = next;
        }
        return pre;
    }

    /**
     * N(a) -> N(b) -> N(c) -> N(d) -> null // next
     * null <--    <--     <--     <--      // last
     * <p>
     * null <- N(a) <- N(b) <- N(c) <- N(d) // next
     * -->     -->     -->      // last
     */
    public static DoubleNode reverseDoubleList(DoubleNode cur) {
        DoubleNode pre = null; // 用来记录 前一个节点的地址
        DoubleNode next;// 用来记录 下一个节点地址
        while (cur != null) {
            // 保存下一个节点地址
            next = cur.next;
            // 修改当前节点的下一个节点和上一个节点地址
            cur.next = pre;
            cur.last = next;
            // 移动指针
            pre = cur;
            cur = next;
        }
        return pre;
    }


    public static void main(String[] args) {
        int len = 20;
        int value = 1000;
        int times = 60000;

        for (int i = 0; i < times; i++) {
//            Node single = generateRandomSingleList(len, value);
//            List singleList = getSingleList(single);
//            Node reverseSingle = reverseSingleList(single);
//            if (!checkSingle(singleList, single, reverseSingle)) {
//                System.out.println("单链表反转失败~！");
//            }
            DoubleNode doubleNode = generateRandomDoubleList(len, value);
            List doubleList = getDoubleList(doubleNode);
            DoubleNode reverseDouble = reverseDoubleList(doubleNode);
            if (!checkDouble(doubleList, doubleNode, reverseDouble)) {
                System.out.println("双链表反转失败~！");
            }
        }
    }

    private static List getDoubleList(DoubleNode source) {
        List nextList = new ArrayList();
        List lastList = new ArrayList();

        DoubleNode last = null;
        while (source != null) {
            nextList.add(source.value);
            last = source;
            source = source.next;
        }
        DoubleNode pre = last;
        while (pre != null) {
            lastList.add(pre.value);
            pre = pre.last;
        }

        List all = new ArrayList();
        all.add(nextList);
        all.add(lastList);
        return all;
    }

    private static List getSingleList(Node source) {
        List list = new ArrayList();
        while (source != null) {
            list.add(source.value);
            source = source.next;
        }
        return list;
    }

    private static boolean checkDouble(List all, DoubleNode source, DoubleNode reverse) {
        if (source == null) {
            return reverse == null;
        }

        List nextList = (ArrayList) all.get(0);
        List lastList = (ArrayList) all.get(1);

        DoubleNode copyReverse = reverse;
        for (int i = nextList.size() - 1; i >= 0; i--) {
            if (!nextList.get(i).equals(reverse.value)) {
                return false;
            }
            reverse = reverse.next;
        }

        for (int i = 0; i < lastList.size(); i++) {
            if (!lastList.get(i).equals(copyReverse.value)) {
                return false;
            }
            copyReverse = copyReverse.next;
        }
        return true;
    }

    private static boolean checkSingle(List list, Node source, Node reverse) {
        if (source == null) {
            return reverse == null;
        }

        for (int i = list.size() - 1; i >= 0; i--) {
            if (!list.get(i).equals(reverse.value)) return false;
            reverse = reverse.next;
        }
        return true;
    }


    public static Node generateRandomSingleList(int len, int value) {
        int size = (int) (Math.random() * (len + 1));
        if (size == 0) return null;
        Node head = new Node((int) (Math.random() * (value + 1)));
        Node pre = head;
        size--;
        while (size != 0) {
            Node cur = new Node((int) (Math.random() * (value + 1)));
            pre.next = cur;
            pre = cur;
            size--;
        }
        return head;
    }

    public static DoubleNode generateRandomDoubleList(int len, int value) {
        int size = (int) (Math.random() * (len + 1));
        if (size == 0) return null;
        DoubleNode head = new DoubleNode((int) (Math.random() * (value + 1)));
        DoubleNode pre = head;
        DoubleNode last = null;
        size--;
        while (size != 0) {
            DoubleNode cur = new DoubleNode((int) (Math.random() * (value + 1)));
            pre.next = cur;
            cur.last = pre;
            pre = cur;
            size--;
        }
        return head;
    }
}
