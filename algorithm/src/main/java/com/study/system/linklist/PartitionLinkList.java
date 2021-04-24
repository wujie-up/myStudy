package com.study.system.linklist;

import java.util.ArrayList;
import java.util.List;

/**
 * 链表按值分为：小的在左边  相等在中间 大的在右边
 */
public class PartitionLinkList {
    static class Node {
        int value;
        Node next;

        public Node(int value) {
            this.value = value;
        }
    }

    /**
     * 使用容器解决：使用数组来存储链表，然后应用荷兰国旗的解决方法（快排思想）
     */
    public static Node partition1(Node head, int num) {
        // 少于2个直接返回
        if (null == head || head.next == null) {
            return head;
        }
        List<Node> arr = new ArrayList<>();
        Node cur = head;
        while (cur != null) {
            arr.add(cur);
            cur = cur.next;
        }

        // 数组分区
        // 3 1] 8 5 2 5 {9 6     5
        int right = arr.size(); // 表示大于区域 {
        int left = -1; // 表示小于区域 ]

        int i = 0;
        while (i < right) {
            if (arr.get(i).value < num) {
                swap(arr, i++, ++left);
            } else if (arr.get(i).value > num) {
                swap(arr, i, --right); // 保持i不变
            } else {
                i++;
            }
        }

        head = arr.get(0);
        Node tail = head;
        for (int j = 1; j < arr.size(); j++) {
            tail.next = arr.get(j);
        }
        // 防止循环链表，末尾的节点后面next可能不为空
        tail.next = null;
        return head;
    }

    private static void swap(List<Node> arr, int i, int j) {
        if (i == j) {
            return;
        }
        Node nodeI = arr.get(i);
        Node nodeJ = arr.get(j);
        arr.set(i, nodeJ);
        arr.set(j, nodeI);
    }

    /**
     * 不使用容器，将链表分成3段，然后在连接
     */
    public static Node partition2(Node head, int num) {
        // 少于2个直接返回
        if (null == head || head.next == null) {
            return head;
        }

        Node bg = null;
        Node eq = null;
        Node sm = null;
        Node bgTail = null;
        Node eqTail = null;
        Node smTail = null;

        Node cur = head;
        while (cur != null) {
            if (cur.value < num) {
                if (sm == null) {
                    sm = smTail = cur;
                } else {
                    smTail.next = cur;
                    smTail = smTail.next;
                }
            } else if (cur.value == num) {
                if (eq == null) {
                    eq = eqTail = cur;
                } else {
                    eqTail.next = cur;
                    eqTail = eqTail.next;
                }
            } else {
                if (bg == null) {
                    bg = bgTail = cur;
                } else {
                    bgTail.next = cur;
                    bgTail = bgTail.next;
                }
            }
            cur = cur.next;
        }

        // small区不为空
        if (null != sm) {
            Node next = null == eq ? bg : eq;
            smTail.next = next;
        }

        // equal区不为空
        if (null != eq) {
            eqTail.next = bg;
        }
        // 返回头判断
        head = null == sm ? (eq == null ? bg : eq) : sm;
        return head;
    }

    /*-------------------------------------  测试代码  -------------------------------------------------*/
    public static void main(String[] args) {
        int maxLen = 20;
        int maxValue = 150;
        int times = 666666;
        for (int i = 0; i < times; i++) {
            int num = (int) (Math.random() * maxValue);
            Node h1 = makeNode(maxLen, maxValue);
            Node h2 = copy(h1);
            if (!test(partition1(h1, num), num) && !test(partition2(h2, num), num)) {
                System.out.println("oops!!!");
                break;
            }
        }
        System.out.println("success!!!");
    }

    private static boolean test(Node head, int num) {
        Node cur = head;
        boolean small = true;
        boolean equal = true;

        while (cur != null) {
            if (cur.value < num) {
                if (!small) {
                    // 被改了 证明 小于 穿插在 大于等于之间
                    return false;
                }
            } else if (cur.value == num) {
                if (small) {
                    small = false;
                }
                if (!equal) {
                    return false;
                }
            } else {
                if (equal) {
                    equal = false;
                }
            }
            cur = cur.next;
        }
        return true;
    }

    private static Node copy(Node head) {
        Node root = null;
        Node tail = null;
        Node cur = head;
        while (null != cur) {
            if (null == root) {
                root = tail = new Node(cur.value);
            } else {
                tail = tail.next = new Node(cur.value);
            }
            cur = cur.next;
        }
        return root;
    }

    private static Node makeNode(int maxLen, int maxValue) {
        Node head = null;
        Node tail = null;
        int len = (int) (Math.random() * maxValue);
        for (int i = 0; i < len; i++) {
            if (null == head) {
                head = tail = new Node((int) (Math.random() * maxValue));
            } else {
                tail.next = new Node((int) (Math.random() * maxValue));
                tail = tail.next;
            }
        }
        return head;
    }
}

