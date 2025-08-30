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

public class RandomTest {

    private final Random random = new Random();

    @Test
    public void testKey() {
        Assert.assertEquals(random.key(), "random");
    }

    @Test
    public void testExecuteWithoutBound() {
        Args args = new Args();
        Object result = random.execute(null, args);
        Assert.assertNotNull(result);
        Assert.assertTrue(result instanceof Integer);
    }

    @Test
    public void testExecuteWithBound() {
        Args args = new Args();
        args.add(100);
        Object result = random.execute(null, args);
        Assert.assertNotNull(result);
        Assert.assertTrue(result instanceof Integer);
        int value = (Integer) result;
        Assert.assertTrue(value >= 0 && value < 100);
    }

    @Test
    public void testExecuteWithZeroBound() {
        Args args = new Args();
        args.add(0);
        Object result = random.execute(null, args);
        Assert.assertNotNull(result);
        Assert.assertTrue(result instanceof Integer);
    }
}