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

public class ContainsMatcherTest {

    @Test
    public void testMatches() {
        ContainsMatcher matcher = new ContainsMatcher("hello");
        Assert.assertTrue(matcher.matches("hello world"));
        Assert.assertTrue(matcher.matches("say hello"));
        Assert.assertFalse(matcher.matches("goodbye world"));
    }

    @Test
    public void testMatchesWithNumbers() {
        ContainsMatcher matcher = new ContainsMatcher("123");
        Assert.assertTrue(matcher.matches("abc123def"));
        Assert.assertFalse(matcher.matches("456"));
    }

    @Test
    public void testMatchesWithStrictMode() {
        ContainsMatcher matcher = new ContainsMatcher("Hello", true);
        Assert.assertTrue(matcher.matches("Hello World"));
        Assert.assertFalse(matcher.matches("hello world"));
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void testConstructorWithNullExpected() {
        new ContainsMatcher(null);
    }

    @Test
    public void testDescribeTo() {
        ContainsMatcher matcher = new ContainsMatcher("test");
        String description = matcher.toString();
        Assert.assertNotNull(description);
    }
}