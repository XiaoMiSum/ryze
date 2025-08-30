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

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.util.*;

/**
 * 对象比较工具类
 *
 * <p>该类提供了丰富的对象比较功能，支持各种数据类型的深度比较、包含关系判断和空值检查。
 * 支持基本类型、字符串、集合、数组、Map、自定义对象等多种数据类型的比较。</p>
 *
 * <p>主要功能：
 * <ul>
 *   <li>深度相等比较：支持对象及其属性的递归比较</li>
 *   <li>包含关系判断：检查一个对象是否包含另一个对象</li>
 *   <li>空值检查：判断对象是否为空</li>
 *   <li>类型兼容比较：支持不同类型间的比较，如String与Number</li>
 * </ul>
 * </p>
 *
 * @author xiaomi
 * @since 2025/8/22
 */
public class Comparator {

    /**
     * 存储已访问对象的标识防止循环引用
     * <p>使用ThreadLocal确保线程安全，避免在比较包含循环引用的对象时出现无限递归</p>
     */
    private static final ThreadLocal<Set<String>> visitedObjects = ThreadLocal.withInitial(HashSet::new);

    /**
     * 主比较方法
     *
     * <p>执行两个对象的相等比较，默认进行深度比较，区分大小写。</p>
     *
     * @param a          第一个对象
     * @param b          第二个对象
     * @param ignoreCase 是否忽略大小写，true表示忽略大小写，false表示区分大小写
     * @return 如果两个对象相等返回true，否则返回false
     * @see #areEqual(Object, Object, boolean, boolean)
     */
    public static boolean areEqual(Object a, Object b, boolean ignoreCase) {
        return areEqual(a, b, true, ignoreCase);
    }

    /**
     * 带深度控制的重载方法
     *
     * <p>执行两个对象的相等比较，可指定是否进行深度比较和是否忽略大小写。</p>
     *
     * @param a           第一个对象
     * @param b           第二个对象
     * @param deepCompare 是否进行深度比较，true表示递归比较对象的所有属性，false表示只比较对象本身
     * @param ignoreCase  是否忽略大小写，true表示忽略大小写，false表示区分大小写
     * @return 如果两个对象相等返回true，否则返回false
     * @see #deepAreEqual(Object, Object, boolean, boolean, Set)
     */
    public static boolean areEqual(Object a, Object b, boolean deepCompare, boolean ignoreCase) {
        visitedObjects.get().clear(); // 清除之前的访问记录
        return deepAreEqual(a, b, deepCompare, ignoreCase, new HashSet<>());
    }

