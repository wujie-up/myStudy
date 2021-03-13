package com.study.design.visitor;

public class OldMan extends Person{
    @Override
    String name() {
        return "老人";
    }

    @Override
    public void visit(Park park) {
        park.OldManTicket(this);
    }
}
