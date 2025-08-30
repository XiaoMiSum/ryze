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

package io.github.xiaomisum.ryze.function.builtin;

import io.github.xiaomisum.ryze.function.Args;
import org.testng.Assert;
import org.testng.annotations.Test;

public class RandomStringTest {

    private final RandomString randomString = new RandomString();

    @Test
    public void testKey() {
        Assert.assertEquals(randomString.key(), "random_string");
    }

    @Test
    public void testExecuteWithoutArgs() {
        Args args = new Args();
        String result = randomString.execute(null, args);
        Assert.assertNotNull(result);
        Assert.assertEquals(result.length(), 10);
    }

    @Test
    public void testExecuteWithLength() {
        Args args = new Args();
        args.add(5);
        String result = randomString.execute(null, args);
        Assert.assertNotNull(result);
        Assert.assertEquals(result.length(), 5);
    }

    @Test
    public void testExecuteWithCustomChars() {
        Args args = new Args();
        args.add(6);
        args.add("abc123");
        String result = randomString.execute(null, args);
        Assert.assertNotNull(result);
        Assert.assertEquals(result.length(), 6);
        for (char c : result.toCharArray()) {
            Assert.assertTrue("abc123".indexOf(c) >= 0);
        }
    }

    @Test
    public void testExecuteWithUpperCase() {
        Args args = new Args();
        args.add(8);
        args.add("");
        args.add(true);
        String result = randomString.execute(null, args);
        Assert.assertNotNull(result);
        Assert.assertEquals(result.length(), 8);
        Assert.assertEquals(result, result.toUpperCase());
    }
}