    /**
     * 深度比较方法
     *
     * <p>执行两个对象的深度相等比较，支持循环引用检测。比较过程如下：
     * <ol>
     *   <li>基本处理：null检查、相同对象检查</li>
     *   <li>防止循环引用：通过visited集合记录已比较的对象对</li>
     *   <li>基本类型兼容比较：字符串、布尔值、数字等</li>
     *   <li>数字类型兼容比较</li>
     *   <li>集合类型处理</li>
     *   <li>数组类型处理</li>
     *   <li>Map类型处理</li>
     *   <li>枚举类型处理</li>
     *   <li>对象深度比较</li>
     *   <li>默认比较：使用对象的equals方法</li>
     * </ol>
     * </p>
     *
     * @param a           第一个对象
     * @param b           第二个对象
     * @param deepCompare 是否进行深度比较
     * @param ignoreCase  是否忽略大小写
     * @param visited     已访问对象集合，用于防止循环引用
     * @return 如果两个对象相等返回true，否则返回false
     */
    // 深度比较方法
    private static boolean deepAreEqual(Object a, Object b, boolean deepCompare, boolean ignoreCase, Set<String> visited) {
        // 基本处理：null检查、相同对象检查
        if (a == b) {
            return true;
        }
        if (a == null || b == null) {
            return false;
        }

        // 防止循环引用
        var aId = System.identityHashCode(a);
        var bId = System.identityHashCode(b);
        var key = aId + "-" + bId;
        if (visited.contains(key)) {
            return true;
        }
        visited.add(key);

        // 1. 基本类型兼容比较
        if (PrimitiveTypeChecker.isPrimitiveOrWrapper(a) && PrimitiveTypeChecker.isPrimitiveOrWrapper(b)) {
            return handleBasicTypes(a, b, ignoreCase);
        }

        // 2. 数字类型兼容比较
        if (a instanceof Number && b instanceof Number) {
            return compareNumbers((Number) a, (Number) b);
        }

        // 3. 集合类型处理
        if (a instanceof Collection && b instanceof Collection) {
            return compareCollections((Collection<?>) a, (Collection<?>) b, deepCompare, ignoreCase, visited);
        }

        // 4. 数组类型处理
        if (a.getClass().isArray() && b.getClass().isArray()) {
            return compareArrays(a, b, deepCompare, ignoreCase, visited);
        }

        // 5. Map类型处理
        if (a instanceof Map && b instanceof Map) {
            return compareMaps((Map<?, ?>) a, (Map<?, ?>) b, deepCompare, ignoreCase, visited);
        }

        // 6. 枚举类型处理
        if (a instanceof Enum && b instanceof Enum) {
            return a.equals(b);
        }

        // 7. 对象深度比较
        if (deepCompare) {
            return compareObjects(a, b, ignoreCase, visited);
        }

        // 8. 默认：使用对象的equals方法
        return a.equals(b);
    }

    /**
     * 处理基本类型兼容比较
     *
     * <p>处理字符串、布尔值和数字类型的兼容比较：
     * <ul>
     *   <li>字符串比较：支持忽略大小写</li>
     *   <li>布尔值兼容比较：支持字符串与布尔值的比较</li>
     *   <li>数字兼容比较：支持字符串与数字的比较</li>
     * </ul>
     * </p>
     *
     * @param a          第一个对象
     * @param b          第二个对象
     * @param ignoreCase 是否忽略大小写
     * @return 如果两个对象在基本类型意义上相等返回true，否则返回false
     */
    private static boolean handleBasicTypes(Object a, Object b, boolean ignoreCase) {
        // 字符串比较
        if (a instanceof String || b instanceof String) {
            var strA = a.toString();
            var strB = b.toString();
            return (ignoreCase ? strA.equalsIgnoreCase(strB) : strA.equals(strB)) || areNumericEqual(strA, strB);
        }
        // 布尔值兼容比较
        if (a instanceof Boolean || b instanceof Boolean) {
            return Boolean.parseBoolean(a.toString()) == Boolean.parseBoolean(b.toString());
        }
        // 数字兼容比较
        if (a instanceof Number || b instanceof Number) {
            return compareNumbers(Double.parseDouble(a.toString()), Double.parseDouble(b.toString()));
        }
        return false;
    }

    /**
     * 比较两个数字是否相等
     *
     * <p>根据数字类型进行相应的比较：
     * <ul>
     *   <li>整数类型：使用long值进行比较</li>
     *   <li>浮点类型：使用double值进行比较，误差范围1e-10</li>
     * </ul>
     * </p>
     *
     * @param numA 第一个数字
     * @param numB 第二个数字
     * @return 如果两个数字相等返回true，否则返回false
     */
    private static boolean compareNumbers(Number numA, Number numB) {
        if (isIntegerType(numA) && isIntegerType(numB)) {
            return numA.longValue() == numB.longValue();
        }
        return Math.abs(numA.doubleValue() - numB.doubleValue()) < 1e-10;
    }

    /**
     * 判断数字是否为整数类型
     *
     * <p>检查给定的数字是否为整数类型，包括Integer、Long、Short、Byte。</p>
     *
     * @param num 待检查的数字
     * @return 如果数字是整数类型返回true，否则返回false
     */
    private static boolean isIntegerType(Number num) {
        return num instanceof Integer || num instanceof Long || num instanceof Short || num instanceof Byte;
    }

