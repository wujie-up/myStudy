package com.study.system.graph;


import com.study.system.graph.model.Node;

import java.util.HashSet;
import java.util.Set;
import java.util.Stack;

/**
 * @description: 深度优先遍历
 * @author: wj2wml@qq.com
 * @date: 2021-06-06 21:47
 **/
public class DFS {

    public static void dfs(Node node) {
        if (null == node) {
            return;
        }
        Stack<Node> stack = new Stack<>();
        // 保存已经走过的点
        Set<Node> set = new HashSet<>();

        stack.push(node);
        set.add(node);
        System.out.println(node.value);

        while (!stack.isEmpty()) {
            Node cur = stack.pop();
            for (Node next : cur.nexts) {
                if (!set.contains(next)) {
                    stack.push(cur);
                    stack.push(next);
                    set.add(next);
                    System.out.println(next.value);
                    break;
                }
            }
        }
    }
}
