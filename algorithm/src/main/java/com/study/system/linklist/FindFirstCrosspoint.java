package com.study.system.linklist;

import java.util.*;

public class FindFirstCrosspoint {
    public static Random random = new Random();


    static class Node {
        int value;
        Node next;

        public Node(int value) {
            this.value = value;
        }
    }

    /**
     * 使用容器
     */
    public static Node getFirstCrossPoint1(Node h1, Node h2) {
        if (h1 == null || h2 == null) {
            return null;
        }

        // 判断是否有环
        Node c1 = getCycleNode1(h1);
        Node c2 = getCycleNode1(h2);

        // 1、其中一个有环，另外一个没有环，不可能相交
        if ((c1 == null && c2 != null) || (c1 != null && c2 == null)) {
            return null;
        }

        // 2、都没有环，将其中一个链表所有元素放入set中，再遍历第二个链表的所有元素，如果再集合中不存在，则没有相交
        if (c1 == null && c2 == null) {
            Set<Node> set = new HashSet<>();
            Node cur1 = h1;
            Node cur2 = h2;
            while (cur1 != null) {
                set.add(cur1);
                cur1 = cur1.next;
            }
            while (cur2 != null) {
                if (set.contains(cur2)) {
                    return cur2;
                }
                cur2 = cur2.next;
            }
        }
        // 都有环，随便返回一个入环节点
        if (c1 != null && c2 != null) {
            return c1;
        }

        return null;
    }

    /**
     * 使用容器
     */
    public static Node getCycleNode1(Node head) {
        if (null == head) {
            return null;
        }
        Set<Node> set = new HashSet<>();
        Node cur = head;
        while (cur != null) {
            if (set.contains(cur)) {
                return cur;
            }
            set.add(cur);
            cur = cur.next;
        }
        return null;
    }

    /**
     * 不使用容器
     */
    public static Node getFirstCrossPoint2(Node h1, Node h2) {
        if (h1 == null || h2 == null) {
            return null;
        }

        // 判断是否有环
        Node c1 = getCycleNode2(h1);
        Node c2 = getCycleNode2(h2);


        // 1、其中一个有环，另外一个没有环，不可能相交
        if ((c1 == null && c2 != null) || (c1 != null && c2 == null)) {
            return null;
        }
        // 2、都没有环，如果相交，最后一个节点肯定是相同的
        if (c1 == null && c2 == null) {
            // 计算链表的长度
            int n = 0;
            Node cur1 = h1;
            Node cur2 = h2;

            while (cur1.next != null) {
                cur1 = cur1.next;
                n++;
            }

            while (cur2.next != null) {
                cur2 = cur2.next;
                n--;
            }

            // 最后一个节点不相同，肯定不相交
            if (cur1 != cur2) {
                return null;
            }
            // 相交则求出相交点
            Node ln = n > 0 ? h1 : h2;
            Node sn = ln == h1 ? h2 : h1;
            // 让长链表先走到短链表 齐长 点
            n = Math.abs(n);
            while (n > 0) {
                ln = ln.next;
                n--;
            }
            // 然后长短链表一起遍历
            while (ln != sn) {
                ln = ln.next;
                sn = sn.next;
            }
            return ln;
        }

        // 3、都有环
        if (c1 != null && c2 != null) {
            // 入环点相同
            if (c1 == c2) {
                return c1;
            } else {
                // 入环点不相同，随便返回一个
                return c1;
            }
        }
        return null;
    }

    /**
     * 不使用容器
     */
    public static Node getCycleNode2(Node head) {
        if (null == head) {
            return null;
        }

        Node fast = head;
        Node slow = head;

        // 如果无环，肯定会遍历到null
        // 如果有环，那么slow 和 fast一定会相遇
        // slow 和fast相遇后，fast指向head，fast和slow每次走一步，它们将会在入环节点再次相遇
        boolean b = false;
        while (fast.next != null && fast.next.next != null) {
            fast = fast.next.next;
            slow = slow.next;
            if (fast == slow) {
                b = true;
                break;
            }
        }

        if (b) {
            fast = head;
            while (fast != slow) {
                fast = fast.next;
                slow = slow.next;
            }
            return slow;
        } else {
            return null;
        }
    }


