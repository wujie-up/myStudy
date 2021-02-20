package com.study.java.basic;

public class AbstractExampleImpl extends AbstractExample{
    @Override
    public void abMethod() {
        // System.out.println(x); // 'x' has private access in 'com.study.java.basic.AbstractExample'
        System.out.println(y);
        System.out.println(z);

        // pMethod(); // 'pMethod()' has private access in 'com.study.java.basic.AbstractExample'
        ptMethod();
    }
}
