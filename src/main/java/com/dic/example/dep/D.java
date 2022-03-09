package com.dic.example.dep;

/**
 * @author Ibrahim Maiga <maiga.ibrm@gmail.com>
 */
public class D {
    private C c;
    private E e;

    public D(C c, E e) {
        this.c = c;
        this.e = e;
    }

    public void doSomething() {
        System.out.println(String.format("I depend on %s and %s", c.getClass().getName(), e.getClass().getName()));
    }
}
