/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2025.  Lorem XiaoMiSum (mi_xiao@qq.com)
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

package io.github.xiaomisum.ryze.assertion.builtin.matcher;

import io.github.xiaomisum.ryze.assertion.Matchers;
import io.github.xiaomisum.ryze.testelement.KW;

/**
 * 小于匹配器
 *
 * <p>该匹配器用于验证实际值是否小于期望值。继承自NumberMatcher，
 * 使用LESS_THAN操作符执行比较。</p>
 *
 * <p>匹配规则：
 * <ul>
 *   <li>将期望值和实际值都转换为double类型</li>
 *   <li>比较实际值是否小于期望值</li>
 * </ul>
 * </p>
 *
 * <p>支持的关键字: &lt;, 小于, less, less_than, lt</p>
 *
 * @author xiaomi
 * @see NumberMatcher
 * @see Matchers.CompareOperator#LESS_THAN
 * @since 2025/8/22
 */
@KW({"<", "小于", "less", "less_than", "lt"})
public class LessMatcher extends NumberMatcher {

    /**
     * 构造函数
     *
     * @param expected 期望值
     */
    public LessMatcher(Object expected) {
        super(expected, Matchers.CompareOperator.LESS_THAN);
    }
}