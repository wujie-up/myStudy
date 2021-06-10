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
public class Dijkstra {

    public static Map<Node, Integer> minDis1(Node start) {
        Map<Node, Integer> disMap = new HashMap<>();
        disMap.put(start, 0);

        // 已经被选过 的 点
        Set<Node> selectedSet= new HashSet<>();
        Node minDisNode = getMinDisAndNotSelectedNode(disMap, selectedSet);
        while (minDisNode != null) {
            selectedSet.add(minDisNode);
            // 最小距离点能到的所有边
            for (Edge edge : minDisNode.edges) {
                Node toNode = edge.to;
                // disMap中没有，说明之前的距离没有算过 肯定是 无穷大
                if (!disMap.containsKey(toNode)) {
                    disMap.put(toNode, disMap.get(minDisNode) + edge.weight);
                } else {
                    // 之前算过距离了，则比较之前的距离 和 当前点距离 + 两点间的距离
                    disMap.put(toNode, Math.min(disMap.get(toNode), disMap.get(minDisNode) + edge.weight));
                }
            }
            // 再从disMap中拿到最小距离 点，作为下次循环计算点
            minDisNode = getMinDisAndNotSelectedNode(disMap, selectedSet);
        }
        return disMap;
    }

    private static Node getMinDisAndNotSelectedNode(Map<Node, Integer> disMap, Set<Node> selectedSet) {
        Node minDisNode = null;
        int minDistance = Integer.MAX_VALUE;
        for (Map.Entry<Node, Integer> entry : disMap.entrySet()) {
            if (!selectedSet.contains(entry.getKey()) && entry.getValue() < minDistance) {
                minDisNode = entry.getKey();
                minDistance = entry.getValue();
            }
        }
        return minDisNode;
    }
}