    /**
     * 比较两个集合是否相等
     *
     * <p>逐个比较集合中的元素，支持深度比较和忽略大小写选项。</p>
     *
     * @param colA        第一个集合
     * @param colB        第二个集合
     * @param deepCompare 是否进行深度比较
     * @param ignoreCase  是否忽略大小写
     * @param visited     已访问对象集合，用于防止循环引用
     * @return 如果两个集合相等返回true，否则返回false
     */
    private static boolean compareCollections(Collection<?> colA, Collection<?> colB, boolean deepCompare,
                                              boolean ignoreCase, Set<String> visited) {
        if (colA.size() != colB.size()) {
            return false;
        }
        var itA = colA.iterator();
        var itB = colB.iterator();
        while (itA.hasNext() && itB.hasNext()) {
            if (!deepAreEqual(itA.next(), itB.next(), deepCompare, ignoreCase, visited)) {
                return false;
            }
        }
        return true;
    }

    /**
     * 比较两个数组是否相等
     *
     * <p>逐个比较数组中的元素，支持深度比较和忽略大小写选项。</p>
     *
     * @param arrA        第一个数组
     * @param arrB        第二个数组
     * @param deepCompare 是否进行深度比较
     * @param ignoreCase  是否忽略大小写
     * @param visited     已访问对象集合，用于防止循环引用
     * @return 如果两个数组相等返回true，否则返回false
     */
    private static boolean compareArrays(Object arrA, Object arrB, boolean deepCompare, boolean ignoreCase, Set<String> visited) {
        var lenA = Array.getLength(arrA);
        var lenB = Array.getLength(arrB);
        if (lenA != lenB) {
            return false;
        }
        for (int i = 0; i < lenA; i++) {
            var elemA = Array.get(arrA, i);
            var elemB = Array.get(arrB, i);
            if (!deepAreEqual(elemA, elemB, deepCompare, ignoreCase, visited)) {
                return false;
            }
        }
        return true;
    }

    /**
     * 比较两个Map是否相等
     *
     * <p>逐个比较Map中的键值对，支持深度比较和忽略大小写选项。</p>
     *
     * @param mapA        第一个Map
     * @param mapB        第二个Map
     * @param deepCompare 是否进行深度比较
     * @param ignoreCase  是否忽略大小写
     * @param visited     已访问对象集合，用于防止循环引用
     * @return 如果两个Map相等返回true，否则返回false
     */
    private static boolean compareMaps(Map<?, ?> mapA, Map<?, ?> mapB, boolean deepCompare, boolean ignoreCase, Set<String> visited) {
        if (mapA.size() != mapB.size()) {
            return false;
        }
        for (var entry : mapA.entrySet()) {
            var key = entry.getKey();
            var valueA = entry.getValue();
            // 如果mapB中没有相同的key，检查失败
            if (!mapB.containsKey(key)) {
                return false;
            }
            var valueB = mapB.get(key);
            // 比较键值
            if (!deepAreEqual(valueA, valueB, deepCompare, ignoreCase, visited)) {
                return false;
            }
        }
        return true;
    }

    /**
     * 对象深度比较方法
     *
     * <p>比较两个对象的所有字段，支持继承关系和访问权限控制。</p>
     *
     * @param objA       第一个对象
     * @param objB       第二个对象
     * @param ignoreCase 是否忽略大小写
     * @param visited    已访问对象集合，用于防止循环引用
     * @return 如果两个对象相等返回true，否则返回false
     */
    // 对象深度比较方法
    private static boolean compareObjects(Object objA, Object objB, boolean ignoreCase, Set<String> visited) {
        // 类型检查
        if (!objA.getClass().equals(objB.getClass())) {
            return false;
        }

        // 获取所有字段（包括父类字段）
        var fields = getAllFields(objA.getClass());
        for (var field : fields) {
            try {
                field.setAccessible(true);
                Object valueA = field.get(objA);
                Object valueB = field.get(objB);

                // 递归比较字段值
                if (!deepAreEqual(valueA, valueB, true, ignoreCase, visited)) {
                    return false;
                }
            } catch (IllegalAccessException e) {
                // 忽略无法访问的字段
            }
        }

        return true;
    }

