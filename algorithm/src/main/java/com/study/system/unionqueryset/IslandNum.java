package com.study.system.unionqueryset;

/**
 * @description: 岛问题
 * @author: wj2wml@qq.com
 * @date: 2021-05-31 22:29
 **/
public class IslandNum {
    static class UnionFind {
        int[] parent;
        int[] size;
        int[] help;
        int sets;// 岛的个数
        int row; // 行
        int column; // 列

        // m 行 n 列
        public UnionFind(char[][] arr) {
            int m = arr.length;
            int n = arr[0].length;
            parent = new int[m * n];
            size = new int[m * n];
            help = new int[m * n];
            row = m;
            column = n;
            for (int i = 0; i < m; i++) {
                for (int j = 0; j < n; j++) {
                    if (arr[i][j] == '1') {
                        sets++;
                        parent[i * column + j] = i * column + j;
                        size[i * column + j] = 1;
                    }
                }
            }
        }

        public int findParent(int i, int j) {
            int cur = i * column + j;
            int hi = 0;
            while (cur != parent[cur]) {
                help[hi++] = cur;
                cur = parent[cur];
            }
            for (hi--; hi >= 0; hi--) {
                parent[help[hi]] = cur;
            }
            return cur;
        }

        public void union(int i, int j, int m, int n) {
            int p1 = findParent(i, j);
            int p2 = findParent(m, n);

            if (p1 != p2) {
                int big = size[i * column + j] >= size[m * column + n] ? p1 : p2;
                int small = big == p1 ? p2 : p1;

                parent[small] = big;
                size[small] = 0;
                sets--;
            }
        }

        public int sets() {
            return sets;
        }
    }


    public static int numIslands(char[][] arr) {
        int m = arr.length;
        int n = arr[0].length;

        UnionFind unionFind = new UnionFind(arr);
        for (int i = 0; i < m; i++) {
            for (int j = 0; j < n; j++) {
                if (arr[i][j] == '1') {
                    if (i + 1 < m && arr[i + 1][j] == '1') {
                        unionFind.union(i, j, i + 1, j);
                    }
                    if (j + 1 < n && arr[i][j + 1] == '1') {
                        unionFind.union(i, j, i, j + 1);
                    }
                }
            }
        }
        return unionFind.sets();
    }

    public static int numIslands2(char[][] arr) {
        int num = 0;
        int m = arr.length;
        int n = arr[0].length;

        for (int i = 0; i < m; i++) {
            for (int j = 0; j < n; j++) {
                if (arr[i][j] == '1') {
                    num++;
                    // 感染周围岛屿 (将自己和上下左右都变成 '0'， 这样下次遍历就不会计算这些地方)
                    infect(i, j, arr);
                }
            }
        }
        return num;
    }

    private static void infect(int i, int j, char[][] arr) {
        if (i < 0 || i >= arr.length || j >= arr[0].length || j < 0 || arr[i][j] != '1') {
            return;
        }
        arr[i][j] = '0';
        infect(i - 1, j, arr);
        infect(i + 1, j, arr);
        infect(i, j - 1, arr);
        infect(i, j + 1, arr);
    }

    public static void main(String[] args) {
        int maxLength = 20;
        int times = 800000;
        int l1 = 0;
        int l2 = 0;
        for (int i = 0; i < times; i++) {
            char[][] arr = makeArr(maxLength);
            long s1 = System.currentTimeMillis();
            int m1 = numIslands(arr);
            long s2 = System.currentTimeMillis();

            int m2 = numIslands2(arr);
            long s3 = System.currentTimeMillis();

            if (m1 != m2) {
                System.out.println("完犊子了！");
                break;
            }
            if ((s2 - s1) > (s3 - s2)) {
                l2++;
            } else if ((s3 - s2) > (s2 - s1)) {
                l1++;
            }
        }
        System.out.println("success!!! l1 :" + l1 + "l2 : " + l2);
    }

    private static char[][] makeArr(int maxLength) {
        int m = (int) (Math.random() * maxLength) + 1;
        int n = (int) (Math.random() * maxLength) + 1;
        char[][] arr = new char[m][n];
        for (int i = 0; i < m; i++) {
            for (int j = 0; j < n; j++) {
                if (Math.random() < 0.5) {
                    arr[i][j] = '1';
                } else {
                    arr[i][j] = '0';
                }
            }
        }
        return arr;
    }
}
