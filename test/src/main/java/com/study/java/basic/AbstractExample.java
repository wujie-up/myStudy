package com.study.java.basic;

public abstract class AbstractExample {
    private int x;
    protected int y;
    public int z;

    public abstract void abMethod();

    private void pMethod(){
        System.out.println("pMethod");
    };

    protected void ptMethod(){
        System.out.println("ptMethod");
    };
}
