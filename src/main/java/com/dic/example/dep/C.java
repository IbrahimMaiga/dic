package com.dic.example.dep;

import static java.lang.String.format;

/**
 * @author Ibrahim Maiga <maiga.ibrm@gmail.com>
 */
public class C {
    private A a;

    public C(A a) {
        this.a = a;
    }

    public void doSomething() {
        System.out.println(format("In depend on: %s", a.getClass().getName()));
    }
}
