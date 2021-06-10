package com.study.system.graph;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @description: 使用bfs解决拓扑排序
 * @author: wj2wml@qq.com
 * @date: 2021-06-08 20:58
 **/
// 题目链接：https://www.lintcode.com/problem/topological-sorting
public class TopologicalOrderDFS2 {
    class DirectedGraphNode {
        int label; // 点的值
        List<DirectedGraphNode> neighbors; // 所有相连点

        DirectedGraphNode(int x) {
            label = x;
            neighbors = new ArrayList<DirectedGraphNode>();
        }
    }

    class Record {
        DirectedGraphNode node;
        int deep; // 表示达到最大深度

        public Record(DirectedGraphNode node, int deep) {
            this.node = node;
            this.deep = deep;
        }
    }

    public ArrayList<DirectedGraphNode> topSort(ArrayList<DirectedGraphNode> graph) {
        // 点与 过点数 建立映射
        Map<DirectedGraphNode, Record> recordMap = new HashMap<>(graph.size());

        for (DirectedGraphNode node : graph) {
            f(node, recordMap);
        }

        List<Record> records = new ArrayList<>();
        for (Record value : recordMap.values()) {
            records.add(value);
        }

        records.sort((o1, o2) -> o2.deep - o1.deep);

        ArrayList<DirectedGraphNode> result = new ArrayList<>();

        for (Record record : records) {
            result.add(record.node);
        }
        return  result;

    }

    private Record f(DirectedGraphNode node, Map<DirectedGraphNode, Record> recordMap) {
        // 如果之前计算过了，直接返回
        if (recordMap.containsKey(node)) {
            return recordMap.get(node);
        }

        int deep = 0;
        for (DirectedGraphNode neighbor : node.neighbors) {
            // 当前最大深度就是 左右相邻节点中最大深度 + 1
            deep = Math.max(deep, f(neighbor, recordMap).deep);
        }
        Record record = new Record(node, deep + 1);
        recordMap.put(node, record);
        return record;
    }
}
