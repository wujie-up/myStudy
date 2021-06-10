package com.study.system.graph;

import com.study.system.graph.model.Edge;
import com.study.system.graph.model.Graph;
import com.study.system.graph.model.Node;

import java.util.*;

/**
 * @description: 拓扑排序
 * @author: wj2wml@qq.com
 * @date: 2021-06-08 20:41
 **/
public class TopologySort {

    public static List<Node> sortTopology(Graph graph) {
        // 记录点对应入度数
        Map<Node, Integer> inMap = new HashMap<>();
        // 入度为0的队列
        Queue<Node> zeroQueue = new LinkedList<>();
        for (Node node : graph.nodes.values()) {
            inMap.put(node, node.in);
            if (node.in == 0) {
                zeroQueue.add(node);
            }
        }
        // 排序结果
        List<Node> result = new ArrayList<>();

        // 从队列中取出入度为0的点
        while (!zeroQueue.isEmpty()) {
            Node cur = zeroQueue.poll();
            result.add(cur);
            for (Node next : cur.nexts) {
                // 删除当前节点作为入度 的影响
                inMap.put(next, inMap.get(next) - 1);
                if (inMap.get(next) == 0) {
                    zeroQueue.add(next);
                }
            }
        }
        return result;
    }
}
