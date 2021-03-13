package com.study.design.visitor;

public class OtherPerson extends Person{
    @Override
    String name() {
        return "普通人";
    }
    @Override
    public void visit(Park park) {
        park.normalTicket(this);
    }
}
