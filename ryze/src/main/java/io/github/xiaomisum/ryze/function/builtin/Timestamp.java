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
public class Timestamp extends TimeShift {

    @Override
    public String key() {
        return "timestamp";
    }

    /**
     * 获取当前时间，支持一个参数
     *
     * <p>参数说明：
     * <ol>
     *   <li>format: 指定日期时间格式，如果该参数未传递，则返回时间戳</li>
     *   <li>amount: 表示要从当前时间中添加或减去的时间量</li>
     * </ol>
     * </p>
     *
     * <p>amount参数格式遵循ISO-8601标准的Duration格式：
     * <ul>
     *   <li>PT20.345S: 解析为 20.345秒</li>
     *   <li>PT15M: 解析为 15分钟</li>
     *   <li>PT10H: 解析为 10小时</li>
     *   <li>P2D: 解析为 2天</li>
     *   <li>P2DT3H4M: 解析为 2天3小时4分钟</li>
     *   <li>P-6H3M: 解析为 -6小时+3分钟</li>
     *   <li>-P6H3M: 解析为 -6小时-3分钟</li>
     *   <li>-P-6H+3M: 解析为 +6小时-3分钟</li>
     * </ul>
     * </p>
     *
     * <p>当两个参数都未传递时，则返回当前时间戳</p>
     *
     * <p>使用示例：
     * <pre>
     * ${timestamp()}                          // 返回当前时间戳
     * ${timestamp("yyyy-MM-dd")}              // 返回当前日期，格式为yyyy-MM-dd
     * ${timestamp("yyyy-MM-dd HH:mm:ss", "P1D")}     // 返回明天此刻的时间，格式为yyyy-MM-dd HH:mm:ss
     * ${timestamp("", "-P7D")}                // 返回7天前的时间戳
     * </pre>
     * </p>
     *
     * @param context 上下文对象
     * @param args    参数列表，包含日期时间格式
     * @return 格式化后的时间字符串或时间戳
     */
    @Override
    public String execute(ContextWrapper context, Args args) {
        checkMethodArgCount(args, 0, 2);
        return super.execute(context, args);
    }


}