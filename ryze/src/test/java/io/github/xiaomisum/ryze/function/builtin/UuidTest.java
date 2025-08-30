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

public class UuidTest {

    private final Uuid uuid = new Uuid();

    @Test
    public void testKey() {
        Assert.assertEquals(uuid.key(), "uuid");
    }

    @Test
    public void testExecute() {
        Args args = new Args();
        Object result = uuid.execute(null, args);
        Assert.assertNotNull(result);
        Assert.assertTrue(result instanceof String);
        // UUID format: 8-4-4-4-12 hexadecimal digits
        Assert.assertTrue(((String) result).matches("[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}"));
    }

    @Test
    public void testExecuteMultipleCalls() {
        Args args = new Args();
        Object result1 = uuid.execute(null, args);
        Object result2 = uuid.execute(null, args);
        Assert.assertNotEquals(result1, result2);
    }
}