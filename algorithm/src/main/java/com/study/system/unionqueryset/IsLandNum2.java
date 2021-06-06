package com.study.system.unionqueryset;

import java.util.HashSet;
import java.util.Set;

/**
 * @description: 岛问题2
 * @author: wj2wml@qq.com
 * @date: 2021-06-03 21:43
 **/
public class IsLandNum2 {
    static class UnionFind {
        int[] parent;
        int[] size;
        int[] help;
        int sets;
        int col;

        public UnionFind(int[][] arr) {
            int m = arr.length;
            int n = arr[0].length;
            col = n;
            int N = m * n;
            parent = new int[N];
            size = new int[N];
            help = new int[N];

//            for (int i = 0; i < m; i++) {
//                for (int j = 0; j < n; j++) {
//                    int index = index(i, j);
//                    parent[index] = index;
//                    size[index] = 1;
//                }
//            }
        }

        public int index(int m, int n) {
            return m * col + n;
        }

        public void union(int m, int n, int a, int b) {
            int p1 = getParent(index(m, n));
            int p2 = getParent(index(a, b));

            if (p1 != p2) {
                sets--;
                int s1 = size[index(m,n)];
                int s2 = size[index(a,b)];
                int big = s1 >= s2 ? p1 : p2;
                int small = big == p1 ? p2 : p1;
                parent[small] = big;
                size[small] = 0;
                size[big] = s1 + s2;
            }
        }

        private void setsAdd() {
            sets++;
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
        int[][] arr = new int[m][n];
        UnionFind unionFind = new UnionFind(arr);
        int row = position.length;
        int[] res = new int[row];
        for (int i = 0; i < row; i++) {
            int a = position[i][0];
            int b = position[i][1];
            if (arr[a][b] == 1) {
                // 当前岛屿已经改变过了, 不需要重复计算
                res[i] = res[i-1];
                continue;
            }
            arr[a][b] = 1;
            unionFind.setsAdd();
            if (a > 0 && arr[a - 1][b] == 1) {
                unionFind.union(a, b, a - 1, b);
            }
            if (a < m - 1 && arr[a + 1][b] == 1) {
                unionFind.union(a, b, a + 1, b);
            }
            if (b > 0 && arr[a][b - 1] == 1) {
                unionFind.union(a, b, a, b - 1);
            }
            if (b < n - 1 && arr[a][b + 1] == 1) {
                unionFind.union(a, b, a, b + 1);
            }
            res[i] = unionFind.sets();
        }
        return res;
    }

    public static int[] numIslands2(int m, int n, int[][] position) {
        if (m < 1 || n < 1 || position.length < 1 || position[0].length < 1) {
            return new int[0];
        }
        int[][] arr = new int[m][n];

        int row = position.length;
        int[] res = new int[row];
        for (int i = 0; i < row; i++) {
            int a = position[i][0];
            int b = position[i][1];

            if (arr[a][b] == 1) {
                res[i] = res[i-1];
                continue;
            }

            arr[a][b] = 1;
            int count = 0;
            Set<String> set = new HashSet<>();
            for (int j = 0; j < m; j++) {
                for (int k = 0; k < n; k++) {
                    if (arr[j][k] == 1) {
                        count++;
                        infect(arr, j, k, set);
                    }
                }
            }
            // 被感染的岛还原
            for (String s : set) {
                String[] strings = s.split("-");
                a = Integer.parseInt(strings[0]);
                b = Integer.parseInt(strings[1]);
                arr[a][b] = 1;
            }
            set.clear();
            res[i] = count;
        }
        return res;
    }

    private static void infect(int[][] arr, int a, int b, Set<String> set) {
        if (a < 0 || a >= arr.length || b < 0 || b >= arr[0].length || arr[a][b] != 1) {
            return;
        }
        set.add(a + "-" + b);
        arr[a][b] = 0;
        infect(arr, a - 1, b, set);
        infect(arr, a + 1, b, set);
        infect(arr, a, b - 1, set);
        infect(arr, a, b + 1, set);
    }

    public static void main(String[] args) {
        int maxLength = 15;
        int maxM = 15;
        int maxN = 15;
        int times = 200000;
        for (int i = 0; i < times; i++) {
            int m = (int) (Math.random() * maxM);
            int n = (int) (Math.random() * maxN);
            int[][] position = makeArr(maxLength, m, n);
            int[] res1 = numIslands(m, n, position);
            int[] res2 = numIslands2(m, n, position);
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
        System.out.println("success!!!");
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
