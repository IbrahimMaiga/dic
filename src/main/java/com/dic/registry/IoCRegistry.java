package com.dic.registry;

import com.dic.annotation.Bean;
import com.dic.annotation.Configuration;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.AbstractMap.SimpleEntry;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

import static java.lang.String.format;
import static java.util.Arrays.stream;
import static java.util.Objects.nonNull;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;

/**
 * @author Ibrahim Maiga <maiga.ibrm@gmail.com>
 */
public class IoCRegistry implements IoC {
    private static final Map<String, Object> registry = new ConcurrentHashMap<>();

    @Override
    public <T> void configure(Class<T> clazz) throws IoCRegistryException, InvocationTargetException, IllegalAccessException, InstantiationException, NoSuchMethodException {
        if (!hasConfigurationAnnotation(clazz)) throwIocRegistryException(clazz.getName());

        List<Method> methods = getDeclaredMethods(clazz);
        T configurationInstance = clazz.getDeclaredConstructor().newInstance();
        Map<Method, List<Method>> dependencyTree = buildDependencyTree(methods);
        buildConfig(configurationInstance, methods, dependencyTree);
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> T get(String className) {
        return ((T) registry.get(className));
    }

    public static IoC create() {
        return new IoCRegistry();
    }

    private Map<Method, List<Method>> buildDependencyTree(List<Method> methods) {
        final List<Method> methodList = new ArrayList<>(methods);
        return methods.stream().map(method -> entry(method, methodDependencies(method, methodList))).collect(toMap(Entry::getKey, Entry::getValue));
    }

    private List<Method> methodDependencies(Method method, List<Method> methodList) {
        return stream(method.getParameterTypes()).map(parameterType -> toMethod(methodList, parameterType.getTypeName())).filter(Optional::isPresent).map(Optional::get).collect(toList());
    }

    private <T> Object invokeMethod(Method method, T configurationInstance) throws InvocationTargetException, IllegalAccessException {
        Class<?>[] parameterTypes = method.getParameterTypes();
        Object[] parameters = getParameters(parameterTypes);
        return method.invoke(configurationInstance, parameters);
    }

    private Object[] getParameters(Class<?>[] parameterTypes) {
        Object[] parameters = new Object[parameterTypes.length];

        for (int i = 0; i < parameters.length; i++) {
            Class<?> parameter = parameterTypes[i];
            String name = parameter.getTypeName();
            parameters[i] = get(name);
        }

        return parameters;
    }

    private Optional<Method> toMethod(final List<Method> methods, final String parameterTypeName) {
        return methods.stream().filter(m -> m.getReturnType().getTypeName().equals(parameterTypeName)).findFirst();
    }

    private <T> void buildConfig(T configurationInstance, List<Method> methods, Map<Method, List<Method>> dependencyTree) throws InvocationTargetException, IllegalAccessException {
        for (Method method : methods) {
            String typeName = method.getReturnType().getTypeName();
            Object o = get(typeName);
            if (o != null) continue;

            List<Method> dependencies = dependencyTree.get(method);
            if (dependencies.size() > 0) {
                buildConfig(configurationInstance, dependencies, dependencyTree);
            }

            Object instance = invokeMethod(method, configurationInstance);
            registry.put(instance.getClass().getName(), instance);
        }
    }

    private <K, V> Entry<K, V> entry(K key, V value) {
        return new SimpleEntry<>(key, value);
    }

    private <T> List<Method> getDeclaredMethods(Class<T> clazz) {
        return stream(clazz.getDeclaredMethods()).filter(this::hasBeanAnnotation).collect(toList());
    }

    private boolean hasBeanAnnotation(Method method) {
        return nonNull(method.getAnnotation(Bean.class));
    }

    private <T> boolean hasConfigurationAnnotation(Class<T> clazz) {
        return nonNull(clazz.getAnnotation(Configuration.class));
    }

    private void throwIocRegistryException(String className) {
        throw new IoCRegistryException(format("class %s is not configurable", className));
    }
}
