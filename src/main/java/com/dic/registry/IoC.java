package com.dic.registry;

import java.lang.reflect.InvocationTargetException;

/**
 * @author Ibrahim Maiga <maiga.ibrm@gmail.com>
 */
public interface IoC {
    <T> void configure(Class<T> clazz) throws IoCRegistryException, InvocationTargetException, IllegalAccessException, InstantiationException;

    <T> T get(String className);
}
