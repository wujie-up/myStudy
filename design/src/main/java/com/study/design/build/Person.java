package com.study.design.build;

import java.util.ArrayList;
import java.util.List;

public class Person {
    int age;
    String name;
    String sex;
    int weight;
    List<String> interests;

    public static class PersonBuilder {
        Person person;

        public PersonBuilder() {
            this.person = new Person();
            this.person.interests = new ArrayList<>();
        }

        public PersonBuilder buildAge(int age) {
            this.person.age = age;
            return this;
        }

        public PersonBuilder buildWeight(int weight) {
            this.person.weight = weight;
            return this;
        }

        public PersonBuilder buildSex(String sex) {
            this.person.sex = sex;
            return this;
        }

        public PersonBuilder buildName(String name) {
            this.person.name = name;
            return this;
        }

        public PersonBuilder buildInterests(String interest) {
            this.person.interests.add(interest);
            return this;
        }

        public Person build() {
            return this.person;
        }
    }
}
