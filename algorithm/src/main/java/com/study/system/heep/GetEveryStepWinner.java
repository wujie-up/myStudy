package com.study.system.heep;

import lombok.Data;

import java.util.*;
import java.util.stream.Collectors;

public class GetEveryStepWinner {

    static class WhoIsWinner {
        Map<Integer, BuyInfo> buyMap;
        HeapUpgrade<BuyInfo> winner;
        HeapUpgrade<BuyInfo> candidate;
        int limit; // 获奖人数限制

        public WhoIsWinner(int limit) {
            this.limit = limit;
            buyMap = new HashMap<>();
            winner =
                    new HeapUpgrade<>((o1, o2) -> o1.num != o2.num ? o1.num - o2.num : o1.time - o2.time);
            candidate =
                    new HeapUpgrade<>((o1, o2) -> o1.num != o2.num ? o2.num - o1.num : o1.time - o2.time);
        }


        public void operate(int id, int time, boolean buyOrReturn) {
            // 1、发生退货，但用户未曾购买过
            if (!buyOrReturn && !buyMap.containsKey(id)) {
                return;
            }
            BuyInfo by;
            // 2、用户是新购买, 先将临时信息放入map
            if (!buyMap.containsKey(id)) {
                buyMap.put(id, new BuyInfo(id, 0, 0));
            }
            // 3、拿到用户信息
            by = buyMap.get(id);
            if (buyOrReturn) {
                by.num++;
            } else {
                by.num--;
            }
            // 4、如果购买数为0，则从map删除
            if (by.num == 0) {
                buyMap.remove(id);
            }
            // 5、根据用户在哪个区 分别操作
            // 5.1 两个区都不在，新加入的。如果得奖区没满，直接得奖区，否则进入候选区
            if (!winner.contains(by) && !candidate.contains(by)) {
                if (winner.size() < limit) {
                    by.time = time;
                    winner.put(by);
                } else {
                    by.time = time;
                    candidate.put(by);
                }
            } else if (winner.contains(by)) { // 5.2 在得奖区中
                if (by.num == 0) {
                    winner.remove(by);
                } else {
                    winner.reHeap(by);
                }
            } else {// 5.3 在候选区中
                if (by.num == 0) {
                    candidate.remove(by);
                } else {
                    candidate.reHeap(by);
                }
            }
            // 6、判断是否需要移动候选区到得奖区
            winnerRebuild(time);
        }

        private void winnerRebuild(int time) {
            // 1、候选区为空的话，则不需要
            if (candidate.isEmpty()) {
                return;
            }
            // 2、得奖区没有满，则直接弹出候选区堆顶，加入得奖区
            if (winner.size() < limit) {
                BuyInfo by = candidate.pop();
                by.time = time;
                winner.put(by);
                return;
            }
            // 3、得奖区满了，需要判断候选区堆顶的购买数 是否大于 得奖区堆顶
            if (candidate.peek().num > winner.peek().num) {
                BuyInfo toW = candidate.pop();
                BuyInfo toC = winner.pop();
                toW.time = time;
                toC.time = time;
                winner.put(toW);
                candidate.put(toC);
            }
        }
    }

    public static List<List<Integer>> topK(int[] arr, boolean[] op, int k) {
        List<List<Integer>> resWinner = new ArrayList<>();
        WhoIsWinner whoIsWinner = new WhoIsWinner(k);
        // 2、遍历事件
        for (int i = 0; i < op.length; i++) {
            int id = arr[i]; // 顾客id
            boolean buyOrReturn = op[i]; // 购买还是退货
            // 处理
            whoIsWinner.operate(id, i, buyOrReturn);
            List<Integer> curWinner = new ArrayList<>();
            for (BuyInfo buyInfo : whoIsWinner.winner.getAllElements()) {
                curWinner.add(buyInfo.id);
            }
            resWinner.add(curWinner);
        }
        return resWinner;
    }

    static class BuyInfo {
        int id;
        int num;
        int time;

        public BuyInfo(int id, int num, int time) {
            this.id = id;
            this.num = num;
            this.time = time;
        }
    }


