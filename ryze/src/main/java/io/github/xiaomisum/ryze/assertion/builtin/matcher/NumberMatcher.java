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
import io.github.xiaomisum.ryze.assertion.ProxyMatcher;
import org.hamcrest.Description;

/**
 * 数字匹配器抽象基类
 *
 * <p>该类是所有数字比较匹配器的基类，提供了通用的数字比较逻辑。
 * 支持大于、小于、大于等于、小于等于四种比较操作。</p>
 *
 * <p>匹配规则：
 * <ul>
 *   <li>将期望值和实际值都转换为double类型进行比较</li>
 *   <li>根据操作符类型执行相应的比较逻辑</li>
 * </ul>
 * </p>
 *
 * @author xiaomi
 * @see Matchers.CompareOperator
 * @since 2025/8/22
 */
public abstract class NumberMatcher extends ProxyMatcher {

    /**
     * 比较操作符
     * <p>定义了具体的比较操作类型，如大于、小于、大于等于、小于等于</p>
     */
    private final Matchers.CompareOperator operator;

    /**
     * 构造函数
     *
     * @param expected 期望值
     * @param operator 比较操作符
     */
    public NumberMatcher(Object expected, Matchers.CompareOperator operator) {
        super(expected);
        this.operator = operator;
    }

    /**
     * 执行数字比较匹配逻辑
     *
     * <p>将期望值和实际值都转换为double类型，然后根据操作符类型执行相应的比较逻辑：
     * <ul>
     *   <li>LESS_THAN: 实际值 < 期望值</li>
     *   <li>LESS_THAN_OR_EQUAL: 实际值 <= 期望值</li>
     *   <li>GREATER_THAN: 实际值 > 期望值</li>
     *   <li>GREATER_THAN_OR_EQUAL: 实际值 >= 期望值</li>
     * </ul>
     * </p>
     *
     * @param actual 实际值
     * @return 根据操作符和数值比较结果返回true或false
     */
    @Override
    public boolean matches(Object actual) {
        double expectedValue = convertToDouble(this.expectedValue);
        double actualValue = convertToDouble(actual);
        return switch (operator) {
            case LESS_THAN -> actualValue < expectedValue;
            case LESS_THAN_OR_EQUAL -> actualValue <= expectedValue;
            case GREATER_THAN -> actualValue > expectedValue;
            case GREATER_THAN_OR_EQUAL -> actualValue >= expectedValue;
        };
    }

    /**
     * 描述匹配器的期望值和比较操作符
     *
     * <p>生成匹配器的描述信息，包括比较操作符和期望值</p>
     *
     * @param description 描述对象
     */
    @Override
    public void describeTo(Description description) {
        description.appendValue(operator.getDesc() + " " + expectedValue);
    }

    /**
     * 将对象转换为double类型
     *
     * <p>支持Number类型和可解析为数字的字符串</p>
     *
     * @param value 要转换的值
     * @return 转换后的double值
     * @throws NumberFormatException 当字符串无法解析为数字时抛出
     */
    private double convertToDouble(Object value) {
        if (value instanceof Number number) {
            return number.doubleValue();
        }
        return Double.parseDouble(value.toString());
    }
}