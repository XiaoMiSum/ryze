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

public class GoogleAuthCodeTest {

    private final GoogleAuthCode googleAuthCode = new GoogleAuthCode();

    @Test
    public void testKey() {
        Assert.assertEquals(googleAuthCode.key(), "google2fa");
    }

    @Test
    public void testExecute() {
        Args args = new Args();
        args.add("JBSWY3DPEHPK3PXP"); // Sample Google Authenticator secret
        Object result = googleAuthCode.execute(null, args);
        Assert.assertNotNull(result);
        Assert.assertTrue(result instanceof String);
        Assert.assertEquals(((String) result).length(), 6);
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void testExecuteWithEmptySecret() {
        Args args = new Args();
        args.add("");
        googleAuthCode.execute(null, args);
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void testExecuteWithNullSecret() {
        Args args = new Args();
        args.add(null);
        googleAuthCode.execute(null, args);
    }
}