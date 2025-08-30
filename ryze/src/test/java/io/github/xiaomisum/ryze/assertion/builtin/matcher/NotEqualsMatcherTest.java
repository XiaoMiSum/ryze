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

public class NotEqualsMatcherTest {

    @Test
    public void testMatchesWithStrings() {
        NotEqualsMatcher matcher = new NotEqualsMatcher("hello");
        Assert.assertFalse(matcher.matches("hello"));
        Assert.assertTrue(matcher.matches("world"));
    }

    @Test
    public void testMatchesWithNumbers() {
        NotEqualsMatcher matcher = new NotEqualsMatcher(10);
        Assert.assertFalse(matcher.matches(10));
        Assert.assertTrue(matcher.matches(20));
    }

    @Test
    public void testMatchesWithStrictMode() {
        NotEqualsMatcher matcher = new NotEqualsMatcher("Hello", true);
        Assert.assertFalse(matcher.matches("Hello"));
        Assert.assertTrue(matcher.matches("hello"));
    }

    @Test
    public void testMatchesWithIgnoreCase() {
        NotEqualsMatcher matcher = new NotEqualsMatcher("Hello", false);
        Assert.assertFalse(matcher.matches("Hello"));
        Assert.assertFalse(matcher.matches("hello"));
    }

    @Test
    public void testMatchesWithArray() {
        NotEqualsMatcher matcher = new NotEqualsMatcher(new String[]{"hello", "world"});
        Assert.assertFalse(matcher.matches("hello"));
        Assert.assertTrue(matcher.matches("test"));
    }

    @Test
    public void testMatchesWithCollection() {
        List<String> expected = Arrays.asList("apple", "banana");
        NotEqualsMatcher matcher = new NotEqualsMatcher(expected);
        Assert.assertFalse(matcher.matches("apple"));
        Assert.assertTrue(matcher.matches("orange"));
    }

    @Test
    public void testDescribeTo() {
        NotEqualsMatcher matcher = new NotEqualsMatcher("test");
        String description = matcher.toString();
        Assert.assertNotNull(description);
    }
}