    /* ----------------------------------------------  测试代码  ----------------------------------------------------- */
    public static void main(String[] args) {
        int maxLen = 20;
        int maxValue = 8;
        int times = 200000;
        int maxK = 10;
        for (int i = 0; i < times; i++) {
            int[] arr = randomArr1(maxLen, maxValue);
            boolean[] op = new boolean[arr.length];
            for (int j = 0; j < op.length; j++) {
                if (Math.random() < 0.5) {
                    op[j] = true;
                } else {
                    op[j] = false;
                }
            }
            int k = (int) (Math.random() * maxK) + 1;
            List<List<Integer>> list1 = topK(arr, op, k);
            List<List<Integer>> list2 = compare(arr, op, k);

            if (!test(list1, list2)) {
                System.out.println("oops !!!");
                break;
            }
        }

    }

    private static int[] randomArr1(int maxLen, int maxValue) {
        int len = (int) (Math.random() * maxLen) + 1;
        int[] arr = new int[len];
        for (int i = 0; i < len; i++) {
            arr[i] = (int) (Math.random() * maxValue) + 1;
        }
        return new int[0];
    }

    private static boolean test(List<List<Integer>> list1, List<List<Integer>> list2) {
        if (list1.size() != list2.size()) {
            return false;
        }
        for (int i = 0; i < list1.size(); i++) {
            List<Integer> inList1 = list1.get(i);
            List<Integer> inList2 = list2.get(i);
            if (inList1.size() != inList2.size()) {
                return false;
            }
            Collections.sort(inList1);
            Collections.sort(inList2);
            for (int j = 0; j < inList1.size(); j++) {
                if (!inList1.get(j).equals(inList2.get(j))) {
                    return false;
                }
            }
        }
        return true;
    }

    // 简单思路实现的比较方法
    private static List<List<Integer>> compare (int[] arr, boolean[] op, int k) {
        HashMap<Integer, BuyInfo> map = new HashMap<>();
        List<BuyInfo> cands = new ArrayList<>();
        List<BuyInfo> daddy = new ArrayList<>();
        List<List<Integer>> ans = new ArrayList<>();

        for (int i = 0; i < arr.length; i++) {
            int id = arr[i];
            boolean buyOrReturn = op[i];
            // 发生退货，用户未购买
            if (!buyOrReturn && !map.containsKey(id)) {
                ans.add(getWinner(daddy));
                continue;
            }
            // 如果是新加入用户
            if (!map.containsKey(id)) {
                map.put(id, new BuyInfo(id, 0, 0));
            }
            BuyInfo buyInfo = map.get(id);

            if (buyOrReturn) {
                buyInfo.num++;
            } else {
                buyInfo.num--;
            }

            if (buyInfo.num == 0) {
                map.remove(id);
            }

            if (!daddy.contains(buyInfo) && !cands.contains(buyInfo)) {
                if (daddy.size() < k) {
                    buyInfo.time = i;
                    daddy.add(buyInfo);
                } else {
                    buyInfo.time = i;
                    cands.add(buyInfo);
                }
            }
            // 删除winner中所有0
            daddy = daddy.stream().filter(a -> a.num != 0).collect(Collectors.toList());
            // 删除can中所有0
            cands = cands.stream().filter(a -> a.num != 0).collect(Collectors.toList());

            Collections.sort(daddy, (o1, o2) -> o1.num != o2.num ? o1.num - o2.num : o1.time - o2.time);
            Collections.sort(cands, (o1, o2) -> o1.num != o2.num ? o2.num - o1.num : o1.time - o2.time);

            winnerRebuild(i, daddy, cands, k);

            ans.add(getWinner(daddy));
        }
        return ans;
    }

    private static void winnerRebuild(int time, List<BuyInfo> daddy, List<BuyInfo> cands, int k) {
        // 1、候选区为空的话，则不需要
        if (cands.isEmpty()) {
            return;
        }
        // 2、得奖区没有满，则直接弹出候选区堆顶，加入得奖区
        if (daddy.size() < k) {
            BuyInfo by = cands.get(0);
            by.time = time;
            daddy.add(by);
            cands.remove(0);
            return;
        }
        // 3、得奖区满了，需要判断候选区堆顶的购买数 是否大于 得奖区堆顶
        if (cands.get(0).num > daddy.get(0).num) {
            BuyInfo toW = cands.get(0);
            cands.remove(0);
            BuyInfo toC = daddy.get(0);
            daddy.remove(0);
            toW.time = time;
            toC.time = time;
            daddy.add(toW);
            cands.add(toC);
        }
    }

    private static List<Integer> getWinner(List<BuyInfo> daddy) {
        ArrayList list = new ArrayList();
        for (BuyInfo buyInfo : daddy) {
            list.add(buyInfo.id);
        }
        return list;
    }
}
