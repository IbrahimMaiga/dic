package com.dic.example;

import com.dic.annotation.Bean;
import com.dic.annotation.Configuration;
import com.dic.example.dep.*;

/**
 * @author Ibrahim Maiga <maiga.ibrm@gmail.com>
 */
@Configuration
public class DipConfiguration {

    @Bean
    public B bBean() {
        return new B();
    }

    @Bean
    public E eBean() {
        return new E();
    }

    @Bean
    public A aBean(B b) {
        return new A(b);
    }

    @Bean
    public C cBean(A a) {
        return new C(a);
    }

    @Bean
    public D dBean(C c, E e) {
        return new D(c, e);
    }
}
