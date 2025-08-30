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
import org.apache.commons.lang3.StringUtils;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;

/**
 * 时间戳函数实现类
 *
 * <p>该类用于获取当前时间的时间戳或格式化时间字符串。
 * 常用于生成时间标识、记录操作时间等场景。</p>
 *
 * <p>在测试用例中可以通过 ${timestamp()} 的方式调用该函数。</p>
 *
 * @author mi.xiao
 */
public class Timestamp implements Function {

    @Override
    public String key() {
        return "timestamp";
    }

    /**
     * 获取当前时间，支持一个参数
     *
     * <p>参数说明：
     * <ol>
     *   <li>format: 指定日期时间格式，如：yyyy-MM-dd、yyyy-MM-dd HH:mm:ss、yyyyMMdd、yyyyMMddHHmmss</li>
     * </ol>
     * </p>
     *
     * <p>当格式化参数未传递时，则返回当前时间戳</p>
     *
     * <p>使用示例：
     * <pre>
     * ${timestamp()}                                // 返回当前时间戳
     * ${timestamp("yyyy-MM-dd")}                    // 返回当前日期，格式为yyyy-MM-dd
     * ${timestamp("yyyy-MM-dd HH:mm:ss")}           // 返回当前日期时间，格式为yyyy-MM-dd HH:mm:ss
     * ${timestamp("yyyyMMddHHmmss")}                // 返回当前日期时间，格式为yyyyMMddHHmmss
     * </pre>
     * </p>
     *
     * @param context 上下文对象
     * @param args    参数列表，包含日期时间格式
     * @return 格式化后的时间字符串或时间戳
     */
    @Override
    public String execute(ContextWrapper context, Args args) {
        var localDateTime = LocalDateTime.now(ZoneId.systemDefault());
        var format = args.getString(0);
        if (StringUtils.isBlank(format)) {
            var offset = ZoneOffset.systemDefault().getRules().getOffset(localDateTime);
            return String.valueOf(localDateTime.toInstant(offset).toEpochMilli());
        }
        return localDateTime.format(DateTimeFormatter.ofPattern(format));
    }


}