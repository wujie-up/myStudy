package com.study.design.build;

public class Main {
    public static void main(String[] args) {
        Person person = new Person.PersonBuilder()
                .buildSex("woman")
                .buildAge(18)
                .buildName("Jelly")
                .buildInterests("football")
                .build();
    }
}
