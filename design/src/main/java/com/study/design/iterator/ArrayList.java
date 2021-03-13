package com.study.design.iterator;

import javax.swing.plaf.PanelUI;

public class ArrayList<E> {
    Object[] arr;
    int index = 0;
    int size;

    public ArrayList(int size) {
        this.arr =  new Object[size];
        this.size = size;
    }

    public void add(E e) {
        if (index == size) {
            Object[] newArr = new Object[size * 2];
            System.arraycopy(arr, 0, newArr, 0, size);
            this.arr = newArr;
        }
        arr[index] = e;
        index++;
    }

    public Iterator<E> iterator() {
        return new ListIterator(this.size);
    }

    private class ListIterator implements Iterator<E> {
        int curIndex; //当前遍历到的index
        int size;

        public ListIterator(int size) {
            this.size = size;
        }

        @Override
        public boolean hasNext() {
            return curIndex != index;
        }

        @Override
        public E next() {
            Object[] arr = ArrayList.this.arr;
            return (E) arr[curIndex++];
        }
    }
}
