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

public class DigestTest {

    private final Digest digest = new Digest();

    @Test
    public void testKey() {
        Assert.assertEquals(digest.key(), "digest");
    }

    @Test
    public void testExecuteWithDefaultMD5() {
        Args args = new Args();
        args.add("password");
        Object result = digest.execute(null, args);
        Assert.assertEquals(result, "5f4dcc3b5aa765d61d8327deb882cf99");
    }

    @Test
    public void testExecuteWithMD5Algorithm() {
        Args args = new Args();
        args.add("md5");
        args.add("password");
        Object result = digest.execute(null, args);
        Assert.assertEquals(result, "5f4dcc3b5aa765d61d8327deb882cf99");
    }

    @Test
    public void testExecuteWithSalt() {
        Args args = new Args();
        args.add("md5");
        args.add("password");
        args.add("salt");
        Object result = digest.execute(null, args);
        Assert.assertEquals(result, "b305cadbb3bce54f3aa59c64fec00dea");
    }

    @Test
    public void testExecuteWithUpperCase() {
        Args args = new Args();
        args.add("md5");
        args.add("password");
        args.add("");
        args.add(true);
        Object result = digest.execute(null, args);
        Assert.assertEquals(result, "5f4dcc3b5aa765d61d8327deb882cf99".toUpperCase());
    }

    @Test
    public void testExecuteWithSHA1() {
        Args args = new Args();
        args.add("sha1");
        args.add("password");
        Object result = digest.execute(null, args);
        Assert.assertEquals(result, "5baa61e4c9b93f3f0682250b6cf8331b7ee68fd8");
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void testExecuteWithEmptyContent() {
        Args args = new Args();
        args.add("md5");
        args.add("");
        digest.execute(null, args);
    }
}