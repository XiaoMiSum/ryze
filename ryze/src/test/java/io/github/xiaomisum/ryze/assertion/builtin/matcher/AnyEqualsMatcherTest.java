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

package io.github.xiaomisum.ryze.assertion.builtin.matcher;

import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.Arrays;
import java.util.List;

public class AnyEqualsMatcherTest {

    @Test
    public void testMatchesWithArray() {
        AnyEqualsMatcher matcher = new AnyEqualsMatcher(new String[]{"hello", "world"});
        Assert.assertTrue(matcher.matches("hello"));
        Assert.assertTrue(matcher.matches("world"));
        Assert.assertFalse(matcher.matches("test"));
    }

    @Test
    public void testMatchesWithCollection() {
        List<String> expected = Arrays.asList("apple", "banana");
        AnyEqualsMatcher matcher = new AnyEqualsMatcher(expected);
        Assert.assertTrue(matcher.matches("apple"));
        Assert.assertTrue(matcher.matches("banana"));
        Assert.assertFalse(matcher.matches("orange"));
    }

    @Test
    public void testMatchesWithPrimitiveValue() {
        List<Integer> expected = Arrays.asList(1, 2, 3);
        AnyEqualsMatcher matcher = new AnyEqualsMatcher(expected);
        Assert.assertTrue(matcher.matches(1));
        Assert.assertTrue(matcher.matches(2));
        Assert.assertFalse(matcher.matches(4));
    }

    @Test
    public void testMatchesWithStrictMode() {
        AnyEqualsMatcher matcher = new AnyEqualsMatcher("Hello", true);
        Assert.assertTrue(matcher.matches("Hello"));
        Assert.assertFalse(matcher.matches("hello"));
    }

    @Test
    public void testDescribeTo() {
        AnyEqualsMatcher matcher = new AnyEqualsMatcher("test");
        String description = matcher.toString();
        Assert.assertNotNull(description);
    }
}