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

public class NotContainsMatcherTest {

    @Test
    public void testMatches() {
        NotContainsMatcher matcher = new NotContainsMatcher("hello");
        Assert.assertFalse(matcher.matches("hello world"));
        Assert.assertFalse(matcher.matches("say hello"));
        Assert.assertTrue(matcher.matches("goodbye world"));
    }

    @Test
    public void testMatchesWithNumbers() {
        NotContainsMatcher matcher = new NotContainsMatcher("123");
        Assert.assertFalse(matcher.matches("abc123def"));
        Assert.assertTrue(matcher.matches("456"));
    }

    @Test
    public void testMatchesWithStrictMode() {
        NotContainsMatcher matcher = new NotContainsMatcher("Hello", true);
        Assert.assertFalse(matcher.matches("Hello World"));
        Assert.assertTrue(matcher.matches("hello world"));
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void testConstructorWithNullExpected() {
        new NotContainsMatcher(null);
    }

    @Test
    public void testDescribeTo() {
        NotContainsMatcher matcher = new NotContainsMatcher("test");
        String description = matcher.toString();
        Assert.assertNotNull(description);
    }
}