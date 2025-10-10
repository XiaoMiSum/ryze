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

import java.nio.charset.StandardCharsets;

/**
 * Base64Decode函数单元测试类
 *
 * <p>该类用于测试Base64Decode函数的正确性和边界情况处理。</p>
 *
 * @author xiaomi
 * @see Base64Decode
 */
public class Base64DecodeTest {

    /**
     * 测试Base64解码基本功能
     *
     * <p>验证Base64Decode函数能够正确地对Base64编码的字符串进行解码。</p>
     */
    @Test
    public void testBase64Decode() {
        Base64Decode function = new Base64Decode();
        ContextWrapper context = new ContextWrapper(SessionRunner.getSessionIfNoneCreateNew());
        Args args = new Args();
        args.add("SGVsbG8gV29ybGQ=");

        Object result = function.execute(context, args);
        byte[] bytes = (byte[]) result;
        Assert.assertEquals(new String(bytes, StandardCharsets.UTF_8), "Hello World");
    }

    /**
     * 测试Base64解码空字符串
     *
     * <p>验证Base64Decode函数能够正确处理空字符串。</p>
     */
    @Test
    public void testBase64DecodeEmptyString() {
        Base64Decode function = new Base64Decode();
        ContextWrapper context = new ContextWrapper(SessionRunner.getSessionIfNoneCreateNew());
        Args args = new Args();
        args.add("");


        Object result = function.execute(context, args);
        byte[] bytes = (byte[]) result;
        Assert.assertEquals(new String(bytes, StandardCharsets.UTF_8), "");
    }

    /**
     * 测试Base64解码特殊字符
     *
     * <p>验证Base64Decode函数能够正确处理包含特殊字符的Base64编码字符串。</p>
     */
    @Test
    public void testBase64DecodeSpecialCharacters() {
        Base64Decode function = new Base64Decode();
        ContextWrapper context = new ContextWrapper(SessionRunner.getSessionIfNoneCreateNew());
        Args args = new Args();
        args.add("SGVsbG9AV29ybGQhIyQl");
        Object result = function.execute(context, args);
        byte[] bytes = (byte[]) result;
        Assert.assertEquals(new String(bytes, StandardCharsets.UTF_8), "Hello@World!#$%");
    }

    /**
     * 测试Base64解码中文字符
     *
     * <p>验证Base64Decode函数能够正确处理中文字符的Base64编码。</p>
     */
    @Test
    public void testBase64DecodeChineseCharacters() {
        Base64Decode function = new Base64Decode();
        ContextWrapper context = new ContextWrapper(SessionRunner.getSessionIfNoneCreateNew());
        Args args = new Args();
        args.add("5L2g5aW95LiW55WM");
        Object result = function.execute(context, args);
        byte[] bytes = (byte[]) result;
        Assert.assertEquals(new String(bytes, StandardCharsets.UTF_8), "你好世界");
    }

    /**
     * 测试Base64解码数字和字母组合
     *
     * <p>验证Base64Decode函数能够正确处理数字和字母组合的Base64编码。</p>
     */
    @Test
    public void testBase64DecodeNumbersAndLetters() {
        Base64Decode function = new Base64Decode();
        ContextWrapper context = new ContextWrapper(SessionRunner.getSessionIfNoneCreateNew());
        Args args = new Args();
        args.add("QUJDMTIz");
        Object result = function.execute(context, args);
        byte[] bytes = (byte[]) result;
        Assert.assertEquals(new String(bytes, StandardCharsets.UTF_8), "ABC123");
    }

    /**
     * 测试Base64解码函数键名
     *
     * <p>验证Base64Decode函数的键名是否正确。</p>
     */
    @Test
    public void testBase64DecodeKey() {
        Base64Decode function = new Base64Decode();
        Assert.assertEquals(function.key(), "base64_decode");
    }

    /**
     * 测试Base64解码参数数量检查
     *
     * <p>验证Base64Decode函数在参数数量不正确时能够抛出异常。</p>
     */
    @Test(expectedExceptions = IllegalArgumentException.class)
    public void testBase64DecodeWithNoArgs() {
        Base64Decode function = new Base64Decode();
        ContextWrapper context = new ContextWrapper(SessionRunner.getSessionIfNoneCreateNew());
        Args args = new Args();

        function.execute(context, args);
    }

    /**
     * 测试Base64解码参数数量检查（参数过多）
     *
     * <p>验证Base64Decode函数在参数过多时能够抛出异常。</p>
     */
    @Test(expectedExceptions = IllegalArgumentException.class)
    public void testBase64DecodeWithTooManyArgs() {
        Base64Decode function = new Base64Decode();
        ContextWrapper context = new ContextWrapper(SessionRunner.getSessionIfNoneCreateNew());
        Args args = new Args();
        args.add("arg1");
        args.add("arg2");

        function.execute(context, args);
    }

    /**
     * 测试Base64解码无效Base64字符串
     *
     * <p>验证Base64Decode函数在处理无效Base64字符串时的行为。</p>
     */
    @Test
    public void testBase64DecodeInvalidString() {
        Base64Decode function = new Base64Decode();
        ContextWrapper context = new ContextWrapper(SessionRunner.getSessionIfNoneCreateNew());
        Args args = new Args();
        args.add("Invalid_Base64!");

        Object result = function.execute(context, args);
        // 解码无效的Base64字符串应该返回空字节数组或特定格式的字节数组
        Assert.assertNotNull(result);
        Assert.assertTrue(result instanceof byte[]);
    }
}