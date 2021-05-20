package com.study.system.tree.binarytree;

import java.util.ArrayList;
import java.util.List;

/**
 * @description: N叉树转二叉树
 * @author: wj2wml@qq.com
 * @date: 2021-05-02 22:13
 **/
public class NTree2BTree {
    static class NtNode {
        int value;
        List<NtNode> sons;

        public NtNode(int value) {
            this.value = value;
        }
    }

    static class BtNode {
        int value;
        BtNode left;
        BtNode right;

        public BtNode(int value) {
            this.value = value;
        }
    }

    public static BtNode n2b(NtNode head) {
        if (head == null) {
            return null;
        }
        BtNode b = new BtNode(head.value);
        b.left = processSons(head.sons);
        return b;
    }

    private static BtNode processSons(List<NtNode> list) {
        BtNode head = null;
        BtNode cur = null;

        if (list == null) {
            return head;
        }

        for (NtNode n : list) {
            BtNode b = new BtNode(n.value);
            if (null == head) {
                head = b;
            } else {
                cur.right = b;
            }
            cur = b;
            cur.left = processSons(n.sons);
        }
        return head;
    }

    public static NtNode b2n(BtNode b) {
        if (null == b) {
            return null;
        }
        NtNode n = new NtNode(b.value);
        n.sons = processSons(b.left);
        return n;
    }

    private static List<NtNode> processSons(BtNode b) {
        List<NtNode> list = new ArrayList<>();
        while (b != null) {
            NtNode n = new NtNode(b.value);
            n.sons = processSons(b.left);
            list.add(n);
            b = b.right;
        }
        return list.size() > 0 ? list : null;
    }

    /*-------------------------------------------- 测试代码 ---------------------------------------------------*/
    public static void main(String[] args) {
        int maxLevel = 6;
        int maxValue = 100;
        int maxSonNum = 5;
        int times = 800000;
        for (int i = 0; i < times; i++) {
            NtNode n = generateNTree(maxLevel, maxSonNum, maxValue);
            BtNode b = n2b(n);
            NtNode rebuildN = b2n(b);
            if (!sameN(n, rebuildN)) {
                System.out.println("oops!!!");
                break;
            }
        }
        System.out.println("success!!!");
    }

    private static boolean sameN(NtNode n, NtNode rN) {
        if (n == null && rN == null) {
            return true;
        }

        if (n == null && rN != null) {
            return false;
        }

        if (n != null && rN == null) {
            return false;
        }

        if (n.value != rN.value) {
            return false;
        }

        if (n.sons == null && rN.sons == null) {
            return true;
        }

        if (n.sons == null && rN.sons != null) {
            return false;
        }

        if (n.sons != null && rN.sons == null) {
            return false;
        }

        if (n.sons.size() != rN.sons.size()) {
            return false;
        }

        for (int i = 0; i < n.sons.size(); i++) {
            if (!sameN(n.sons.get(i), rN.sons.get(i))) {
                return false;
            }
        }
        return true;
    }

    private static NtNode generateNTree(int maxLevel, int maxSonNum, int maxValue) {
        int level = (int) (Math.random() * maxLevel);
        if (level == 0) {
            return null;
        }
        return generate(1, level, maxSonNum, maxValue);
    }

    private static NtNode generate(int curLevel, int maxLevel, int maxSonNum, int maxValue) {
        if (curLevel > maxLevel || Math.random() < 0.5) {
            return null;
        }
        NtNode cur = new NtNode((int) (Math.random() * maxValue));
        List<NtNode> sons = null;
        int sonNum = (int) (Math.random() * maxSonNum);
        if (sonNum > 0) {
            sons = new ArrayList<>();
        }
        for (int i = 0; i < sonNum; i++) {
            NtNode son = generate(curLevel + 1, maxLevel, maxSonNum, maxValue);
            if (null != son) {
                sons.add(son);
            }
        }
        cur.sons = null != sons && sons.size() > 0 ? sons : null;
        return cur;
    }
}
