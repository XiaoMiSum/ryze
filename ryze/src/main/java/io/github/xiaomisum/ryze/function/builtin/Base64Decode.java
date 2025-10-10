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

import io.github.xiaomisum.ryze.context.ContextWrapper;
import io.github.xiaomisum.ryze.function.Args;
import io.github.xiaomisum.ryze.function.Function;
import org.apache.commons.codec.binary.Base64;

/**
 * Base64解码函数实现类
 *
 * <p>该类实现了Function接口，提供Base64解码功能。将输入的Base64编码字符串进行解码并返回解码结果。</p>
 *
 * <p>使用示例:
 * <pre>
 * ${base64_decode(SGVsbG8gV29ybGQ=)}
 * // 返回: Hello World
 * </pre>
 * </p>
 *
 * @author xiaomi
 * @see Function
 * @see Base64
 */
public class Base64Decode implements Function {

    /**
     * 获取函数键名
     *
     * @return 函数键名 "base64_decode"
     */
    @Override
    public String key() {
        return "base64_decode";
    }

    /**
     * 执行Base64解码操作
     *
     * <p>将输入的Base64编码字符串参数进行解码。该方法要求且仅要求一个字符串参数。</p>
     *
     * @param context 执行上下文环境
     * @param args    函数参数，必须包含一个Base64编码字符串参数
     * @return Base64解码后的字节数组结果
     * @throws IllegalArgumentException 当参数数量不为1时抛出
     */
    @Override
    public Object execute(ContextWrapper context, Args args) {
        checkMethodArgCount(args, 1, 1);
        return Base64.decodeBase64(args.getString(0));
    }
}