package com.study.system.graph;

import com.study.system.graph.model.Node;

import java.util.*;

/**
 * @description: 加强堆
 * @author: wj2wml@qq.com
 * @date: 2021-06-09 22:08
 **/
public class HeapUpgrade {
    private List<Node> heap;
    private Map<Node, Integer> indexMap;
    private Map<Node, Integer> disMap;
    private int size; // 堆上有几个点

    public HeapUpgrade(int size) {
        heap = new ArrayList<>(size);
        indexMap = new HashMap<>(size);
        disMap = new HashMap<>(size);
        this.size = 0;
    }

    public boolean isEmpty() {
        return size == 0;
    }

    public NodeRecord pop() {
        Node popNode = heap.get(0);
        NodeRecord nodeRecord = new NodeRecord(popNode, disMap.get(popNode));
        // 与末尾交换
        swap(0, --size);
        // 删除索引，为了表示 被选中过，将索引表示未 - 1
        indexMap.put(popNode, -1);
        heap.remove(popNode);
        // 自上往下做堆化
        heapify(0);
        return nodeRecord;
    }

    private void heapify(int index) {
        // 找打子节点中的最大值
        int left = index * 2 + 1;
        while (left < size) {
            int smaller = left + 1 < size && aSmallThanB(left, left + 1) ? left : left + 1;
            if (aSmallThanB(smaller, index)) {
                swap(smaller, index);
                index = smaller;
                left = index * 2 + 1;
            } else {
                break;
            }
        }
    }

    private boolean aSmallThanB(int a, int b) {
        return disMap.get(a).compareTo(disMap.get(b)) < 0;
    }

    public void addOrUpdate(Node node, int dis) {
        // 判断是否在堆中
        if (inHeap(node)) {
            // 取 之前的距离 和 传入距离的 最小值
            disMap.put(node, Math.min(disMap.get(node), dis));
            // 上面取最小值，所以 值只会变小，只需要向上做一次堆化
            insertHeapify(indexMap.get(node));
        }
        // 判断是否被删除（选过）的点
        if (notDeleted(node)) {
            // 没有被删除，说明是未添加的点
            heap.add(node);
            indexMap.put(node, size);
            disMap.put(node, dis);
            // 自下而上做一次堆化
            insertHeapify(size++);
        }
    }

    private void insertHeapify(int index) {
        // 与自己的父亲 比较
        while (disMap.get(heap.get(index)) < disMap.get(heap.get((index - 1) / 2))) {
            swap(index, (index - 1) / 2);
            index = (index - 1) / 2;
        }
    }

    private void swap(int a, int b) {
        Node aNode = heap.get(a);
        Node bNode = heap.get(b);

        heap.set(a, bNode);
        heap.set(b, aNode);
        indexMap.put(aNode, b);
        indexMap.put(bNode, a);
    }

    private boolean inHeap(Node node) {
        // 表示node加入过 并且 没有被删除
        return indexMap.containsKey(node) && indexMap.get(node) != -1;
    }

    private boolean notDeleted(Node node) {
        return indexMap.get(node) != -1;
    }


    public static class NodeRecord {
        public Node node;
        public int distance;

        public NodeRecord(Node node, int distance) {
            this.node = node;
            this.distance = distance;
        }
    }
}
