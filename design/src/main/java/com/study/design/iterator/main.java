package com.study.design.iterator;

public class main {
    public static void main(String[] args) {
        ArrayList<String> list = new ArrayList<>(5);
        list.add("0");
        list.add("1");
        list.add("2");
        list.add("3");
        list.add("4");
        list.add("5");
        list.add("6");
        LinkedList<String> linkList = new LinkedList<>();
        linkList.add("0");
        linkList.add("1");
        linkList.add("2");
        linkList.add("3");
        linkList.add("4");
        linkList.add("5");
        linkList.add("6");

        Iterator<String> it = list.iterator();
//        Iterator<String> it = linkList.iterator();
        while (it.hasNext()) {
            System.out.print(it.next() + " ");
        }
    }
}
