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

import java.util.UUID;

/**
 * UUID生成函数实现类
 *
 * <p>该类用于生成标准的UUID（通用唯一标识符）。
 * 常用于生成唯一标识符、主键、会话ID等需要全局唯一性的场景。</p>
 *
 * <p>在测试用例中可以通过 ${uuid()} 的方式调用该函数。</p>
 *
 * @author xiaomi
 */
public class Uuid implements Function {

    @Override
    public String key() {
        return "uuid";
    }

    /**
     * 返回一个UUID，无参数
     *
     * <p>该函数不接受任何参数，每次调用都会生成一个新的UUID字符串。</p>
     *
     * <p>使用示例：
     * <pre>
     * ${uuid()}     // 返回类似 "550e8400-e29b-41d4-a716-446655440000" 的UUID字符串
     * </pre>
     * </p>
     *
     * @param context 上下文对象
     * @param args    参数列表（该函数不使用任何参数）
     * @return 生成的UUID字符串
     */
    @Override
    public Object execute(ContextWrapper context, Args args) {
        return UUID.randomUUID().toString();
    }
}