package com.study.system.unionqueryset;

import java.util.HashMap;
import java.util.List;
import java.util.Stack;

/**
 * @description: 并查集
 * @author: wj2wml@qq.com
 * @date: 2021-05-31 21:31
 **/
public class UnionFindSet {
    /**
     *  用来对 V 进行包装，外界无感知
     */
    static class Node<V> {
        V v;
        public Node(V v) {
            this.v = v;
        }
    }

    public static class UnionFind<V> {
        // v 与包装类关系映射
        public HashMap<V, Node<V>> nodes;
        // key:node value: node节点的顶层节点
        public HashMap<Node<V>, Node<V>> parents;
        // key:node （顶层节点） value:集合大小
        public HashMap<Node<V>, Integer> sizeMap;

        public UnionFind(List<V> values) {
            nodes = new HashMap<>();
            parents = new HashMap<>();
            sizeMap = new HashMap<>();

            for (V v : values) {
                Node<V> node = new Node<>(v);
                nodes.put(v, node);
                // 初始化：自己是自己的顶层节点
                parents.put(node, node);
                // 自己跟自己是一个集合。集合大小1
                sizeMap.put(node, 1);
            }
        }

        /**
         * 找到顶层父节点
         */
        public Node<V> findTopParent(Node<V> cur) {
            // 存放 遍历过的所有节点
            Stack<Node<V>> stack = new Stack<>();
            while (cur != parents.get(cur)) {
                stack.push(cur);
                cur = parents.get(cur);
            }
            // 优化，链表扁平化，已经合并的链表，将其 顶层parent 都更新
            while (!stack.isEmpty()) {
                parents.put(stack.pop(), cur);
            }
            return cur;
        }

        /**
         * 是否是同一集合：判断顶层父节点 是不是同一个
         */
        public boolean isSameSet(V a, V b) {
            return findTopParent(nodes.get(a)) == findTopParent(nodes.get(b));
        }

        /**
         *  合并两个集合
         */
        public void union(V a, V b) {
            // 找到父节点
            Node<V> aParent = findTopParent(nodes.get(a));
            Node<V> bParent = findTopParent(nodes.get(b));
            // 顶层节点不同才需要合并, 小集合 挂 大集合
            if (aParent != bParent) {
                int aSize = sizeMap.get(aParent);
                int bSize = sizeMap.get(bParent);
                Node<V> big = aSize >= bSize ? aParent : bParent;
                Node<V> small = big == aParent ? bParent : aParent;
                parents.put(small, big);
                sizeMap.put(big, aSize + bSize);
                // 小集合的 大小 在合并 后 没有意义
                sizeMap.remove(small);
            }
        }

        /**
         *  返回集合个数
         */
        public int sets() {
            return sizeMap.size();
        }
    }
}
