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
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;

/**
 * 随机字符串生成函数实现类
 *
 * <p>该类用于生成指定长度的随机字符串，支持自定义字符集和大小写转换。
 * 常用于生成随机用户名、密码、标识符等测试数据。</p>
 *
 * <p>在测试用例中可以通过 ${random_string()} 的方式调用该函数。</p>
 *
 * @author xiaomi
 */
public class RandomString implements Function {

    @Override
    public String key() {
        return "random_string";
    }

    /**
     * 获取随机字符串，支持三个参数，且三个参数都允许为空
     *
     * <p>参数说明：
     * <ol>
     *   <li>length: 返回的随机字符串长度，允许为空，默认：10</li>
     *   <li>string: 基础字符串，允许为空，如果传了该参数，则从该参数中获取字符作为种子生成随机字符串</li>
     *   <li>upper: 是否将生成的字符串转为大写，允许为空，默认：false</li>
     * </ol>
     * </p>
     *
     * <p>使用示例：
     * <pre>
     * ${random_string()}                    // 生成默认长度为10的小写随机字符串
     * ${random_string(5)}                   // 生成长度为5的小写随机字符串
     * ${random_string(8, "abcd1234")}       // 从指定字符集中生成长度为8的随机字符串
     * ${random_string(6, "", true)}         // 生成长度为6的大写随机字符串
     * </pre>
     * </p>
     *
     * @param context 上下文对象
     * @param args    参数列表，包含长度、字符集和大小写选项
     * @return 生成的随机字符串
     */
    public String execute(ContextWrapper context, Args args) {
        checkMethodArgCount(args, 0, 3);
        var length = args.getString(0).isEmpty() ? 10 : args.getIntValue(0);
        var charsToUse = args.getString(1);
        var upper = args.getBooleanValue(2);
        length = length == 0 ? 10 : length;
        var randomString = StringUtils.isBlank(charsToUse) ? RandomStringUtils.secure().nextAlphabetic(length)
                : RandomStringUtils.secure().next(length, charsToUse);
        return upper ? randomString.toUpperCase() : randomString;
    }

}