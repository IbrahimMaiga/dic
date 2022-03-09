package com.dic.example;

import com.dic.example.dep.*;
import com.dic.registry.IoC;
import com.dic.registry.IoCRegistry;

/**
 * @author Ibrahim Maiga <maiga.ibrm@gmail.com>
 */
public interface Main {

    static void main(String... args) throws Exception {
        IoC registry = IoCRegistry.create();
        registry.configure(DipConfiguration.class);
        D d = registry.get(D.class.getName());
        d.doSomething();
    }
}
