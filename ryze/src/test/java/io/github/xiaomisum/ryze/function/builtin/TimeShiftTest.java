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

public class TimeShiftTest {

    private final TimeShift timeShift = new TimeShift();

    @Test
    public void testKey() {
        Assert.assertEquals(timeShift.key(), "time_shift");
    }

    @Test
    public void testExecuteWithoutArgs() {
        Args args = new Args();
        String result = timeShift.execute(null, args);
        Assert.assertNotNull(result);
        // Should return a timestamp (numeric string)
        Assert.assertTrue(result.matches("\\d+"));
    }

    @Test
    public void testExecuteWithFormat() {
        Args args = new Args();
        args.add("yyyy-MM-dd");
        String result = timeShift.execute(null, args);
        Assert.assertNotNull(result);
        Assert.assertTrue(result.matches("\\d{4}-\\d{2}-\\d{2}"));
    }

    @Test
    public void testExecuteWithFormatAndShift() {
        Args args = new Args();
        args.add("yyyy-MM-dd");
        args.add("P1D"); // Plus one day
        String result = timeShift.execute(null, args);
        Assert.assertNotNull(result);
        Assert.assertTrue(result.matches("\\d{4}-\\d{2}-\\d{2}"));
    }

    @Test
    public void testExecuteWithShiftOnly() {
        Args args = new Args();
        args.add("");
        args.add("P1D");
        String result = timeShift.execute(null, args);
        Assert.assertNotNull(result);
        Assert.assertTrue(result.matches("\\d+"));
    }
}