package com.study.system.graph;

import java.util.*;

/**
 * @description: 使用bfs解决拓扑排序
 * @author: wj2wml@qq.com
 * @date: 2021-06-08 20:58
 **/
// 题目链接：https://www.lintcode.com/problem/topological-sorting
public class TopologicalOrderBFS {

    class DirectedGraphNode {
        int label; // 点的值
        List<DirectedGraphNode> neighbors; // 所有相连点

        DirectedGraphNode(int x) {
            label = x;
            neighbors = new ArrayList<DirectedGraphNode>();
        }
    }

    public ArrayList<DirectedGraphNode> topSort(ArrayList<DirectedGraphNode> graph) {
        Map<DirectedGraphNode, Integer> inMap = new HashMap<>(graph.size());
        // 初始化所有点的 入度数量
        for (DirectedGraphNode node : graph) {
            if (!inMap.containsKey(node)) {
                inMap.put(node, 0);
            }
            for (DirectedGraphNode neighbor : node.neighbors) {
                if (!inMap.containsKey(neighbor)) {
                    inMap.put(neighbor, 1);
                } else {
                    inMap.put(neighbor, inMap.get(neighbor) + 1);
                }
            }
        }

        Queue<DirectedGraphNode> zeroQueue = new LinkedList<>();
        for (Map.Entry<DirectedGraphNode, Integer> entry : inMap.entrySet()) {
            if (entry.getValue() == 0) {
                zeroQueue.add(entry.getKey());
            }
        }

        ArrayList<DirectedGraphNode> result = new ArrayList<>(graph.size());

        while (!zeroQueue.isEmpty()) {
            DirectedGraphNode cur = zeroQueue.poll();
            result.add(cur);
            for (DirectedGraphNode neighbor : cur.neighbors) {
                inMap.put(neighbor, inMap.get(neighbor) - 1);
                if (inMap.get(neighbor) == 0) {
                    zeroQueue.add(neighbor);
                }
            }
        }
        return result;
    }
}
