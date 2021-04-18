package com.study.system.heep;

import java.util.HashMap;
import java.util.Map;

public class TireTree {

    private Node root;

    public TireTree() {
        this.root = new Node();
    }

    public void insert(String str) {
        if (null == str) {
            return;
        }
        char[] chars = str.toCharArray();
        Node cur = root;
        cur.pass++;
        for (char c : chars) {
            int i = c - 'a'; // 以0->a为数组起点, 算出相对的数组下标
            if (null == cur.next[i]) {
                cur.next[i] = new Node();
                cur.next[i].pass++;
            } else {
                cur.next[i].pass++;
            }
            cur = cur.next[i];
        }
        // 最后位置为end位置
        cur.end++;
    }

    /**
     * 查询某个字符串在结构中有几个
     */
    public int search(String str) {
        if (null == str) {
            return 0;
        }
        char[] chars = str.toCharArray();
        Node cur = root;
        for (char c : chars) {
            int i = c - 'a'; // 以0->a为数组起点, 算出相对的数组下标
            if (null == cur.next[i]) {
                return 0;
            }
            cur = cur.next[i];
        }
        return cur.end;
    }

    public void delete(String str) {
        if (search(str) > 0) {
            char[] chars = str.toCharArray();
            Node cur = root;
            cur.pass--;
            for (char c : chars) {
                int i = c - 'a';
                if (--cur.next[i].pass == 0) {
                    cur.next[i] = null;
                    return;
                }
                cur = cur.next[i];
            }
            cur.end--;
        }
    }

    /**
     * 查询有多少个字符串，是以str做前缀的
     */
    public int prefixNumber(String str) {
        if (null == str) {
            return 0;
        }
        char[] chars = str.toCharArray();
        Node cur = root;
        for (char c : chars) {
            int i = c - 'a'; // 以0->a为数组起点, 算出相对的数组下标
            if (null == cur.next[i]) {
                return 0;
            }
            cur = cur.next[i];
        }
        return cur.pass;
    }

    private class Node {
        private int pass; // 表示途径当前节点的字符串个数
        private int end; // 表示当前节点作为结尾的字符串个数
        private Node[] next;

        public Node() {
            this.pass = 0;
            this.end = 0;
            this.next = new Node[26]; // 26个字母，都是小写的
        }
    }

    static class TireTree2 {
        private Node2 root;

        public TireTree2() {
            this.root = new Node2();
        }

        private class Node2 {
            private int pass; // 表示途径当前节点的字符串个数
            private int end; // 表示当前节点作为结尾的字符串个数
            private Map<Integer, Node2> path;

            public Node2() {
                this.pass = 0;
                this.end = 0;
                this.path = new HashMap<>();
            }
        }

        public void insert(String str) {
            if (null == str) {
                return;
            }
            char[] chars = str.toCharArray();
            Node2 cur = root;
            cur.pass++;
            for (char c : chars) {
                int i = c - 'a'; // 以0->a为数组起点, 算出相对的数组下标
                if (!cur.path.containsKey(i)) {
                    cur.path.put(i, new Node2());
                    cur.path.get(i).pass++;
                } else {
                    cur.path.get(i).pass++;
                }
                cur = cur.path.get(i);
            }
            // 最后位置为end位置
            cur.end++;
        }

        public int search(String str) {
            if (null == str) {
                return 0;
            }
            char[] chars = str.toCharArray();
            Node2 cur = root;
            for (char c : chars) {
                int i = c - 'a'; // 以0->a为数组起点, 算出相对的数组下标
                if (!cur.path.containsKey(i)) {
                    return 0;
                }
                cur = cur.path.get(i);
            }
            return cur.end;
        }

        public void delete(String str) {
            if (search(str) > 0) {
                char[] chars = str.toCharArray();
                Node2 cur = root;
                cur.pass--;
                for (char c : chars) {
                    int i = c - 'a';
                    if (--cur.path.get(i).pass == 0) {
                        cur.path.remove(i);
                        return;
                    }
                    cur = cur.path.get(i);
                }
                cur.end--;
            }
        }

        public int prefixNumber(String str) {
            if (null == str) {
                return 0;
            }
            char[] chars = str.toCharArray();
            Node2 cur = root;

            for (char c : chars) {
                int i = c - 'a'; // 以0->a为数组起点, 算出相对的数组下标
                if (!cur.path.containsKey(i)) {
                    return 0;
                }
                cur = cur.path.get(i);
            }
            return cur.pass;
        }
    }

    static class TestMap {
        private Map<String, Integer> map;

        public TestMap() {
            this.map = new HashMap<>();
        }

        public void insert(String str) {
            if (map.containsKey(str)) {
                map.put(str, map.get(str) + 1);
            } else {
                map.put(str, 1);
            }
        }

        public void delete(String str) {
            if (!map.containsKey(str)) {
                return;
            } else {
                if (map.get(str) == 1) {
                    map.remove(str);
                } else {
                    map.put(str, map.get(str) - 1);
                }
            }
        }

        public int search(String str) {
            if (str == null || !map.containsKey(str)) {
                return 0;
            }
            return map.get(str);
        }

        public int prefixNumber(String str) {
            if (str == null) {
                return 0;
            }
            int count = 0;
            for (Map.Entry<String, Integer> entry : map.entrySet()) {
                if (entry.getKey().startsWith(str)) {
                    count += entry.getValue();
                }
            }
            return count;
        }
    }


    public static void main(String[] args) {
        int maxLen = 20;
        int maxArrLen = 30;
        int times = 200000;
        for (int i = 0; i < times; i++) {
            String[] str = randomStrArr(maxLen, maxArrLen);
            TireTree tireTree = new TireTree();
            TireTree2 tireTree2 = new TireTree2();
            TestMap testMap = new TestMap();
            for (String s : str) {
                double decide = Math.random();
                if (decide < 0.25) {
                    tireTree.insert(s);
                    tireTree2.insert(s);
                    testMap.insert(s);
                } else if (decide < 0.5) {
                    tireTree.delete(s);
                    tireTree2.delete(s);
                    testMap.delete(s);
                } else if (decide < 0.75) {
                    int s1 = tireTree.search(s);
                    int s2 = tireTree2.search(s);
                    int s3 = testMap.search(s);
                    if (s1 != s2 || s2 != s3) {
                        System.out.println("opps!!");
                        return;
                    }
                } else {
                    int s1 = tireTree.prefixNumber(s);
                    int s2 = tireTree2.prefixNumber(s);
                    int s3 = testMap.prefixNumber(s);
                    if (s1 != s2 || s2 != s3) {
                        System.out.println("opps!!");
                        return;
                    }
                }
            }
        }
    }

    private static String[] randomStrArr(int maxLen, int maxArrLen) {
        int arrLen = (int) (Math.random() * maxArrLen) + 1;
        String[] strings = new String[arrLen];
        for (int i = 0; i < arrLen; i++) {
            strings[i] = randomStr(maxLen);
        }
        return strings;
    }

    private static String randomStr(int maxLen) {
        char[] cs = new char[(int) (Math.random() * maxLen) + 1];
        for (int i = 0; i < cs.length; i++) {
            int b = (int) (Math.random() * 26);
            cs[i] = (char) ('a' + b);
        }
        return String.valueOf(cs);
    }
}
