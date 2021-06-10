package com.study.system.graph.model;

/**
 * @description: 边
 * @author: wj2wml@qq.com
 * @date: 2021-06-06 21:52
 **/
public class Edge {
    public int weight; // 边的权重
    public Node from; // 边的起点，有向图才有意义
    public Node to; // 边的终点，有向图才有意义
}
