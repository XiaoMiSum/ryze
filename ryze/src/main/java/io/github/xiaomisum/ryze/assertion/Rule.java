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

package io.github.xiaomisum.ryze.assertion;

/**
 * 验证规则接口，定义了断言验证规则的基本规范
 *
 * <p>所有验证规则都需要实现该接口，提供具体的验证逻辑实现。
 * 验证规则用于比较实际值和期望值，返回验证结果。</p>
 *
 * <p>常见的验证规则包括相等性比较、包含关系、大小比较、正则匹配等。
 * 每个规则实现类通常对应一种特定的比较操作。</p>
 *
 * @author xiaomi
 * @see AbstractAssertion 抽象断言类
 */
@Deprecated(since = "6.0.2", forRemoval = true)
public interface Rule {

    /**
     * 执行断言规则验证
     *
     * <p>该方法实现具体的验证逻辑，比较实际值和期望值是否符合规则要求。</p>
     *
     * @param actual   实际值，来自测试执行结果
     * @param expected 期望值，来自断言配置
     * @return 验证结果，true表示符合规则，false表示不符合规则
     */
    boolean assertThat(Object actual, Object expected);
}