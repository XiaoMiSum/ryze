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

package io.github.xiaomisum.ryze.assertion;

import org.hamcrest.BaseMatcher;

/**
 * Matcher抽象基类，继承自Hamcrest的BaseMatcher
 *
 * <p>该类是所有自定义Matcher的基类，提供了通用的属性和构造方法。
 * 它封装了期望值和严格匹配标志，为具体的匹配器实现提供了基础功能。</p>
 *
 * <p>在字符串比较时，strict为true表示严格区分大小写，false表示忽略大小写；
 * 在对象比较时，strict可能用于其他用途（如是否严格匹配类型）。</p>
 *
 * @author xiaomi
 * @since 2025/8/22
 */
public abstract class ProxyMatcher extends BaseMatcher<Object> {

    /**
     * 严格匹配标志
     * <p>在字符串比较时，true表示严格区分大小写，false表示忽略大小写；
     * 在对象比较时，可能用于其他用途（如是否严格匹配类型）</p>
     */
    protected boolean strict;

    /**
     * 期望值
     * <p>用于与实际值进行比较的期望值</p>
     */
    protected Object expectedValue;

    /**
     * 默认构造函数
     * <p>将期望值初始化为null，严格匹配标志设置为false</p>
     */
    public ProxyMatcher() {
        this(null);
    }

    /**
     * 带期望值的构造函数
     * <p>使用指定的期望值初始化，严格匹配标志设置为false（非严格模式）</p>
     *
     * @param expectedValue 期望值
     */
    public ProxyMatcher(Object expectedValue) {
        this(expectedValue, false); // 默认：非严格模式
    }

    /**
     * 带期望值和严格匹配标志的构造函数
     *
     * @param expectedValue 期望值
     * @param strict        严格匹配标志，true表示严格匹配，false表示宽松匹配
     */
    public ProxyMatcher(Object expectedValue, boolean strict) {
        this.expectedValue = expectedValue;
        this.strict = strict;
    }
}