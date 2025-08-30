/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2025.  Lorem XiaoMiSum (mi_xiao@qq.com)
 *
 * Permission is hereby granted, free of charge, to any person obtaining
 * a copy of this software and associated documentation files (the
 * 'Software'), to deal in the Software without restriction, including
 * without limitation the rights to use, copy, modify, merge, publish,
 * distribute, sublicense, and/or sell copies of the Software, and to
 * permit persons to whom the Software is furnished to do so, subject to
 * the following conditions:
 *
 * The above copyright notice and this permission notice shall be
 * included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED 'AS IS', WITHOUT WARRANTY OF ANY KIND,
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
 * MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.
 * IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY
 * CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT,
 * TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE
 * SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package io.github.xiaomisum.ryze.support;

import java.util.*;

/**
 * 集合工具类，封装了常用集合操作
 */
@SuppressWarnings({"unchecked", "rawtypes"})
public class Collections {

    public static <K, V> Map<K, V> unmodifiableMap(Map<K, V> m) {
        return java.util.Collections.unmodifiableMap(m);
    }

    public static boolean isUnmodifiableMap(Map map) {
        // 先通过类名快速判断
        String className = map.getClass().getName();
        if (className.contains("UnmodifiableMap") || className.contains("ImmutableMap") ||
                className.startsWith("java.util.ImmutableCollections$")) {
            return true;
        }
        if (map instanceof HashMap || map instanceof TreeMap) {
            return false;
        }
        // 类名未知时，通过行为测试确认
        String testKey = "__unmodifiable_test_key_%s__".formatted(System.currentTimeMillis());
        try {
            map.put(testKey, "");
            map.remove(testKey);
            return false;
        } catch (UnsupportedOperationException e) {
            return true;
        }
    }

    public static <K, V> Map<K, V> putAllIfNonNull(Map<K, V> target, Map<K, V> data) {
        if (Objects.isNull(target)) {
            return data;
        }
        if (Objects.isNull(data)) {
            return target;
        }
        if (isUnmodifiableMap(target)) {
            target = new HashMap<>(target);
        }
        target.putAll(data);
        return target;
    }

    public static <T> List<T> addAllIfNonNull(List<T> target, List<T> data) {
        if (Objects.isNull(target)) {
            return data;
        }
        if (Objects.isNull(data)) {
            return target;
        }
        if (isUnmodifiableList(target)) {
            target = new ArrayList<>(target);
        }

        target.addAll(data);
        return target;
    }

    public static Map of(Object... keywords) {
        if (keywords == null || keywords.length == 0) {
            return new HashMap<>();
        }
        var map = new HashMap<>();
        for (int i = 0; i < keywords.length; i += 2) {
            if (i + 1 < keywords.length) {
                map.put(keywords[i], keywords[i + 1]);
            }
        }
        return map;
    }


    public static <K, V> Map<K, V> newHashMap(Map<K, V> map) {
        return new HashMap<>(map);
    }

    public static <K, V> Map<K, V> newHashMap() {
        return new HashMap<>();
    }

    @SafeVarargs
    public static <T> List<T> newArrayList(T... elements) {
        if (elements != null) {
            return newArrayList(Arrays.asList(elements));
        }
        return new ArrayList<>();
    }


    public static <T> List<T> newArrayList(List<T> elements) {
        return new ArrayList<>(elements);
    }

    public static <T> List<T> emptyList() {
        return java.util.Collections.emptyList();
    }

    public static <T> Iterator<T> emptyIterator() {
        return java.util.Collections.emptyIterator();
    }

    public static <T> List<T> unmodifiableList(List<T> list) {
        return java.util.Collections.unmodifiableList(list);
    }

    public static <T extends Comparable<? super T>> void sort(List<T> list) {
        java.util.Collections.sort(list);
    }


    public static void reverse(List<?> list) {
        java.util.Collections.reverse(list);
    }

    public static boolean isUnmodifiableList(List list) {
        // 先通过类名快速判断
        String className = list.getClass().getName();
        if (className.contains("UnmodifiableList") || className.contains("ImmutableList") ||
                className.startsWith("java.util.ImmutableCollections$")) {
            return true;
        }
        if (list instanceof ArrayList || list instanceof LinkedList) {
            return false;
        }

        // 类名未知时，通过行为测试确认
        try {
            list.addFirst("");
            list.removeFirst();
            return false;
        } catch (UnsupportedOperationException e) {
            return true;
        }
    }

    public static <T> List<T> singletonList(T o) {
        return java.util.Collections.singletonList(o);
    }

    public static <T> Set<T> unmodifiableSet(Set<? extends T> s) {
        return java.util.Collections.unmodifiableSet(s);
    }

    public static <T> Set<T> emptySet() {
        return java.util.Collections.emptySet();
    }
}
