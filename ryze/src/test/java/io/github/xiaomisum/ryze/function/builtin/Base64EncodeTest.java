/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2022.  Lorem XiaoMiSum (mi_xiao@qq.com)
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

import io.github.xiaomisum.ryze.SessionRunner;
import io.github.xiaomisum.ryze.context.ContextWrapper;
import io.github.xiaomisum.ryze.function.Args;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * Base64Encode函数单元测试类
 *
 * <p>该类用于测试Base64Encode函数的正确性和边界情况处理。</p>
 *
 * @author xiaomi
 * @see Base64Encode
 */
public class Base64EncodeTest {

    /**
     * 测试Base64编码基本功能
     *
     * <p>验证Base64Encode函数能够正确地对字符串进行Base64编码。</p>
     */
    @Test
    public void testBase64Encode() {
        Base64Encode function = new Base64Encode();
        ContextWrapper context = new ContextWrapper(SessionRunner.getSessionIfNoneCreateNew());
        Args args = new Args();
        args.add("Hello World");

        Object result = function.execute(context, args);
        Assert.assertEquals(result, "SGVsbG8gV29ybGQ=");
    }

    /**
     * 测试Base64编码空字符串
     *
     * <p>验证Base64Encode函数能够正确处理空字符串。</p>
     */
    @Test
    public void testBase64EncodeEmptyString() {
        Base64Encode function = new Base64Encode();
        ContextWrapper context = new ContextWrapper(SessionRunner.getSessionIfNoneCreateNew());
        Args args = new Args();
        args.add("");

        Object result = function.execute(context, args);
        Assert.assertEquals(result, "");
    }

    /**
     * 测试Base64编码特殊字符
     *
     * <p>验证Base64Encode函数能够正确处理包含特殊字符的字符串。</p>
     */
    @Test
    public void testBase64EncodeSpecialCharacters() {
        Base64Encode function = new Base64Encode();
        ContextWrapper context = new ContextWrapper(SessionRunner.getSessionIfNoneCreateNew());
        Args args = new Args();
        args.add("Hello@World!#$%");

        Object result = function.execute(context, args);
        Assert.assertEquals(result, "SGVsbG9AV29ybGQhIyQl");
    }

    /**
     * 测试Base64编码中文字符
     *
     * <p>验证Base64Encode函数能够正确处理中文字符。</p>
     */
    @Test
    public void testBase64EncodeChineseCharacters() {
        Base64Encode function = new Base64Encode();
        ContextWrapper context = new ContextWrapper(SessionRunner.getSessionIfNoneCreateNew());
        Args args = new Args();
        args.add("你好世界");

        Object result = function.execute(context, args);
        Assert.assertEquals(result, "5L2g5aW95LiW55WM");
    }

    /**
     * 测试Base64编码数字和字母组合
     *
     * <p>验证Base64Encode函数能够正确处理数字和字母的组合。</p>
     */
    @Test
    public void testBase64EncodeNumbersAndLetters() {
        Base64Encode function = new Base64Encode();
        ContextWrapper context = new ContextWrapper(SessionRunner.getSessionIfNoneCreateNew());
        Args args = new Args();
        args.add("ABC123");

        Object result = function.execute(context, args);
        Assert.assertEquals(result, "QUJDMTIz");
    }

    /**
     * 测试Base64编码函数键名
     *
     * <p>验证Base64Encode函数的键名是否正确。</p>
     */
    @Test
    public void testBase64EncodeKey() {
        Base64Encode function = new Base64Encode();
        Assert.assertEquals(function.key(), "base64_encode");
    }

    /**
     * 测试Base64编码参数数量检查
     *
     * <p>验证Base64Encode函数在参数数量不正确时能够抛出异常。</p>
     */
    @Test(expectedExceptions = IllegalArgumentException.class)
    public void testBase64EncodeWithNoArgs() {
        Base64Encode function = new Base64Encode();
        ContextWrapper context = new ContextWrapper(SessionRunner.getSessionIfNoneCreateNew());
        Args args = new Args();

        function.execute(context, args);
    }

    /**
     * 测试Base64编码参数数量检查（参数过多）
     *
     * <p>验证Base64Encode函数在参数过多时能够抛出异常。</p>
     */
    @Test(expectedExceptions = IllegalArgumentException.class)
    public void testBase64EncodeWithTooManyArgs() {
        Base64Encode function = new Base64Encode();
        ContextWrapper context = new ContextWrapper(SessionRunner.getSessionIfNoneCreateNew());
        Args args = new Args();
        args.add("arg1");
        args.add("arg2");

        function.execute(context, args);
    }
}