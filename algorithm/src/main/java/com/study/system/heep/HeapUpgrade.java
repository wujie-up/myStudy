package com.study.system.heep;

import java.util.*;

/**
 * 加强小顶堆
 */
public class HeapUpgrade<E> {
    private List<E> heap;
    /**
     * 反向索引（倒排索引）
     */
    private Map<E, Integer> indexMap;
    private Comparator<? super E> comparator;
    private int heapSize;

    public HeapUpgrade(Comparator<? super E> comparator) {
        this.comparator = comparator;
        this.heap = new ArrayList<>();
        this.indexMap = new HashMap<>();
        this.heapSize = 0;
    }

    public int size() {
        return heapSize;
    }

    public boolean isEmpty() {
        return heapSize == 0;
    }

    public boolean contains(E e) {
        return heap.contains(e);
    }

    public List<E> getAllElements() {
        List<E> list = new ArrayList<>(heapSize);
        for (E e : heap) {
            list.add(e);
        }
        return list;
    }

    public void update(int index, E newE) {
        heap.set(index, newE);
        reHeap(index);
    }

    public void remove(E e) {
        Integer index = indexMap.get(e);
        // 拿到最后一个元素
        E replaceE = heap.get(heapSize - 1);
        // 删除索引
        indexMap.remove(e);
        // 将末尾移除，size减小
        heap.remove(--heapSize);
        // 如果删除元素刚好是末尾则不需要以下操作
        if (e != replaceE) {
            heap.set(index, replaceE);
            indexMap.put(replaceE, index);
            reHeap(index);
        }
    }

    /**
     * 重新堆化：从上往下堆化 和 从下往上堆化两种情况，只会满足一种（要不比上面小，要不比下面大）
     *
     * @param index 起始位置
     */
    public void reHeap(int index) {
        // 从上往下堆化
        heapify(index);
        // 从下往上堆化
        heapInsert(index);
    }

    public void reHeap(E e) {
        Integer index = indexMap.get(e);
        reHeap(index);
    }

    public void put(E e) {
        heap.add(e);
        indexMap.put(e, heapSize);
        heapInsert(heapSize++);
    }

    public E pop() {
        E res = heap.get(0);
        // 先交换 再删除
        swap(0, --heapSize);
        // 删除索引
        indexMap.remove(res);
        heap.remove(res);
        // 堆化
        heapify(0);
        return res;
    }

    public E peek() {
        return heap.get(0);
    }

    public E peek(int index) {
        return heap.get(index);
    }


    private void heapify(int index) {
        int left = index * 2 + 1;
        while (left < heapSize) {
            // 算出左右最小节点的索引
            int smaller = left + 1 < heapSize
                    && aLessThanB(left + 1, left) ? left + 1 : left;
            if (aLessThanB(smaller, index)) {
                swap(index, smaller);
                index = smaller;
                left = index * 2 + 1;
            } else {
                break;
            }
        }
    }

    private void heapInsert(int index) {
        while (aLessThanB(index, (index - 1) / 2)) {
            swap(index, (index - 1) / 2);
            index = (index - 1) / 2;
        }
    }

    private boolean aLessThanB(int indexA, int indexB) {
        return comparator.compare(heap.get(indexA), heap.get(indexB)) < 0;
    }

    private void swap(int a, int b) {
        if (a != b) {
            E e1 = heap.get(a);
            E e2 = heap.get(b);
            // 索引交换
            indexMap.put(e1, b);
            indexMap.put(e2, a);
            // 值交换
            heap.set(a, e2);
            heap.set(b, e1);
        }
    }

/*------------------------------------  测试代码  -----------------------------------------------*/
    private static class Inner {
        int value;

        public Inner(int value) {
            this.value = value;
        }
    }

    public static void main(String[] args) {
        int maxLen = 20;
        int maxValue = 3000;
        int times = 888888;
        for (int i = 0; i < times; i++) {
            int[] arr = randomArr(maxLen, maxValue);
            HeapUpgrade<Inner> heap = new HeapUpgrade<>(Comparator.comparingInt(o -> o.value));
            PriorityQueue<Inner> queue = new PriorityQueue<>(Comparator.comparingInt(o -> o.value));
            for (int a : arr) {
                Inner inner = new Inner(a);
                heap.put(inner);
                queue.add(inner);
            }
            if (Math.random() < 0.5) {
                if (!test1(heap, queue)) {
                    System.out.println("oops!!!");
                    break;
                }
            } else {
                if (!teatDeleteUpdate(heap)) {
                    System.out.println("oops!!!");
                    break;
                }
            }
        }
    }

    private static boolean teatDeleteUpdate(HeapUpgrade<Inner> heap) {
        int index = (int) (Math.random() * heap.size());
        if (Math.random() < 0.5) {
            heap.remove(heap.peek(index));
        } else {
            Inner inner = heap.peek(index);
            int value = inner.value;
            inner.value = value + ((int) (Math.random() * value) - (int) (Math.random() * value));
            heap.update(index, inner);
        }
        return heapCheck(heap);
    }

    private static boolean heapCheck(HeapUpgrade<Inner> heap) {
        if (heap.isEmpty()) {
            return true;
        }
        Inner min = heap.pop();
        while (!heap.isEmpty()) {
            if (heap.pop().value < min.value) {
                return false;
            }
        }
        return true;
    }

    private static boolean test1(HeapUpgrade<Inner> heap, PriorityQueue<Inner> queue) {
        if (heap.size() != queue.size()) {
            System.out.println(heap.size() + "  " + queue.size());
            return false;
        }
        int i = 0;

        Inner i1 = heap.pop();
        Inner i2 = queue.poll();
        if (i1 != i2) {
            System.out.println(i1.value + "   " + i2.value);
            return false;
        }

        if (heap.size() != queue.size()) {
            System.out.println(heap.size() + " --  " + queue.size());
            return false;
        }
        return true;
    }

    private static int[] randomArr(int maxLen, int maxValue) {
        int len = (int) (Math.random() * maxLen) + 1;
        int[] arr = new int[len];
        for (int i = 0; i < len; i++) {
            arr[i] = (int) (Math.random() * (maxValue + 1)) - (int) (Math.random() * maxValue);
        }
        return arr;
    }
}
