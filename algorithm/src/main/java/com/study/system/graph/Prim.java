package com.study.system.graph;

import com.study.system.graph.model.Edge;
import com.study.system.graph.model.Graph;
import com.study.system.graph.model.Node;

import java.util.Comparator;
import java.util.HashSet;
import java.util.PriorityQueue;
import java.util.Set;

/**
 * @description: 最小生成树P算法
 * @author: wj2wml@qq.com
 * @date: 2021-06-08 22:59
 **/
public class Prim {

    public static Set<Edge> getLeastTree(Graph graph) {
        PriorityQueue<Edge> heap = new PriorityQueue<>(Comparator.comparingInt(o -> o.weight));

        Set<Edge> result = new HashSet<>();
        // 已经 解锁过的点
        Set<Node> doneNodes = new HashSet<>();

        for (Node node : graph.nodes.values()) {
            if (!doneNodes.contains(node)) {
                doneNodes.add(node);
                // 解锁所有边
                for (Edge edge : node.edges) {
                    heap.add(edge);
                }
            }
            while (!heap.isEmpty()) {
                Edge edge = heap.poll();
                // 边指向的是没有到过的点，则不会形成环
                if (!doneNodes.contains(edge.to)) {
                    result.add(edge);
                    Node toNode = edge.to;
                    doneNodes.add(toNode);
                    // 解锁所有新的边
                    for (Edge next : toNode.edges) {
                        heap.add(next);
                    }
                }
            }
            break; // 如果是森林，则不能break
        }
        return result;
    }
}
