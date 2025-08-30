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

import io.github.xiaomisum.ryze.assertion.ProxyMatcher;
import io.github.xiaomisum.ryze.support.Comparator;
import io.github.xiaomisum.ryze.testelement.KW;
import org.hamcrest.Description;

/**
 * 非空匹配器
 *
 * <p>该匹配器用于验证实际值是否不为空。与IsEmpty相反，
 * 当实际值不为空时返回true，为空时返回false。</p>
 *
 * <p>匹配规则：
 * <ul>
 *   <li>null值返回false</li>
 *   <li>空字符串（包括只包含空白字符的字符串）返回false</li>
 *   <li>空集合返回false</li>
 *   <li>空数组返回false</li>
 *   <li>空Map返回false</li>
 *   <li>其他情况返回true</li>
 * </ul>
 * </p>
 *
 * <p>支持的关键字: is_not_empty, is_not_null, is_not_blank, isNotEmpty, isNotNull, isNotBlank, 非空</p>
 *
 * @author xiaomi
 * @see Comparator#isEmpty(Object)
 * @since 2025/8/22
 */
@KW({"is_not_empty", "is_not_null", "is_not_blank", "isNotEmpty", "isNotNull", "isNotBlank", "非空"})
public class IsNotEmpty extends ProxyMatcher {

    /**
     * 默认构造函数
     * <p>使用默认的非严格模式</p>
     */
    public IsNotEmpty() {
        this(null, false);
    }

    /**
     * 构造函数，可指定期望值和是否严格匹配
     *
     * @param expectedValue 期望值
     * @param strict        严格匹配标志
     */
    public IsNotEmpty(Object expectedValue, boolean strict) {
        super(expectedValue, strict);
    }

    /**
     * 执行非空匹配逻辑
     *
     * <p>使用Comparator工具类的isEmpty方法进行判断，然后对结果取反。
     * 当实际值不为空时返回true，否则返回false。</p>
     *
     * @param actualValue 实际值
     * @return 如果实际值不为空返回true，否则返回false
     * @see Comparator#isEmpty(Object)
     */
    @Override
    public boolean matches(Object actualValue) {
        return !Comparator.isEmpty(actualValue);
    }

    /**
     * 描述匹配器的匹配条件
     *
     * <p>生成匹配器的描述信息，固定为"is empty or is null"（注意：这里描述的是反面条件）</p>
     *
     * @param description 描述对象
     */
    @Override
    public void describeTo(Description description) {
        description.appendText("is not empty or is not null");
    }
}