package com.study.system.unionqueryset;

import java.util.*;

/**
 * @description: 岛问题2
 * @author: wj2wml@qq.com
 * @date: 2021-06-03 21:43
 **/
public class IsLandNum2Plus {
    static class UnionFind {
        int[] parent;
        int[] size;
        int[] help;
        int sets; // 岛屿数量
        int row; // 行数
        int col; // 列数


        public UnionFind(int m, int n) {
            row = m;
            col = n;
            int N = m * n;
            parent = new int[N];
            size = new int[N];
            help = new int[N];
        }

        public int index(int a, int b) {
            return a * col + b;
        }

        // 加入新的岛屿
        public int connect(int i, int j) {
            int index = index(i, j);
            // 判断当前 位置 是否已经 加入  过
            if (size[index] == 0) {
                // 没有加入，则初始化数据
                parent[index] = index;
                size[index] = 1;
                sets++;
                // 再去尝试联合 周围的岛屿
                union(i, j, i + 1, j);
                union(i, j, i - 1, j);
                union(i, j, i, j - 1);
                union(i, j, i, j + 1);
            }
            return sets;
        }

        public void union(int m, int n, int a, int b) {
            // 越界处理
            if (m < 0 || m >= row || n < 0 || n >= col || a < 0 || a >= row || b < 0 || b >= col) {
                return;
            }

            int index1 = index(m, n);
            int index2 = index(a, b);
            // 其中一个不是岛屿，则不需要合并
            if (size[index1] == 0 || size[index2] == 0) {
                return;
            }

            int p1 = getParent(index1);
            int p2 = getParent(index2);

            if (p1 != p2) {
                if (size[p1] >= size[p2]) {
                    size[p1] += size[p2];
                    parent[p2] = p1;
                } else {
                    size[p2] += size[p1];
                    parent[p1] = p2;
                }
                sets--;
            }
        }


        private int getParent(int cur) {
            int i = 0;
            while (cur != parent[cur]) {
                help[i++] = cur;
                cur = parent[cur];
            }
            for (i--; i >= 0; i--) {
                parent[help[i]] = cur;
            }
            return cur;
        }

        public int sets() {
            return sets;
        }
    }

    public static int[] numIslands(int m, int n, int[][] position) {
        if (m < 1 || n < 1 || position.length < 1 || position[0].length < 1) {
            return new int[0];
        }

        UnionFind unionFind = new UnionFind(m, n);
        int row = position.length;
        int[] res = new int[row];

        for (int i = 0; i < row; i++) {
            res[i] = unionFind.connect(position[i][0], position[i][1]);
        }
        return res;
    }

    public static class UnionFind2 {
        private Map<String, String> parent;
        private Map<String, Integer> size;
        private List<String> help;
        private int sets;

        public UnionFind2() {
            parent = new HashMap<>();
            size = new HashMap<>();
            help = new ArrayList<>();
            sets = 0;
        }

        public int connect(int i, int j) {
            String key = getKey(i, j);
            // 当前坐标 之前 没有处理过才处理
            if (!parent.containsKey(key)) {
                parent.put(key, key);
                size.put(key, 1);
                sets++;
                union(key, getKey(i - 1, j)); // up
                union(key, getKey(i + 1, j)); // down
                union(key, getKey(i, j - 1)); // left
                union(key, getKey(i, j + 1)); // right
            }
            return sets;
        }

        private void union(String k1, String k2) {
            // 两个都是岛屿 才进行合并. 这里就不用考虑越界的问题了
            if (parent.containsKey(k1) && parent.containsKey(k2)) {
                String p1 = getParent(k1);
                String p2 = getParent(k2);

                if (!p1.equals(p2)) {
                    int s1 = size.get(p1);
                    int s2 = size.get(p2);

                    String big = s1 >= s2 ? p1 : p2;
                    String small = big.equals(p1) ? p2 : p1;

                    parent.put(small, big);
                    size.put(big, s1 + s2);
                    sets--;
                }
            }
        }

        private String getParent(String key) {

            while (!parent.get(key).equals(key)) {
                help.add(key);
                key = parent.get(key);
            }

            for (String k : help) {
                parent.put(k, key);
            }
            help.clear();
            return key;
        }

        private String getKey(int i, int j) {
            return i + "-" + j;
        }

    }

    public static int[] numIslands2(int m, int n, int[][] position) {
        if (m < 1 || n < 1 || position.length < 1 || position[0].length < 1) {
            return new int[0];
        }

        UnionFind2 unionFind = new UnionFind2();

        int row = position.length;
        int[] res = new int[row];
        for (int i = 0; i < row; i++) {
            res[i] = unionFind.connect(position[i][0], position[i][1]);
        }
        return res;
    }


    public static void main(String[] args) {
        int maxLength = 10;
        int maxM = 1000;
        int maxN = 1000;
        int times = 200000;

        int c1 = 0;
        int c2 = 0;

        for (int i = 0; i < times; i++) {
            int m = (int) (Math.random() * maxM);
            int n = (int) (Math.random() * maxN);
            int[][] position = makeArr(maxLength, m, n);

            long l1 = System.currentTimeMillis();
            int[] res1 = numIslands(m, n, position);
            long l2 = System.currentTimeMillis();
            int[] res2 = numIslands2(m, n, position);
            long l3 = System.currentTimeMillis();

            if ((l2 - l1) > (l3 - l2)) {
                c2++;
            }
            if ((l2 - l1) < (l3 - l2)) {
                c1++;
            }

            if (res1.length != res2.length) {
                System.out.println("完犊子了！");
                break;
            }
            boolean ans = true;
            for (int j = 0; j < res1.length; j++) {
                if (res1[j] != res2[j]) {
                    ans = false;
                    break;
                }
            }
            if (!ans) {
                System.out.println("完犊子了！");
                numIslands(m, n, position);
                numIslands2(m, n, position);
                break;
            }
        }
        // 随着 m n 的值增大， c2 与 c1 的差值越大
        System.out.println("success!!! c1 win:" + c1 + ", c2 win:" + c2);
    }

    private static int[][] makeArr(int maxLength, int a, int b) {
        int m = (int) (Math.random() * maxLength);
        int[][] arr = new int[m][2];
        Set<String> set = new HashSet<>();
        for (int i = 0; i < m; i++) {
            arr[i][0] = (int) (Math.random() * a);
            arr[i][1] = (int) (Math.random() * b);
        }
        return arr;
    }
}
