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

import com.alibaba.fastjson2.JSON;
import io.github.xiaomisum.ryze.assertion.ProxyMatcher;
import io.github.xiaomisum.ryze.support.Collections;
import io.github.xiaomisum.ryze.support.Comparator;
import io.github.xiaomisum.ryze.support.PrimitiveTypeChecker;
import io.github.xiaomisum.ryze.testelement.KW;
import org.hamcrest.Description;

import java.util.Collection;

/**
 * 任意包含匹配器
 *
 * <p>该匹配器用于验证实际值是否包含期望值集合中的任意一个元素。
 * 与ContainsMatcher不同，该匹配器的期望值可以是集合或数组，检查实际值是否包含其中任意一个元素。</p>
 *
 * <p>匹配规则：
 * <ul>
 *   <li>当期望值为数组时，先转换为集合</li>
 *   <li>当期望值为集合并且实际值为基本类型时，检查实际值是否包含集合中任意元素</li>
 *   <li>其他情况，使用Comparator.contains方法检查实际值是否包含期望值</li>
 * </ul>
 * </p>
 *
 * <p>支持的关键字: any_contains, contains_any, any_contains, contain_any</p>
 *
 * @author xiaomi
 * @see Comparator#contains(Object, Object, boolean)
 * @since 2025/8/22
 */
@KW({"any_contains", "contains_any", "any_contains", "contain_any"})
public class AnyContainsMatcher extends ProxyMatcher {

    /**
     * 构造函数，使用默认的非严格模式
     *
     * @param expected 期望值，可以是单个值、数组或集合
     */
    public AnyContainsMatcher(Object expected) {
        this(expected, false);
    }

    /**
     * 构造函数，可指定是否严格匹配
     *
     * @param expected 期望值，可以是单个值、数组或集合
     * @param strict   严格匹配标志，true表示区分大小写，false表示忽略大小写
     */
    public AnyContainsMatcher(Object expected, boolean strict) {
        super(expected, strict);
    }

    /**
     * 执行任意包含匹配逻辑
     *
     * <p>根据不同的数据类型执行不同的比较逻辑：
     * <ol>
     *   <li>如果期望值是数组，则转换为集合</li>
     *   <li>如果期望值是集合且实际值是基本类型，则检查实际值是否包含集合中任意元素</li>
     *   <li>其他情况，使用Comparator.contains方法检查实际值是否包含期望值</li>
     * </ol>
     * </p>
     *
     * @param actualValue 实际值
     * @return 如果实际值包含期望值集合中的任意一个元素返回true，否则返回false
     * @see Comparator#contains(Object, Object, boolean)
     */
    @Override
    public boolean matches(Object actualValue) {
        if (expectedValue instanceof Object[] expectedArray) {
            expectedValue = Collections.newArrayList(expectedArray);
        }
        if (expectedValue instanceof Collection<?> expectedCollection && PrimitiveTypeChecker.isPrimitiveOrWrapper(actualValue)) {
            return expectedCollection.stream().anyMatch(e -> Comparator.contains(actualValue, e, !strict));
        }
        return Comparator.contains(actualValue, expectedValue, !strict);
    }

    /**
     * 描述匹配器的期望值和匹配条件
     *
     * <p>生成匹配器的描述信息，包括期望值和匹配模式（严格/非严格）</p>
     *
     * @param description 描述对象
     */
    @Override
    public void describeTo(Description description) {
        var isPrimitiveOrWrapper = PrimitiveTypeChecker.isPrimitiveOrWrapper(expectedValue);
        description.appendText("equals ").appendValue(isPrimitiveOrWrapper ? expectedValue : JSON.toJSONString(expectedValue))
                .appendText(strict || isPrimitiveOrWrapper ? " (%s%s)".formatted(isPrimitiveOrWrapper ? "any" : "", !strict ? " ignore case" : "") : "");

    }
}