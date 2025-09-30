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

@SuppressWarnings("all")
public class CollectionsTest {

    @Test(expectedExceptions = UnsupportedOperationException.class)
    public void testUnmodifiableMap() {
        Map<String, String> originalMap = new HashMap<>();
        originalMap.put("key1", "value1");
        originalMap.put("key2", "value2");

        Map<String, String> unmodifiableMap = Collections.unmodifiableMap(originalMap);

        // 验证内容相同
        Assert.assertEquals(unmodifiableMap, originalMap);

        // 验证修改会抛出异常
        unmodifiableMap.put("key3", "value3");
    }

    @Test
    public void testIsUnmodifiableMap() {
        Map<String, String> hashMap = new HashMap<>();
        hashMap.put("key", "value");

        Map<String, String> unmodifiableMap = Collections.unmodifiableMap(hashMap);

        // 可修改的map
        Assert.assertFalse(Collections.isUnmodifiableMap(hashMap));

        // 不可修改的map
        Assert.assertTrue(Collections.isUnmodifiableMap(unmodifiableMap));
    }

    @Test
    public void testPutAllIfNonNull() {
        Map<String, String> targetMap = new HashMap<>();
        targetMap.put("target1", "value1");

        Map<String, String> dataMap = new HashMap<>();
        dataMap.put("data1", "value2");
        dataMap.put("data2", "value3");

        // 正常合并
        Map<String, String> result = Collections.putAllIfNonNull(targetMap, dataMap);
        Assert.assertEquals(result.size(), 3);
        Assert.assertEquals(result.get("target1"), "value1");
        Assert.assertEquals(result.get("data1"), "value2");
        Assert.assertEquals(result.get("data2"), "value3");

        // target为null
        result = Collections.putAllIfNonNull(null, dataMap);
        Assert.assertEquals(result, dataMap);

        // data为null
        Map<String, String> resultMap = Collections.putAllIfNonNull(targetMap, null);
        Assert.assertEquals(resultMap, targetMap);
    }

    @Test
    public void testAddAllIfNonNull() {
        List<String> targetList = new ArrayList<>();
        targetList.add("target1");

        List<String> dataList = new ArrayList<>();
        dataList.add("data1");
        dataList.add("data2");

        // 正常合并
        List<String> result = Collections.addAllIfNonNull(targetList, dataList);
        Assert.assertEquals(result.size(), 3);
        Assert.assertEquals(result.get(0), "target1");
        Assert.assertEquals(result.get(1), "data1");
        Assert.assertEquals(result.get(2), "data2");

        // target为null
        result = Collections.addAllIfNonNull(null, dataList);
        Assert.assertEquals(result, dataList);

        // data为null
        List<String> resultList = Collections.addAllIfNonNull(targetList, null);
        Assert.assertEquals(resultList, targetList);
    }

    @Test
    public void testOf() {
        // 空参数
        Map emptyMap = Collections.of();
        Assert.assertNotNull(emptyMap);
        Assert.assertTrue(emptyMap.isEmpty());

        // 偶数个参数
        Map map = Collections.of("key1", "value1", "key2", "value2");
        Assert.assertEquals(map.size(), 2);
        Assert.assertEquals(map.get("key1"), "value1");
        Assert.assertEquals(map.get("key2"), "value2");

        // 奇数个参数
        Map oddMap = Collections.of("key1", "value1", "key2");
        Assert.assertEquals(oddMap.size(), 1);
        Assert.assertEquals(oddMap.get("key1"), "value1");
    }

    @Test
    public void testNewHashMap() {
        // 测试从现有map创建
        Map<String, String> originalMap = new HashMap<>();
        originalMap.put("key1", "value1");
        originalMap.put("key2", "value2");

        Map<String, String> newMap = Collections.newHashMap(originalMap);
        Assert.assertEquals(newMap, originalMap);
        Assert.assertNotSame(newMap, originalMap); // 确保是新的实例

        // 测试创建空map
        Map<String, String> emptyMap = Collections.newHashMap();
        Assert.assertNotNull(emptyMap);
        Assert.assertTrue(emptyMap.isEmpty());
    }


    @Test(expectedExceptions = UnsupportedOperationException.class)
    public void testEmptyMap() {
        Map<String, String> emptyMap = Collections.emptyMap();
        Assert.assertNotNull(emptyMap);
        Assert.assertTrue(emptyMap.isEmpty());

        // 验证是不可修改的
        emptyMap.put("key", "value");
    }

