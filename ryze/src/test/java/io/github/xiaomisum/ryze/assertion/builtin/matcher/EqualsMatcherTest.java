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

public class EqualsMatcherTest {

    @Test
    public void testMatchesWithStrings() {
        EqualsMatcher matcher = new EqualsMatcher("hello");
        Assert.assertTrue(matcher.matches("hello"));
        Assert.assertFalse(matcher.matches("world"));
    }

    @Test
    public void testMatchesWithNumbers() {
        EqualsMatcher matcher = new EqualsMatcher(10);
        Assert.assertTrue(matcher.matches(10));
        Assert.assertFalse(matcher.matches(20));
    }

    @Test
    public void testMatchesWithStrictMode() {
        EqualsMatcher matcher = new EqualsMatcher("Hello", true);
        Assert.assertTrue(matcher.matches("Hello"));
        Assert.assertFalse(matcher.matches("hello"));
    }

    @Test
    public void testMatchesWithIgnoreCase() {
        EqualsMatcher matcher = new EqualsMatcher("Hello", false);
        //  Assert.assertTrue(matcher.matches("Hello"));
        Assert.assertTrue(matcher.matches("hello"));
    }

    @Test
    public void testDescribeTo() {
        EqualsMatcher matcher = new EqualsMatcher("test");
        String description = matcher.toString();
        Assert.assertNotNull(description);
    }
}