    /*------------------------------------------- 测试代码 --------------------------------------------------*/
    public static void main(String[] args) {
        int maxLen = 10;
        int maxValue = 100;
        int times = 3500000;
        for (int i = 0; i < times; i++) {
            List<Node> list = makeNodeList(maxLen, maxValue);
            Node a1 = getFirstCrossPoint1(list.get(0), list.get(1));
            Node a2 = getFirstCrossPoint2(list.get(0), list.get(1));
            if (a1 != a2) {
                System.out.println("oops!!");
                break;
            }
        }
        System.out.println("success");
    }

    private static List<Node> makeNodeList(int maxLen, int maxValue) {
        int len1 = (int) (Math.random() * maxLen) + 1;
        List<Node> list1 = new ArrayList();
        Node head1 = null;
        Node tail1 = null;
        for (int i = 0; i < len1; i++) {
            Node node = new Node((int) (Math.random() * maxValue));
            if (head1 == null) {
                head1 = tail1 = node;
            } else {
                tail1.next = node;
                tail1 = tail1.next;
            }
            list1.add(node);
        }

        int len2 = (int) (Math.random() * maxLen) + 1;
        List<Node> list2 = new ArrayList();
        Node head2 = null;
        Node tail2 = null;
        for (int i = 0; i < len2; i++) {
            Node node = new Node((int) (Math.random() * maxValue));
            if (head2 == null) {
                head2 = tail2 = node;
            } else {
                tail2.next = node;
                tail2 = tail2.next;
            }
            list2.add(node);
        }

        double r = Math.random();

        // 记录h1 和 h2 的环
        Node c1 = null;
        Node c2 = null;
        if (r < 0.25) {
            // 25%的几率，让链表1 和链表2 都有环
            Node lastN1 = list1.get(list1.size() - 1);
            c1 = list1.get(random.nextInt(list1.size()));
            lastN1.next = c1;

            Node lastN2 = list2.get(list2.size() - 1);
            c2 = list2.get(random.nextInt(list2.size()));
            lastN2.next = c2;
        } else if (r < 0.5) {
            // 25%的几率，让链表1 无环 和链表2 有环
            Node lastN2 = list2.get(list2.size() - 1);
            c2 = list2.get(random.nextInt(list2.size()));
            lastN2.next = c2;
        } else if (r < 0.75) {
            // 25%的几率，让链表1 有环 和链表2 无环
            Node lastN1 = list1.get(list1.size() - 1);
            c1 = list1.get(random.nextInt(list1.size()));
            lastN1.next = c1;
        } else {
            // 25%的几率，让链表1  和链表2 都无环
        }
        double r1 = Math.random();
        // 30%的几率，让链表1 和链表2 随机相交
        if (r1 < 0.3) {
            // 从链表1中随机取出一个节点
            Node n1 = list1.get(random.nextInt(list1.size()));
            // 替换链表2中随机一个位置的点，让其next指向链表1点
            Node n2 = list2.get(random.nextInt(list2.size()));
            n2.next = n1;
        } else if (r1 < 0.6) {
            // 30%的几率，让链表1 和链表2 环上相交
            // 50几率让入环点相同
            if (Math.random() < 0.5) {
                Node n2 = list2.get(random.nextInt(list2.size()));
                n2.next = c1;
            } else {
                Node n2 = list2.get(random.nextInt(list2.size()));
                if (c1 != null) {
                    n2.next = c1.next;
                }
            }
        } else {
            // 40%的几率，不相交
        }
        List<Node> ans = new ArrayList<>();
        ans.add(head1);
        ans.add(head2);
        return ans;
    }
}
