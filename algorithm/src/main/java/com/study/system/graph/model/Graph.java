package com.study.system.graph.model;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @description: 图
 * @author: wj2wml@qq.com
 * @date: 2021-06-08 20:42
 **/
public class Graph {
    public Map<Integer, Node> nodes;
    public Set<Edge> edges;

    public Graph(Map<Integer, Node> nodes, Set<Edge> edges) {
        this.nodes = nodes;
        this.edges = edges;
    }
}
