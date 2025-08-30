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

import java.util.HashSet;
import java.util.Set;

/**
 * 基本类型检查工具类
 *
 * <p>该类提供了检查对象或类是否为Java基本类型或其包装类型的工具方法。
 * 支持boolean、byte、char、short、int、long、float、double等基本类型
 * 及其对应的包装类型Boolean、Byte、Character、Short、Integer、Long、Float、Double。</p>
 *
 * <p>主要功能：
 * <ul>
 *   <li>检查对象是否为基本类型</li>
 *   <li>检查对象是否为包装类型</li>
 *   <li>检查对象是否为基本类型或其包装类型</li>
 *   <li>检查类是否为基本类型</li>
 *   <li>检查类是否为包装类型</li>
 *   <li>检查类是否为基本类型或其包装类</li>
 * </ul>
 * </p>
 *
 * @author xiaomi
 * @since 2025/8/22
 */
public class PrimitiveTypeChecker {

    /**
     * 所有基本类型的Class对象集合
     * <p>包含boolean.class、byte.class、char.class、short.class、
     * int.class、long.class、float.class、double.class、void.class</p>
     */
    private static final Set<Class<?>> PRIMITIVE_TYPES = new HashSet<>();

    /**
     * 所有包装类型的Class对象集合
     * <p>包含Boolean.class、Byte.class、Character.class、Short.class、
     * Integer.class、Long.class、Float.class、Double.class、Void.class</p>
     */
    private static final Set<Class<?>> WRAPPER_TYPES = new HashSet<>();

    static {
        // 添加所有基本类型
        PRIMITIVE_TYPES.add(boolean.class);
        PRIMITIVE_TYPES.add(byte.class);
        PRIMITIVE_TYPES.add(char.class);
        PRIMITIVE_TYPES.add(short.class);
        PRIMITIVE_TYPES.add(int.class);
        PRIMITIVE_TYPES.add(long.class);
        PRIMITIVE_TYPES.add(float.class);
        PRIMITIVE_TYPES.add(double.class);
        PRIMITIVE_TYPES.add(void.class);
        // String 也认为是基础数据类型
        PRIMITIVE_TYPES.add(String.class);

        // 添加所有包装类型
        WRAPPER_TYPES.add(Boolean.class);
        WRAPPER_TYPES.add(Byte.class);
        WRAPPER_TYPES.add(Character.class);
        WRAPPER_TYPES.add(Short.class);
        WRAPPER_TYPES.add(Integer.class);
        WRAPPER_TYPES.add(Long.class);
        WRAPPER_TYPES.add(Float.class);
        WRAPPER_TYPES.add(Double.class);
        WRAPPER_TYPES.add(Void.class);
        // String 也认为是基础数据类型
        WRAPPER_TYPES.add(String.class);
    }

    /**
     * 判断对象是否为原始基本类型
     *
     * <p>检查给定对象是否为Java的基本数据类型实例，如int、boolean等。
     * 注意：此方法检查的是对象的运行时类型，而非对象本身。</p>
     *
     * @param obj 待检查的对象，可以为null
     * @return 如果对象是基本类型实例返回true，否则返回false
     */
    public static boolean isPrimitiveType(Object obj) {
        return obj != null && PRIMITIVE_TYPES.contains(obj.getClass());
    }

    /**
     * 判断对象是否为包装类型
     *
     * <p>检查给定对象是否为Java基本数据类型的包装类实例，
     * 如Integer、Boolean等。</p>
     *
     * @param obj 待检查的对象，可以为null
     * @return 如果对象是包装类型实例返回true，否则返回false
     */
    public static boolean isWrapperType(Object obj) {
        return obj != null && WRAPPER_TYPES.contains(obj.getClass());
    }

    /**
     * 检查是否为基本类型或其包装类型
     *
     * <p>检查给定对象是否为Java基本数据类型或其包装类型实例。</p>
     *
     * @param obj 待检查的对象，可以为null
     * @return 如果对象是基本类型或包装类型实例返回true，否则返回false
     */
    public static boolean isPrimitiveOrWrapper(Object obj) {
        if (obj == null) return false;
        Class<?> clazz = obj.getClass();
        return PRIMITIVE_TYPES.contains(clazz) || WRAPPER_TYPES.contains(clazz);
    }

    /**
     * 检查类是否为基本类型
     *
     * <p>检查给定的类是否为Java基本数据类型，如int.class、boolean.class等。</p>
     *
     * @param clazz 待检查的类，可以为null
     * @return 如果类是基本类型返回true，否则返回false
     */
    public static boolean isPrimitiveClass(Class<?> clazz) {
        return clazz != null && PRIMITIVE_TYPES.contains(clazz);
    }

    /**
     * 检查类是否为包装类型
     *
     * <p>检查给定的类是否为Java基本数据类型的包装类，如Integer.class、Boolean.class等。</p>
     *
     * @param clazz 待检查的类，可以为null
     * @return 如果类是包装类型返回true，否则返回false
     */
    public static boolean isWrapperClass(Class<?> clazz) {
        return clazz != null && WRAPPER_TYPES.contains(clazz);
    }

    /**
     * 检查类是否为基本类型或其包装类
     *
     * <p>检查给定的类是否为Java基本数据类型或其包装类。</p>
     *
     * @param clazz 待检查的类，可以为null
     * @return 如果类是基本类型或包装类返回true，否则返回false
     */
    public static boolean isPrimitiveOrWrapperClass(Class<?> clazz) {
        return clazz != null &&
                (PRIMITIVE_TYPES.contains(clazz) || WRAPPER_TYPES.contains(clazz));
    }
}