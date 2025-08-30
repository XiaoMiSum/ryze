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

public class UrlEncodeTest {

    private final UrlEncode urlEncode = new UrlEncode();

    @Test
    public void testKey() {
        Assert.assertEquals(urlEncode.key(), "url_encode");
    }

    @Test
    public void testExecute() {
        Args args = new Args();
        args.add("Hello World");
        String result = urlEncode.execute(null, args);
        Assert.assertEquals(result, "Hello%20World");
    }

    @Test
    public void testExecuteWithChinese() {
        Args args = new Args();
        args.add("中文");
        String result = urlEncode.execute(null, args);
        Assert.assertEquals(result, "%E4%B8%AD%E6%96%87");
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void testExecuteWithEmptyContent() {
        Args args = new Args();
        args.add("");
        urlEncode.execute(null, args);
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void testExecuteWithNullContent() {
        Args args = new Args();
        args.add(null);
        urlEncode.execute(null, args);
    }
}