package com.study.system.unionqueryset;

/**
 * @description: 朋友圈问题
 * @author: wj2wml@qq.com
 * @date: 2021-05-31 20:49
 **/
public class FriendCircles {
    static class UnionFind {
        // parent[i]=k , k为 i的顶层朋友
        private int[] parent;
        // size[i]=k , 顶层 i 的 所有朋友个数
        private int[] size;
        private int[] help;
        // 朋友圈的个数
        private int sets;

        public UnionFind(int N) {
            parent = new int[N];
            size = new int[N];
            help = new int[N];
            sets = N;
            for (int i = 0; i < N; i++) {
                parent[i] = i;
                size[i] = 1;
            }
        }

        public int findParent(int i) {
            int j = 0;
            while (i != parent[i]) {
                help[j++] = i;
                i = parent[i];
            }
            for (j--; j >= 0; j--) {
                parent[help[j]] = i;
            }
            return i;
        }

        public void union(int i, int j) {
            int p1 = findParent(i);
            int p2 = findParent(j);

            if (p1 != p2) {
                int big = size[p1] >= size[p2] ? p1 : p2;
                int small = big == p1 ? p2 : p1;
                parent[small] = big;
                size[big] = size[p1] + size[p2];
                size[small] = 0;
                sets--;
            }
        }

        public int sets() {
            return sets;
        }
    }

    public static int findCircleNum(int[][] M) {
        if (M == null || M.length == 0) {
            return 0;
        }
        int N = M.length;
        UnionFind unionFind = new UnionFind(N);
        for (int i = 0; i < N; i++) {
            for (int j = i + 1; j < N; j++) {
                // i j 是朋友关系
                if (M[i][j] == 1) {
                    unionFind.union(i, j);
                }
            }
        }
        return unionFind.sets();
    }
}