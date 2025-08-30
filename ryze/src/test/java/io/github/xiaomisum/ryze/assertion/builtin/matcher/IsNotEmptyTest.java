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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class IsNotEmptyTest {

    @Test
    public void testMatchesWithNonNull() {
        IsNotEmpty matcher = new IsNotEmpty();
        Assert.assertTrue(matcher.matches("test"));
    }

    @Test
    public void testMatchesWithNonEmptyString() {
        IsNotEmpty matcher = new IsNotEmpty();
        Assert.assertTrue(matcher.matches("hello"));
        Assert.assertFalse(matcher.matches(" "));
    }

    @Test
    public void testMatchesWithNonEmptyCollection() {
        IsNotEmpty matcher = new IsNotEmpty();
        List<String> list = new ArrayList<>();
        list.add("item");
        Assert.assertTrue(matcher.matches(list));
    }

    @Test
    public void testMatchesWithNonEmptyMap() {
        IsNotEmpty matcher = new IsNotEmpty();
        Map<String, String> map = new HashMap<>();
        map.put("key", "value");
        Assert.assertTrue(matcher.matches(map));
    }

    @Test
    public void testMatchesWithEmptyValues() {
        IsNotEmpty matcher = new IsNotEmpty();
        Assert.assertFalse(matcher.matches(null));
        Assert.assertFalse(matcher.matches(""));
        List<String> list = new ArrayList<>();
        Assert.assertFalse(matcher.matches(list));
    }

    @Test
    public void testDescribeTo() {
        IsNotEmpty matcher = new IsNotEmpty();
        String description = matcher.toString();
        Assert.assertNotNull(description);
        Assert.assertTrue(description.contains("not empty"));
    }
}