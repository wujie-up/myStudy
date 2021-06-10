package com.study.system.graph.model;

import java.util.ArrayList;
import java.util.List;

/**
 * @description: 点
 * @author: wj2wml@qq.com
 * @date: 2021-06-06 21:52
 **/
public class Node {
    public int value;
    public int in; // 进线数量
    public int out; // 出线数量
    public List<Node> nexts; // 连接的所有节点
    public List<Edge> edges; // 连接的所有边

    public Node(int value) {
        this.value = value;
        in = 0;
        out = 0;
        nexts = new ArrayList<>();
        edges = new ArrayList<>();
    }
}
