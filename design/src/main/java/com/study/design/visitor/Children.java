package com.study.design.visitor;

public class Children extends Person {
    @Override
    String name() {
        return "小孩";
    }
    @Override
    public void visit(Park park) {
        park.childrenTicket(this);
    }
}
