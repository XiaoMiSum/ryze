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

import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.*;

public class ComparatorTest {

    @Test
    public void testAreEqualBasicTypes() {
        // 测试字符串相等
        Assert.assertTrue(Comparator.areEqual("test", "test", false));
        Assert.assertTrue(Comparator.areEqual("Test", "test", true));
        Assert.assertFalse(Comparator.areEqual("Test", "test", false));

        // 测试数字相等
        Assert.assertTrue(Comparator.areEqual(10, 10.0, false));
        Assert.assertTrue(Comparator.areEqual("10", 10, false));
        Assert.assertTrue(Comparator.areEqual(10.0, "10", false));

        // 测试布尔值相等
        Assert.assertTrue(Comparator.areEqual(true, "true", false));
        Assert.assertTrue(Comparator.areEqual("false", false, false));
        Assert.assertTrue(Comparator.areEqual(true, true, false));
    }

    @Test
    public void testAreEqualCollections() {
        // 测试列表相等
        List<String> list1 = Arrays.asList("a", "b", "c");
        List<String> list2 = Arrays.asList("a", "b", "c");
        List<String> list3 = Arrays.asList("a", "b");
        Assert.assertTrue(Comparator.areEqual(list1, list2, false));
        Assert.assertFalse(Comparator.areEqual(list1, list3, false));

        // 测试集合相等
        Set<Integer> set1 = new HashSet<>(Arrays.asList(1, 2, 3));
        Set<Integer> set2 = new HashSet<>(Arrays.asList(3, 2, 1));
        Set<Integer> set3 = new HashSet<>(Arrays.asList(1, 2));
        Assert.assertTrue(Comparator.areEqual(set1, set2, false));
        Assert.assertFalse(Comparator.areEqual(set1, set3, false));
    }

    @Test
    public void testAreEqualMaps() {
        // 测试Map相等
        Map<String, Object> map1 = new HashMap<>();
        map1.put("key1", "value1");
        map1.put("key2", 100);

        Map<String, Object> map2 = new HashMap<>();
        map2.put("key1", "value1");
        map2.put("key2", 100);

        Map<String, Object> map3 = new HashMap<>();
        map3.put("key1", "value1");

        Assert.assertTrue(Comparator.areEqual(map1, map2, false));
        Assert.assertFalse(Comparator.areEqual(map1, map3, false));
    }

    @Test
    public void testAreEqualArrays() {
        // 测试数组相等
        int[] array1 = {1, 2, 3};
        int[] array2 = {1, 2, 3};
        int[] array3 = {1, 2};
        Assert.assertTrue(Comparator.areEqual(array1, array2, false));
        Assert.assertFalse(Comparator.areEqual(array1, array3, false));

        // 测试对象数组相等
        String[] strArray1 = {"a", "b", "c"};
        String[] strArray2 = {"a", "b", "c"};
        Assert.assertTrue(Comparator.areEqual(strArray1, strArray2, false));
    }

    @Test
    public void testAreEqualObjects() {
        // 测试对象相等
        Person person1 = new Person("Alice", 25);
        Person person2 = new Person("Alice", 25);
        Person person3 = new Person("Bob", 30);

        Assert.assertTrue(Comparator.areEqual(person1, person2, false));
        Assert.assertFalse(Comparator.areEqual(person1, person3, false));
    }

    @Test
    public void testAreEqualWithCircularReference() {
        // 测试循环引用对象
        Node node1 = new Node("node1");
        Node node2 = new Node("node2");
        node1.setNext(node2);
        node2.setNext(node1);

        Node node3 = new Node("node1");
        Node node4 = new Node("node2");
        node3.setNext(node4);
        node4.setNext(node3);

        Assert.assertTrue(Comparator.areEqual(node1, node3, false));
    }

    @Test
    public void testAreEqualNulls() {
        // 测试null值
        Assert.assertTrue(Comparator.areEqual(null, null, false));
        Assert.assertFalse(Comparator.areEqual(null, "test", false));
        Assert.assertFalse(Comparator.areEqual("test", null, false));
    }

    @Test
    public void testAreEqualWithoutDeepCompare() {
        Person person1 = new Person("Alice", 25);
        Person person2 = new Person("Alice", 25);
        Person person3 = person1;

        // 测试不进行深度比较
        Assert.assertFalse(Comparator.areEqual(person1, person2, false, false));
        // 测试进行深度比较
        Assert.assertTrue(Comparator.areEqual(person1, person2, true, false));
        // 测试对象相等
        Assert.assertTrue(Comparator.areEqual(person1, person3, false, false));
    }

    @Test
    public void testContains() {
        // 测试字符串包含
        Assert.assertTrue(Comparator.contains("hello world", "hello", false));
        Assert.assertTrue(Comparator.contains("hello world", "HELLO", true));
        Assert.assertFalse(Comparator.contains("hello world", "HELLO", false));

        // 测试数字包含
        Assert.assertTrue(Comparator.contains(12345, "234", false));
        Assert.assertFalse(Comparator.contains(12345, "abc", false));

        // 测试布尔值包含
        Assert.assertTrue(Comparator.contains(true, "ru", false));

        // 测试集合包含
        List<String> list = Arrays.asList("apple", "banana", "cherry");
        Assert.assertTrue(Comparator.contains(list, "banana", false));
        Assert.assertFalse(Comparator.contains(list, "grape", false));

        // 测试Map包含
        Map<String, Object> map = new HashMap<>();
        map.put("key1", "value1");
        map.put("key2", 100);
        Assert.assertTrue(Comparator.contains(map, "key1", false));
        Assert.assertTrue(Comparator.contains(map, "value1", false));
        Assert.assertFalse(Comparator.contains(map, "nonexistent", false));
    }

    @Test
    public void testIsEmpty() {
        // 测试null
        Assert.assertTrue(Comparator.isEmpty(null));

        // 测试空字符串
        Assert.assertTrue(Comparator.isEmpty(""));
        Assert.assertTrue(Comparator.isEmpty("   "));
        Assert.assertFalse(Comparator.isEmpty("test"));

        // 测试空集合
        Assert.assertTrue(Comparator.isEmpty(new ArrayList<>()));
        Assert.assertFalse(Comparator.isEmpty(List.of("item")));

        // 测试空数组
        Assert.assertTrue(Comparator.isEmpty(new Object[0]));
        Assert.assertFalse(Comparator.isEmpty(new Object[]{"item"}));

        // 测试空Map
        Assert.assertTrue(Comparator.isEmpty(new HashMap<>()));
        Map<String, String> nonEmptyMap = new HashMap<>();
        nonEmptyMap.put("key", "value");
        Assert.assertFalse(Comparator.isEmpty(nonEmptyMap));

        // 测试其他对象
        Assert.assertFalse(Comparator.isEmpty(new Object()));
        Assert.assertFalse(Comparator.isEmpty(123));
        Assert.assertFalse(Comparator.isEmpty(true));
    }

    // 测试用的自定义类
    static class Person {

        private String name;
        private int age;

        public Person() {
        }

        public Person(String name, int age) {
            this.name = name;
            this.age = age;
        }

        public String getName() {
            return name;
        }

        public int getAge() {
            return age;
        }
    }

    // 测试用的带循环引用的类
    static class Node {
        private final String value;
        private Node next;

        public Node(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }

        public Node getNext() {
            return next;
        }

        public void setNext(Node next) {
            this.next = next;
        }
    }
}