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

public class TimestampTest {

    private final Timestamp timestamp = new Timestamp();

    @Test
    public void testKey() {
        Assert.assertEquals(timestamp.key(), "timestamp");
    }

    @Test
    public void testExecuteWithoutFormat() {
        Args args = new Args();
        String result = timestamp.execute(null, args);
        Assert.assertNotNull(result);
        // Should return a timestamp (numeric string)
        Assert.assertTrue(result.matches("\\d+"));
    }

    @Test
    public void testExecuteWithDateFormat() {
        Args args = new Args();
        args.add("yyyy-MM-dd");
        String result = timestamp.execute(null, args);
        Assert.assertNotNull(result);
        Assert.assertTrue(result.matches("\\d{4}-\\d{2}-\\d{2}"));
    }

    @Test
    public void testExecuteWithDateTimeFormat() {
        Args args = new Args();
        args.add("yyyy-MM-dd HH:mm:ss");
        String result = timestamp.execute(null, args);
        Assert.assertNotNull(result);
        Assert.assertTrue(result.matches("\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2}"));
    }
}