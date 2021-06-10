package com.study.system.graph;

import com.study.system.graph.model.Edge;
import com.study.system.graph.model.Graph;
import com.study.system.graph.model.Node;

import java.util.*;

/**
 * @description: 最小生成树K算法
 * @author: wj2wml@qq.com
 * @date: 2021-06-08 22:21
 **/
public class Kruskal {
    static class UnionFind {
        Map<Node, Node> parentMap;
        Map<Node, Integer> sizeMap;
        List<Node> helpList;

        public UnionFind(Collection<Node> nodes) {
            parentMap = new HashMap<>();
            sizeMap = new HashMap<>();
            helpList = new ArrayList<>();
            for (Node node : nodes) {
                parentMap.put(node, node);
                sizeMap.put(node, 0);
            }
        }

        public void union(Node a, Node b) {
            Node ap = parent(a);
            Node bp = parent(b);

            if (ap != bp) {
                int aSize = sizeMap.get(ap);
                int bSize = sizeMap.get(bp);
                Node big = aSize >= bSize ? ap : bp;
                Node small = big == ap ? bp : ap;
                parentMap.put(small, big);
                sizeMap.put(big, aSize + bSize);
            }
        }

        public boolean isSame(Node a, Node b) {
            return parent(a) != parent(b);
        }

        public Node parent(Node cur) {
            while (cur != parentMap.get(cur)) {
                helpList.add(cur);
                cur = parentMap.get(cur);
            }
            for (Node node : helpList) {
                parentMap.put(node, cur);
            }
            helpList.clear();
            return cur;
        }
    }

    public static Set<Edge> getLestTree(Graph graph) {
        // 根据边权重 排序的小根堆
        PriorityQueue<Edge> heap = new PriorityQueue<>(Comparator.comparingInt(o -> o.weight));

        for (Edge edge : graph.edges) {
            heap.add(edge);
        }

        UnionFind unionFind = new UnionFind(graph.nodes.values());
        Set<Edge> result = new HashSet<>();

        while (!heap.isEmpty()) {
            // 从堆中拿取权重最小的边
            Edge edge = heap.poll();
            // 查看是否 边上 的两个点在同一集合 中
            if (!unionFind.isSame(edge.from, edge.to)) {
                result.add(edge);
                // 合并集合
                unionFind.union(edge.from, edge.to);
            }
        }
        return result;
    }
}