    /**
     * 获取类及其父类的所有字段
     *
     * <p>通过反射获取指定类及其所有父类声明的字段。</p>
     *
     * @param clazz 类对象
     * @return 包含所有字段的列表
     */
    // 获取类及其父类的所有字段
    private static List<Field> getAllFields(Class<?> clazz) {
        var fields = new ArrayList<Field>();
        while (clazz != null) {
            fields.addAll(Arrays.asList(clazz.getDeclaredFields()));
            clazz = clazz.getSuperclass();
        }
        return fields;
    }

    /**
     * 检查两个字符串数字是否相等
     *
     * <p>尝试将字符串解析为数字并比较它们是否相等，误差范围1e-10。</p>
     *
     * @param s1 第一个字符串
     * @param s2 第二个字符串
     * @return 如果两个字符串表示的数字相等返回true，否则返回false
     */
    // 其他辅助方法保持不变...
    private static boolean areNumericEqual(String s1, String s2) {
        try {
            var d1 = Double.parseDouble(s1);
            var d2 = Double.parseDouble(s2);
            return Math.abs(d1 - d2) < 1e-10;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    /**
     * 检查实际值是否包含期望值
     *
     * <p>根据不同的数据类型执行包含关系判断：
     * <ul>
     *   <li>String：检查是否包含子串</li>
     *   <li>Number：检查数字字符串是否包含子串</li>
     *   <li>Boolean：检查布尔字符串是否包含子串</li>
     *   <li>Collection：检查集合中是否有元素包含指定值</li>
     *   <li>Map：检查键或值是否包含指定值</li>
     * </ul>
     * </p>
     *
     * @param actualValue   实际值
     * @param expectedValue 期望值
     * @param ignoreCase    是否忽略大小写
     * @return 如果实际值包含期望值返回true，否则返回false
     */
    public static boolean contains(Object actualValue, Object expectedValue, boolean ignoreCase) {
        return switch (actualValue) {
            case String string -> !ignoreCase ? string.contains(expectedValue.toString())
                    : string.toLowerCase().contains(expectedValue.toString().toLowerCase());
            case Number number -> number.toString().contains(expectedValue.toString());
            case Boolean booleanActualValue -> booleanActualValue.toString().contains(expectedValue.toString());
            case Collection<?> collection -> collection.stream().anyMatch(e -> contains(e, expectedValue, ignoreCase));
            case Map<?, ?> map -> map.keySet().stream().anyMatch(k -> areEqual(k, expectedValue, ignoreCase)) ||
                    map.values().stream().anyMatch(v -> contains(v, expectedValue, ignoreCase));
            case null, default -> false;
        };
    }

    /**
     * 检查对象是否为空
     *
     * <p>根据不同的数据类型判断是否为空：
     * <ul>
     *   <li>null：返回true</li>
     *   <li>String：检查是否为null或空字符串（去除首尾空白后）</li>
     *   <li>CharSequence：检查是否为空</li>
     *   <li>Iterable：检查迭代器是否有下一个元素</li>
     *   <li>Object[]：检查数组长度是否为0</li>
     *   <li>Map：检查是否为空</li>
     *   <li>其他：返回false</li>
     * </ul>
     * </p>
     *
     * @param actualValue 待检查的对象
     * @return 如果对象为空返回true，否则返回false
     */
    public static boolean isEmpty(Object actualValue) {
        return switch (actualValue) {
            case null -> true;
            case String string -> string.trim().isEmpty();
            case CharSequence charSequence -> charSequence.isEmpty();
            case Iterable<?> iterable -> !iterable.iterator().hasNext();
            case Object[] array -> array.length == 0;
            case Map<?, ?> map -> map.isEmpty();
            default -> false;
        };
    }
}