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

import com.alibaba.fastjson2.annotation.JSONField;
import io.github.xiaomisum.ryze.builder.IBuilder;
import io.github.xiaomisum.ryze.context.ContextWrapper;
import io.github.xiaomisum.ryze.testelement.sampler.SampleResult;
import org.apache.commons.lang3.StringUtils;
import org.hamcrest.Matcher;
import org.hamcrest.MatcherAssert;

import java.util.function.Supplier;

/**
 * 抽象断言类，实现了断言的基本逻辑和通用功能
 *
 * <p>该类提供了断言的通用实现，包括字段、期望值、规则等属性的管理，
 * 以及断言执行的基本流程。具体的断言实现类需要继承该类并实现
 * {@link #extractActualValue(SampleResult)}方法来提供特定的实际值提取逻辑。</p>
 *
 * <p>断言执行流程：
 * <ol>
 *   <li>检查测试结果状态，如果已失败则直接返回</li>
 *   <li>提取实际值</li>
 *   <li>根据规则查找对应的验证规则实现</li>
 *   <li>执行验证规则并设置结果状态</li>
 *   <li>如果验证失败则抛出异常</li>
 * </ol>
 * </p>
 *
 * @author xiaomi
 * @see Assertion 断言接口
 * @see Matcher 验证规则接口
 */
@SuppressWarnings({"rawtypes", "unchecked"})
public abstract class AbstractAssertion implements Assertion, AssertionConstantsInterface {

    /**
     * 实际值，用于与期望值进行比较
     */
    protected Object actualValue;

    /**
     * 期望值，断言的目标值
     */
    @JSONField(name = EXPECTED)
    protected Object expected;

    /**
     * 验证规则，如"==", "!=", "contains"等
     */
    @JSONField(name = RULE)
    protected String rule;

    /**
     * 字段名，用于标识要验证的数据字段
     */
    @JSONField(name = FIELD)
    protected String field;

    /**
     * 严格匹配标志
     * <p>在字符串比较时，true表示严格区分大小写，false表示忽略大小写；
     * 在对象比较时，可能用于其他用途（如是否严格匹配类型）</p>
     */
    @JSONField(name = STRICT)
    protected boolean strict;

    /**
     * 指定匹配方式 {@link Matcher}
     * <p>
     * 通过 org.hamcrest.Matcher 接口实现，以提供更丰富的校验规则
     */
    @JSONField(serialize = false, deserialize = false)
    protected Matcher<Object> matcher;

    /**
     * 执行断言验证逻辑
     *
     * <p>该方法实现了断言的通用执行流程：
     * <ol>
     *   <li>计算期望值（变量\函数替换）</li>
     *   <li>提取实际值</li>
     *   <li>如果matcher非空，且为 ProxyMatcher 实例，则设置matcher的期望值和是否严格匹配</li>
     *   <li>如果matcher为空，则通过 rule、expected、strict创建一个 matcher对象</li>
     *   <li>调用 {@link MatcherAssert#assertThat(Object, Matcher)} 进行断言</li>
     * </ol>
     * </p>
     *
     * @param context 上下文对象，包含测试执行过程中的变量和状态信息
     */
    @Override
    public void assertThat(ContextWrapper context) {
        if (context.getTestResult() instanceof SampleResult result) {
            expected = context.evaluate(expected);
            actualValue = extractActualValue(result);
            if (matcher != null && (matcher instanceof ProxyMatcher proxy)) {
                proxy.strict = strict;
                proxy.expectedValue = expected;
            }
            if (matcher == null) {
                rule = StringUtils.isBlank(rule) ? "==" : rule;
                matcher = Matchers.createMatcher(rule, expected, strict);
            }
            MatcherAssert.assertThat(actualValue, matcher);
        }
    }

    /**
     * 提取期望值对象值
     *
     * <p>具体的断言实现类需要重写该方法，提供特定的提取期望值对象值的逻辑。</p>
     *
     * @param result 取样结果对象
     * @return 期望值对象值
     */
    protected abstract Object extractActualValue(SampleResult result);

    /**
     * 获取实际值
     *
     * @return 实际值
     */
    public Object getActualValue() {
        return actualValue;
    }

    /**
     * 设置实际值
     *
     * @param actualValue 实际值
     */
    public void setActualValue(Object actualValue) {
        this.actualValue = actualValue;
    }

    /**
     * 获取期望值
     *
     * @return 期望值
     */
    public Object getExpected() {
        return expected;
    }

    /**
     * 设置期望值
     *
     * @param expected 期望值
     */
    public void setExpected(Object expected) {
        this.expected = expected;
    }

    /**
     * 获取验证规则
     *
     * @return 验证规则
     */
    public String getRule() {
        return rule;
    }

    /**
     * 设置验证规则
     *
     * @param rule 验证规则
     */
    public void setRule(String rule) {
        this.rule = rule;
    }

    /**
     * 获取字段名
     *
     * @return 字段名
     */
    public String getField() {
        return field;
    }

    /**
     * 设置字段名
     *
     * @param field 字段名
     */
    public void setField(String field) {
        this.field = field;
    }

    /**
     * 验证器基础构建器
     *
     * @param <SELF>      构建类自身
     * @param <ASSERTION> 断言类型
     */
    public static abstract class Builder<SELF extends Builder<SELF, ASSERTION>, ASSERTION extends AbstractAssertion>
            implements IBuilder<ASSERTION> {
        private final ASSERTION assertion;
        protected SELF self;


        protected Builder(ASSERTION assertion) {
            self = (SELF) this;
            this.assertion = assertion;
        }

        /**
         * 设置字段名
         *
         * @param field 字段名
         * @return 构建器自身
         */
        public SELF field(String field) {
            this.assertion.field = field;
            return self;
        }

        /**
         * 设置验证规则
         *
         * @param rule 验证规则
         * @return 构建器自身
         */
        public SELF rule(String rule) {
            this.assertion.rule = rule;
            return self;
        }

        /**
         * 设置期望值
         *
         * @param expected 期望值
         * @return 构建器自身
         */
        public SELF expected(Object expected) {
            this.assertion.expected = expected;
            return self;
        }

        /**
         * 设置严格匹配模式
         *
         * @param strict 是否严格匹配
         * @return 构建器自身
         */
        public SELF strict(boolean strict) {
            this.assertion.strict = strict;
            return self;
        }

        /**
         * 指定匹配器
         *
         * @param matcher 匹配器
         * @return 构建器自身
         */
        public SELF matcher(Matcher<Object> matcher) {
            this.assertion.matcher = matcher;
            return self;
        }

        /**
         * 通过函数式表达式指定匹配器
         *
         * @param supplier 匹配器
         * @return 构建器自身
         */
        public SELF matcher(Supplier<Matcher<Object>> supplier) {
            this.assertion.matcher = supplier.get();
            return self;
        }

        /**
         * 构建断言对象
         *
         * @return 构建的断言对象
         */
        @Override
        public ASSERTION build() {
            return assertion;
        }
    }
}