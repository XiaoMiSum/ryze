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

import java.util.concurrent.ThreadLocalRandom;

/**
 * 随机数生成函数实现类
 *
 * <p>该类用于生成伪随机整数，支持指定上限值。
 * 常用于需要随机数据的测试场景，如随机ID、随机数量等。</p>
 *
 * <p>在测试用例中可以通过 ${random()} 的方式调用该函数。</p>
 *
 * @author xiaomi
 */
public class Random implements Function {

    @Override
    public String key() {
        return "random";
    }

    /**
     * 生成（伪）随机数，支持一个参数
     *
     * <p>参数说明：
     * <ol>
     *   <li>bound: 随机数的上限值（不包含），允许为空</li>
     * </ol>
     * </p>
     *
     * <p>当传入的bound值小于1或空值时，则不限制返回的数据范围（可能为正数，也可能为负数）</p>
     *
     * <p>使用示例：
     * <pre>
     * ${random()}        // 生成任意整数随机数
     * ${random(100)}     // 生成0-99之间的随机数
     * ${random(1000)}    // 生成0-999之间的随机数
     * </pre>
     * </p>
     *
     * @param context 上下文对象
     * @param args    参数列表，包含随机数上限值
     * @return 生成的随机整数
     */
    @Override
    public Integer execute(ContextWrapper context, Args args) {
        checkMethodArgCount(args, 0, 1);
        var bound = args.getIntValue(0);
        var random = ThreadLocalRandom.current();
        return bound > 0 ? random.nextInt(bound) : random.nextInt();
    }
}