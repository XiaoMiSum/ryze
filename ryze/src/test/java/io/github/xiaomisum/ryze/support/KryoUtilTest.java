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

public class KryoUtilTest {

    @Test
    public void testGetKryo() {
        // 测试获取Kryo实例
        Assert.assertNotNull(KryoUtil.getKryo());

        // 验证每次获取的都是同一个实例（在同一线程中）
        com.esotericsoftware.kryo.Kryo kryo1 = KryoUtil.getKryo();
        com.esotericsoftware.kryo.Kryo kryo2 = KryoUtil.getKryo();
        Assert.assertSame(kryo1, kryo2);
    }

    @Test
    public void testCopySimpleObject() {
        // 测试简单对象的深拷贝
        SimplePerson original = new SimplePerson("Alice", 25);
        SimplePerson copied = KryoUtil.copy(original);

        // 验证拷贝对象不为null
        Assert.assertNotNull(copied);

        // 验证拷贝对象与原对象内容相同
        Assert.assertEquals(copied, original);

        // 验证拷贝对象与原对象不是同一个实例
        Assert.assertNotSame(copied, original);

        // 验证修改拷贝对象不影响原对象
        copied.setName("Bob");
        Assert.assertEquals(original.getName(), "Alice");
        Assert.assertEquals(copied.getName(), "Bob");
    }

    @Test
    public void testCopyComplexObject() {
        // 测试复杂对象的深拷贝
        SimplePerson person = new SimplePerson("Alice", 25);
        List<String> hobbies = Collections.newArrayList("reading", "swimming");
        Map<String, Object> attributes = new HashMap<>();
        attributes.put("height", 165);
        attributes.put("weight", 55.5);

        ComplexPerson original = new ComplexPerson(person, hobbies, attributes);
        ComplexPerson copied = KryoUtil.copy(original);

        // 验证拷贝对象不为null
        Assert.assertNotNull(copied);

        // 验证拷贝对象与原对象内容相同
        Assert.assertEquals(copied, original);

        // 验证拷贝对象与原对象不是同一个实例
        Assert.assertNotSame(copied, original);
        Assert.assertNotSame(copied.getPerson(), original.getPerson());
        Assert.assertNotSame(copied.getHobbies(), original.getHobbies());
        Assert.assertNotSame(copied.getAttributes(), original.getAttributes());

        // 验证修改拷贝对象不影响原对象
        copied.getPerson().setName("Bob");
        copied.getHobbies().add("dancing");
        copied.getAttributes().put("hairColor", "black");

        Assert.assertEquals(original.getPerson().getName(), "Alice");
        Assert.assertEquals(copied.getPerson().getName(), "Bob");
        Assert.assertEquals(original.getHobbies().size(), 2);
        Assert.assertEquals(copied.getHobbies().size(), 3);
        Assert.assertNull(original.getAttributes().get("hairColor"));
        Assert.assertEquals(copied.getAttributes().get("hairColor"), "black");
    }

    @Test
    public void testCopyCollections() {
        // 测试集合的深拷贝
        List<SimplePerson> originalList = new ArrayList<>();
        originalList.add(new SimplePerson("Alice", 25));
        originalList.add(new SimplePerson("Bob", 30));

        List<SimplePerson> copiedList = KryoUtil.copy(originalList);

        // 验证拷贝集合不为null
        Assert.assertNotNull(copiedList);

        // 验证拷贝集合与原集合内容相同
        Assert.assertEquals(copiedList, originalList);

        // 验证拷贝集合与原集合不是同一个实例
        Assert.assertNotSame(copiedList, originalList);

        // 验证集合中的元素也被深拷贝
        Assert.assertNotSame(copiedList.get(0), originalList.get(0));
        Assert.assertEquals(copiedList.get(0), originalList.get(0));

        // 验证修改拷贝集合不影响原集合
        copiedList.get(0).setName("Charlie");
        Assert.assertEquals(originalList.get(0).getName(), "Alice");
        Assert.assertEquals(copiedList.get(0).getName(), "Charlie");
    }

    @Test
    public void testCopyNull() {
        // 测试null对象的拷贝
        SimplePerson copied = KryoUtil.copy(null);
        Assert.assertNull(copied);
    }

    @Test
    public void testCopyPrimitiveTypes() {
        // 测试基本类型包装类的拷贝
        Integer originalInt = 42;
        Integer copiedInt = KryoUtil.copy(originalInt);
        Assert.assertEquals(copiedInt, originalInt);
        Assert.assertSame(copiedInt, originalInt); // 包装类是不可变的，应该返回同一实例

        String originalString = "Hello, World!";
        String copiedString = KryoUtil.copy(originalString);
        Assert.assertEquals(copiedString, originalString);
        Assert.assertSame(copiedString, originalString); // String是不可变的，应该返回同一实例
    }

    @Test
    public void testCopyMap() {
        // 测试Map的深拷贝
        Map<String, SimplePerson> originalMap = new HashMap<>();
        originalMap.put("person1", new SimplePerson("Alice", 25));
        originalMap.put("person2", new SimplePerson("Bob", 30));

        Map<String, SimplePerson> copiedMap = KryoUtil.copy(originalMap);

        // 验证拷贝Map不为null
        Assert.assertNotNull(copiedMap);

        // 验证拷贝Map与原Map内容相同
        Assert.assertEquals(copiedMap, originalMap);

        // 验证拷贝Map与原Map不是同一个实例
        Assert.assertNotSame(copiedMap, originalMap);

        // 验证Map中的值也被深拷贝
        Assert.assertNotSame(copiedMap.get("person1"), originalMap.get("person1"));
        Assert.assertEquals(copiedMap.get("person1"), originalMap.get("person1"));

        // 验证修改拷贝Map不影响原Map
        copiedMap.get("person1").setName("Charlie");
        Assert.assertEquals(originalMap.get("person1").getName(), "Alice");
        Assert.assertEquals(copiedMap.get("person1").getName(), "Charlie");
    }

    // 测试用的简单类
    public static class SimplePerson {
        private String name;
        private int age;

        public SimplePerson() {
        }

        public SimplePerson(String name, int age) {
            this.name = name;
            this.age = age;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public int getAge() {
            return age;
        }

        public void setAge(int age) {
            this.age = age;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            SimplePerson that = (SimplePerson) o;
            return age == that.age && Objects.equals(name, that.name);
        }

        @Override
        public int hashCode() {
            return Objects.hash(name, age);
        }
    }

    // 测试用的复杂类（包含嵌套对象和集合）
    public static class ComplexPerson {
        private SimplePerson person;
        private List<String> hobbies;
        private Map<String, Object> attributes;

        public ComplexPerson() {
        }

        public ComplexPerson(SimplePerson person, List<String> hobbies, Map<String, Object> attributes) {
            this.person = person;
            this.hobbies = hobbies;
            this.attributes = attributes;
        }

        public SimplePerson getPerson() {
            return person;
        }

        public void setPerson(SimplePerson person) {
            this.person = person;
        }

        public List<String> getHobbies() {
            return hobbies;
        }

        public void setHobbies(List<String> hobbies) {
            this.hobbies = hobbies;
        }

        public Map<String, Object> getAttributes() {
            return attributes;
        }

        public void setAttributes(Map<String, Object> attributes) {
            this.attributes = attributes;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            ComplexPerson that = (ComplexPerson) o;
            return Objects.equals(person, that.person) &&
                    Objects.equals(hobbies, that.hobbies) &&
                    Objects.equals(attributes, that.attributes);
        }

        @Override
        public int hashCode() {
            return Objects.hash(person, hobbies, attributes);
        }
    }
}