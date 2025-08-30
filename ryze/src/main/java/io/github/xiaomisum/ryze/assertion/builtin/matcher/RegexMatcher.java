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
import io.github.xiaomisum.ryze.testelement.KW;
import org.hamcrest.Description;

import java.util.regex.Pattern;

/**
 * 正则表达式匹配器
 *
 * <p>该匹配器用于验证实际值是否匹配指定的正则表达式模式。
 * 支持严格模式（区分大小写）和非严格模式（忽略大小写）。</p>
 *
 * <p>匹配规则：
 * <ul>
 *   <li>使用Java的Pattern类进行正则表达式匹配</li>
 *   <li>在严格模式下，区分大小写进行匹配</li>
 *   <li>在非严格模式下，忽略大小写进行匹配</li>
 * </ul>
 * </p>
 *
 * <p>支持的关键字: regex, rx, 正则, 正则表达式</p>
 *
 * @author xiaomi
 * @see Pattern
 * @since 2025/8/22
 */
@KW({"regex", "rx", "正则", "正则表达式"})
public class RegexMatcher extends ProxyMatcher {

    /**
     * 正则表达式模式
     * <p>编译后的Pattern对象，用于执行匹配操作</p>
     */
    private final Pattern pattern;

    /**
     * 构造函数，使用默认的非严格模式
     *
     * @param expected 期望的正则表达式模式
     */
    public RegexMatcher(Object expected) {
        this(expected, false);
    }

    /**
     * 构造函数，可指定是否严格匹配
     *
     * @param expected 期望的正则表达式模式
     * @param strict   严格匹配标志，true表示区分大小写，false表示忽略大小写
     * @throws IllegalArgumentException 当期望值为null时抛出
     */
    public RegexMatcher(Object expected, boolean strict) {
        super(strict);
        if (expected == null) {
            throw new IllegalArgumentException("Regex pattern cannot be null");
        }
        this.pattern = Pattern.compile(expected.toString(), !strict ? Pattern.CASE_INSENSITIVE : 0);
    }

    /**
     * 执行正则表达式匹配逻辑
     *
     * <p>使用编译后的Pattern对象对实际值进行匹配。
     * 实际值必须是String类型。</p>
     *
     * @param actualValue 实际值，必须是String类型
     * @return 如果实际值匹配正则表达式返回true，否则返回false
     * @throws ClassCastException 当actualValue不是String类型时抛出
     */
    @Override
    public boolean matches(Object actualValue) {
        return pattern.matcher((String) actualValue).matches();
    }

    /**
     * 描述匹配器的正则表达式模式和匹配条件
     *
     * <p>生成匹配器的描述信息，包括正则表达式模式和匹配模式（区分/忽略大小写）</p>
     *
     * @param description 描述对象
     */
    @Override
    public void describeTo(Description description) {
        description.appendText("matches regex: ").appendValue(pattern.pattern()).appendText(strict ? "" : " (ignore case)");
    }
}