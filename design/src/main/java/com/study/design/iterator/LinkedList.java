package com.study.design.iterator;

public class LinkedList<E> {
    Node<E> head;
    Node<E> tail;
    int size;

    public void add(E e) {
        Node<E> node = new Node<>(e);
        if (head == null) {
            head = tail = node;
        } else {
            tail.next = node;
            tail = node;
        }
        size++;
    }

    static class Node<E> {
        Node next;
        E e;

        public Node(E e) {
            this.e = e;
        }
    }

    public Iterator<E> iterator() {
        return new LinkListIterator();
    }

    private class LinkListIterator implements Iterator<E> {
        Node cur;
        Node tail;

        public LinkListIterator() {
            this.cur =  LinkedList.this.head;
            this.tail =  LinkedList.this.tail;
        }

        @Override
        public boolean hasNext() {
            return cur != null;
        }

        @Override
        public E next() {
            Node node = cur;
            cur = cur.next;
            return (E) node.e;
        }
    }
}
