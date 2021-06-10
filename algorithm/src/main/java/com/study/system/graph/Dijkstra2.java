package com.study.system.graph;

import com.study.system.graph.model.Edge;
import com.study.system.graph.model.Node;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * @description: 迪杰斯特拉算法求最短路径
 * @author: wj2wml@qq.com
 * @date: 2021-06-09 21:29
 **/
public class Dijkstra2 {
    // size 图中点的个数，没有给的话就需要自己算了
    public static Map<Node, Integer> minDis2(Node start, int size) {
        Map<Node, Integer> disMap = new HashMap<>();

        HeapUpgrade heap = new HeapUpgrade(size);
        heap.addOrUpdate(start, 0);

        while (!heap.isEmpty()) {
            HeapUpgrade.NodeRecord record = heap.pop();
            Node cur = record.node;
            int distance = record.distance;

            for (Edge edge : cur.edges) {
                Node toNode = edge.to;
                int toDis = edge.weight;
                heap.addOrUpdate(toNode, distance + toDis);
            }
            disMap.put(cur, distance);
        }

        return disMap;
    }
}
