package io.github.xiaomisum.ryze.support;

import io.github.xiaomisum.ryze.testelement.KW;

import java.util.*;

public class RyzeServiceLoader {


    /**
     * 通过 SPI 查找并返回指定接口或抽象类的关键字字典
     *
     * @param clazz 目标类型
     * @param <T>   目标类型
     * @return 指定接口或抽象类的关键字字典
     */
    @SuppressWarnings("unchecked")
    public static <T> Map<String, Class<? extends T>> loadAsMapBySPI(Class<T> clazz) {
        // SPI 查找并注册
        var keyMap = new HashMap<String, Class<? extends T>>();
        var serviceLoader = ServiceLoader.load(clazz);
        serviceLoader.iterator().forEachRemaining(t -> {
            var implClazz = (Class<? extends T>) t.getClass();
            var keys = getKeyWord(implClazz);
            keys.stream().distinct().forEach(key -> keyMap.put(key, implClazz));
        });
        return Collections.unmodifiableMap(keyMap);
    }

    @SuppressWarnings("unchecked")
    public static <T> List<Class<? extends T>> loadAsListBySPI(Class<T> clazz) {
        // SPI 查找并注册
        var collections = new ArrayList<Class<? extends T>>();
        var serviceLoader = ServiceLoader.load(clazz);
        serviceLoader.iterator().forEachRemaining(t -> {
            collections.add((Class<T>) t.getClass());
        });
        return Collections.unmodifiableList(collections);
    }

    /**
     * 通过 SPI 查找并返回指定接口或抽象类的关键字字典
     *
     * @param clazz 目标类型
     * @return 指定接口或抽象类的关键字字典
     */
    public static <T> Map<String, T> loadAsInstanceMapBySPI(Class<T> clazz) {
        // SPI 查找并注册
        var keyMap = new HashMap<String, T>();
        var serviceLoader = ServiceLoader.load(clazz);
        serviceLoader.iterator().forEachRemaining(t -> {
            var keys = getKeyWord(t.getClass());
            keys.stream().distinct().forEach(key -> keyMap.put(key, t));
        });
        return Collections.unmodifiableMap(keyMap);
    }

    public static <T> List<T> loadAsInstanceListBySPI(Class<T> clazz) {
        // SPI 查找并注册
        var serviceLoader = ServiceLoader.load(clazz);
        var list = new ArrayList<T>();
        serviceLoader.iterator().forEachRemaining(list::add);
        return list;

    }

    // 获取目标类的 @KeyWord 注解并返回它的值
    private static List<String> getKeyWord(Class<?> clazz) {
        var annotation = clazz.getAnnotation(KW.class);
        var keys = new ArrayList<String>();
        keys.add(clazz.getSimpleName().toLowerCase());
        if (annotation != null) {
            Arrays.stream(annotation.value()).distinct().forEach(key -> keys.add(key.toLowerCase()));
        }
        return keys;
    }
}
