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

import java.util.HashMap;
import java.util.Map;

public class SameObjectMatcherTest {

    @Test
    public void testMatchesWithNullValues() {
        SameObjectMatcher matcher = new SameObjectMatcher(null);
        Assert.assertTrue(matcher.matches(null));
        Assert.assertFalse(matcher.matches("test"));
    }

    @Test
    public void testMatchesWithStrings() {
        SameObjectMatcher matcher = new SameObjectMatcher("hello");
        Assert.assertTrue(matcher.matches("hello"));
        Assert.assertFalse(matcher.matches("world"));
    }

    @Test
    public void testMatchesWithMaps() {
        Map<String, String> expected = new HashMap<>();
        expected.put("key1", "value1");
        expected.put("key2", "value2");

        Map<String, String> actual = new HashMap<>();
        actual.put("key1", "value1");
        actual.put("key2", "value2");

        SameObjectMatcher matcher = new SameObjectMatcher(expected);
        Assert.assertTrue(matcher.matches(actual));
    }

    @Test
    public void testMatchesWithStrictMode() {
        SameObjectMatcher matcher = new SameObjectMatcher("Hello", true);
        Assert.assertTrue(matcher.matches("Hello"));
        Assert.assertFalse(matcher.matches("hello"));
    }

    @Test
    public void testMatchesWithDifferentTypes() {
        SameObjectMatcher matcher = new SameObjectMatcher("10", false);
        Assert.assertTrue(matcher.matches(10)); // Should pass with non-strict mode
    }

    @Test
    public void testDescribeTo() {
        SameObjectMatcher matcher = new SameObjectMatcher("test");
        String description = matcher.toString();
        Assert.assertNotNull(description);
    }
}