package com.study.system.tree.recursion;

import java.util.ArrayList;
import java.util.List;

/**
 * @description: 最大party快乐值
 * @author: wj2wml@qq.com
 * @date: 2021-05-25 21:58
 **/
public class GetMaxPartyHappy {
    static class Employee {
        public int happy; // 这名员工可以带来的快乐值
        List<Employee> subordinates; // 这名员工有哪些直接下级

        public Employee(int happy, List<Employee> subordinates) {
            this.happy = happy;
            this.subordinates = subordinates;
        }
    }

    static class Info {
        int noComeHappy;
        int comeHappy;

        public Info(int noComeHappy, int comeHappy) {
            this.noComeHappy = noComeHappy;
            this.comeHappy = comeHappy;
        }
    }

    public static int getMaxHappy1(Employee boss) {
        if (boss == null) {
            return 0;
        }
        Info info = process1(boss);
        return Math.max(info.comeHappy, info.noComeHappy);
    }

    private static Info process1(Employee cur) {
        if (cur == null) {
            return new Info(0, 0);
        }
        int noComeHappy = 0;
        int comeHappy = cur.happy;
        for (Employee sub : cur.subordinates) {
            Info subInfo = process1(sub);
            noComeHappy += Math.max(subInfo.comeHappy, subInfo.noComeHappy);
            comeHappy += subInfo.noComeHappy;
        }
        return new Info(noComeHappy, comeHappy);
    }

    public static int getMaxHappy2(Employee boss) {
        if (boss == null) {
            return 0;
        }
        int p1 = process2(boss, false);
        int p2 = process2(boss, true);
        return Math.max(p1, p2);
    }

    private static int process2(Employee cur, boolean up) {
        List<Employee> subs = cur.subordinates;
        if (up) {
            // 上级来了，下级不可来
            int h1 = 0;
            for (Employee sub : subs) {
                h1 += process2(sub, false);
            }
            return h1;
        } else {
            // 上级不来，当前下级可来，可不来
            int h2 = cur.happy;
            int h3 = 0;
            for (Employee sub : subs) {
                h2 += process2(sub, true);
                h3 += process2(sub, false);
            }
            return Math.max(h2, h3);
        }
    }

    public static void main(String[] args) {
        int maxLevel = 10;
        int maxSubs = 6;
        int maxHappy = 1000;
        int times = 500000;
        for (int i = 0; i < times; i++) {
            Employee boss = buildTree(maxLevel, maxSubs, maxHappy);
            int m1 = getMaxHappy1(boss);
            int m2 = getMaxHappy2(boss);
            if (m1 != m2) {
                System.out.println("完犊子了！");
                break;
            }
        }
        System.out.println("success!!!");
    }

    private static Employee buildTree(int maxLevel, int maxSubs, int maxHappy) {
        maxLevel = (int) (Math.random() * maxLevel);
        if (maxLevel == 0) {
            return null;
        }
        return generateEmployee(1, maxLevel, maxSubs, maxHappy);
    }

    private static Employee generateEmployee(int curLevel, int maxLevel, int maxSubs, int maxHappy) {
        if (curLevel > maxLevel) {
            return null;
        }
        List<Employee> subs = new ArrayList<>();
        Employee boss = new Employee((int) (Math.random() * maxHappy), subs);
        int subSize = (int) (Math.random() * maxSubs);
        for (int i = 0; i < subSize; i++) {
            Employee subEmployee = generateEmployee(curLevel + 1, maxLevel, maxSubs, maxHappy);
            if (null != subEmployee) {
                subs.add(subEmployee);
            }
        }
        return boss;
    }
}
