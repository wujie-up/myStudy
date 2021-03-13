package com.study.design.visitor;

public abstract class Person {
    abstract String name();
    abstract void visit(Park park);
}
