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
import io.github.xiaomisum.ryze.support.Comparator;
import io.github.xiaomisum.ryze.support.PrimitiveTypeChecker;
import io.github.xiaomisum.ryze.testelement.KW;
import org.hamcrest.Description;

/**
 * 包含匹配器
 *
 * <p>该匹配器用于验证实际值是否包含期望值。支持多种数据类型的包含关系判断，
 * 如字符串包含子串、集合包含元素等。</p>
 *
 * <p>匹配规则：
 * <ul>
 *   <li>对于字符串类型，检查是否包含指定子串</li>
 *   <li>对于数字类型，检查数字字符串是否包含指定子串</li>
 *   <li>对于布尔类型，检查布尔值字符串是否包含指定子串</li>
 *   <li>对于集合类型，检查集合中是否有元素包含指定值</li>
 *   <li>对于Map类型，检查键或值是否包含指定值</li>
 * </ul>
 * </p>
 *
 * <p>支持的关键字: contains, ct, 包含, ⊆, contain</p>
 *
 * @author xiaomi
 * @see Comparator#contains(Object, Object, boolean)
 * @since 2025/8/22
 */
@KW({"contains", "ct", "包含", "⊆", "contain"})
public class ContainsMatcher extends ProxyMatcher {

    /**
     * 构造函数，使用默认的非严格模式
     *
     * @param expected 期望值
     * @throws IllegalArgumentException 当期望值为null时抛出
     */
    public ContainsMatcher(Object expected) {
        this(expected, false);
    }

    /**
     * 构造函数，可指定是否严格匹配
     *
     * @param expected 期望值
     * @param strict   严格匹配标志，true表示区分大小写，false表示忽略大小写
     * @throws IllegalArgumentException 当期望值为null时抛出
     */
    public ContainsMatcher(Object expected, boolean strict) {
        super(expected, strict);
        if (expected == null) {
            throw new IllegalArgumentException("expected value cannot be null");
        }
    }

    /**
     * 执行包含匹配逻辑
     *
     * <p>委托给containsCompatible方法执行具体的包含逻辑判断</p>
     *
     * @param actualValue 实际值
     * @return 如果实际值包含期望值返回true，否则返回false
     * @see #containsCompatible(Object, Object)
     */
    @Override
    public boolean matches(Object actualValue) {
        // 需要实现包含逻辑（使用 strict）
        return containsCompatible(actualValue, expectedValue);
    }

    /**
     * 执行具体的包含逻辑判断
     *
     * <p>使用Comparator工具类的contains方法进行比较，支持多种数据类型的包含关系判断。
     * 根据strict参数决定是否严格匹配（区分大小写）。</p>
     *
     * @param actualValue   实际值
     * @param expectedValue 期望值
     * @return 如果实际值包含期望值返回true，否则返回false
     * @see Comparator#contains(Object, Object, boolean)
     */
    @SuppressWarnings("all")
    private boolean containsCompatible(Object actualValue, Object expectedValue) {
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
        description.appendText("contains ").appendValue(isPrimitiveOrWrapper ? expectedValue : JSON.toJSONString(expectedValue))
                .appendText(!strict ? " ignore case" : "");
    }
}