package com.study.java.basic;

import java.io.*;
import java.util.ArrayList;

public class ArrayListTest {
    public static void main(String[] args) throws IOException, ClassNotFoundException {
        ArrayList list = new ArrayList();
        list.add("haha");
        ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream("./array"));
        oos.writeObject(list);

        ObjectInputStream is = new ObjectInputStream(new FileInputStream("./array"));
        ArrayList arrayList = (ArrayList) is.readObject();
        System.out.println(arrayList);
    }
}
