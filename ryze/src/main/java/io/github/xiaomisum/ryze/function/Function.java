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

package io.github.xiaomisum.ryze.function;

import io.github.xiaomisum.ryze.context.ContextWrapper;

/**
 * 函数接口，定义了扩展函数的基本规范
 *
 * <p>所有自定义函数都需要实现该接口，提供函数的唯一标识(key)和执行逻辑(execute)。
 * 函数通过key()方法进行注册和调用，在测试用例中通过类似 ${functionName()} 的语法使用。</p>
 *
 * <p>函数执行时会传入上下文对象和参数列表，根据具体实现返回相应的数据。
 * 函数可用于生成动态数据、处理字符串、计算时间等场景。</p>
 *
 * <p>在Ryze框架中，函数是实现测试用例动态数据处理的重要机制，
 * 通过函数可以生成随机数、获取当前时间、处理字符串等，
 * 大大增强了测试用例的灵活性和实用性。</p>
 *
 * @author xiaomi
 * @see Args 参数列表类
 * @see ContextWrapper 上下文包装类
 */
@SuppressWarnings("all")
public interface Function {

    /**
     * 获取函数的唯一标识符
     *
     * <p>该标识符用于在测试用例中调用函数，例如一个返回uuid的函数实现该方法返回"uuid"，
     * 则在测试用例中可以通过 ${uuid()} 的方式调用该函数</p>
     *
     * <p>函数标识符应遵循以下规范：
     * <ul>
     *   <li>使用小写字母和下划线命名</li>
     *   <li>具有明确的语义，能够表达函数的功能</li>
     *   <li>在系统中保持唯一性</li>
     * </ul></p>
     *
     * @return 函数的唯一标识符，用于在表达式中调用函数
     */
    String key();

    /**
     * 检查函数参数数量是否符合要求
     *
     * <p>用于验证传入参数的数量是否在指定范围内，如果不符则抛出运行时异常</p>
     *
     * <p>该方法提供了一个便捷的参数验证机制，函数实现可以调用此方法来确保参数数量正确。</p>
     *
     * @param args   参数列表
     * @param minCnt 最小参数数量
     * @param maxCnt 最大参数数量
     * @throws RuntimeException 当参数数量不在指定范围内时抛出
     */
    default void checkMethodArgCount(Args args, int minCnt, int maxCnt) throws RuntimeException {
        if (args.size() < minCnt || args.size() > maxCnt) {
            throw new RuntimeException("函数 " + key() + " 参数数量错误, 期望参数数量为  " + minCnt + " ~ " + maxCnt + ", 实际为 " + args.size());
        }
    }

    /**
     * 执行函数逻辑并返回结果
     *
     * <p>这是函数的核心实现方法，根据传入的上下文和参数执行具体业务逻辑</p>
     *
     * <p>函数执行时可以访问测试上下文中的变量和配置信息，也可以对参数进行处理，
     * 最终返回处理结果。返回值可以是任意类型的对象，框架会自动处理类型转换。</p>
     *
     * @param context 上下文对象，包含测试执行过程中的变量和状态信息
     * @param args    函数参数列表，包含调用函数时传入的所有参数
     * @return 函数执行结果，可以是任意类型的数据，如字符串、数字、布尔值等
     */
    Object execute(ContextWrapper context, Args args);

}