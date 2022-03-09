package com.dic.example.dep;
/**
 * @author Ibrahim Maiga <maiga.ibrm@gmail.com>
 */
public class A {
    private B b;

    public A(B b) {
        this.b = b;
    }

    public void doSomething() {
        System.out.println(String.format("In depend on: %s", b.getClass().getName()));
    }
}