    @Test
    public void testNewArrayList() {
        // 测试从数组创建
        List<String> list = Collections.newArrayList("item1", "item2", "item3");
        Assert.assertEquals(list.size(), 3);
        Assert.assertEquals(list.get(0), "item1");
        Assert.assertEquals(list.get(1), "item2");
        Assert.assertEquals(list.get(2), "item3");

        // 测试从null数组创建
        List<String> nullList = Collections.newArrayList((String[]) null);
        Assert.assertNotNull(nullList);
        Assert.assertTrue(nullList.isEmpty());

        // 测试从现有list创建
        List<String> originalList = Arrays.asList("item1", "item2");
        List<String> newList = Collections.newArrayList(originalList);
        Assert.assertEquals(newList, originalList);
        Assert.assertNotSame(newList, originalList); // 确保是新的实例
    }

    @Test(expectedExceptions = UnsupportedOperationException.class)
    public void testEmptyList() {
        List<String> emptyList = Collections.emptyList();
        Assert.assertNotNull(emptyList);
        Assert.assertTrue(emptyList.isEmpty());

        // 验证是不可修改的
        emptyList.add("item");
    }

    @Test(expectedExceptions = NoSuchElementException.class)
    public void testEmptyIterator() {
        Iterator<String> emptyIterator = Collections.emptyIterator();
        Assert.assertNotNull(emptyIterator);
        Assert.assertFalse(emptyIterator.hasNext());

        // 验证调用next会抛出异常
        emptyIterator.next();
    }

    @Test(expectedExceptions = UnsupportedOperationException.class)
    public void testUnmodifiableList() {
        List<String> originalList = new ArrayList<>();
        originalList.add("item1");
        originalList.add("item2");

        List<String> unmodifiableList = Collections.unmodifiableList(originalList);

        // 验证内容相同
        Assert.assertEquals(unmodifiableList, originalList);

        // 验证修改会抛出异常
        unmodifiableList.add("item3");
    }

    @Test
    public void testIsUnmodifiableList() {
        List<String> arrayList = new ArrayList<>();
        arrayList.add("item");

        List<String> unmodifiableList = Collections.unmodifiableList(arrayList);

        // 可修改的list
        Assert.assertFalse(Collections.isUnmodifiableList(arrayList));

        // 不可修改的list
        Assert.assertTrue(Collections.isUnmodifiableList(unmodifiableList));
    }

    @Test
    public void testSort() {
        List<Integer> list = new ArrayList<>();
        list.add(3);
        list.add(1);
        list.add(2);

        Collections.sort(list);
        Assert.assertEquals(list.get(0), Integer.valueOf(1));
        Assert.assertEquals(list.get(1), Integer.valueOf(2));
        Assert.assertEquals(list.get(2), Integer.valueOf(3));
    }

    @Test
    public void testReverse() {
        List<String> list = new ArrayList<>();
        list.add("first");
        list.add("second");
        list.add("third");

        Collections.reverse(list);
        Assert.assertEquals(list.get(0), "third");
        Assert.assertEquals(list.get(1), "second");
        Assert.assertEquals(list.get(2), "first");
    }

    @Test(expectedExceptions = UnsupportedOperationException.class)
    public void testSingletonList() {
        List<String> singletonList = Collections.singletonList("single");
        Assert.assertNotNull(singletonList);
        Assert.assertEquals(singletonList.size(), 1);
        Assert.assertEquals(singletonList.get(0), "single");

        // 验证是不可修改的
        singletonList.add("another");
    }

    @Test(expectedExceptions = UnsupportedOperationException.class)
    public void testUnmodifiableSet() {
        Set<String> originalSet = new HashSet<>();
        originalSet.add("item1");
        originalSet.add("item2");

        Set<String> unmodifiableSet = Collections.unmodifiableSet(originalSet);

        // 验证内容相同
        Assert.assertEquals(unmodifiableSet, originalSet);

        // 验证修改会抛出异常
        unmodifiableSet.add("item3");
    }

    @Test(expectedExceptions = UnsupportedOperationException.class)
    public void testEmptySet() {
        Set<String> emptySet = Collections.emptySet();
        Assert.assertNotNull(emptySet);
        Assert.assertTrue(emptySet.isEmpty());

        // 验证是不可修改的
        emptySet.add("item");
    }
}