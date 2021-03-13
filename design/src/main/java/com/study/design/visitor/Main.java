package com.study.design.visitor;

public class Main {
    public static void main(String[] args) {
        Park park = new DragonThemePark(188);
        Person child = new Children();
        Person old = new OldMan();
        Person other = new OtherPerson();

        child.visit(park);
        old.visit(park);
        other.visit(park);
    }
}

class DragonThemePark implements Park {
    private int price;

    public DragonThemePark(int price) {
        this.price = price;
    }

    // 根据不同的人群，收取不同的票价
    @Override
    public void normalTicket(Person person) {
        System.out.println(person.name() + "票价: " + price + "元");
    }
    @Override
    public void OldManTicket(Person person) {
        System.out.println(person.name() + "票价: " + (price * 0.5) + "元");
    }
    @Override
    public void childrenTicket(Person person) {
        System.out.println(person.name() + "票价: " + (price * 0) + "元");
    }
}
interface Park{
    void normalTicket(Person person);
    void OldManTicket(Person person);
    void childrenTicket(Person person);
}


