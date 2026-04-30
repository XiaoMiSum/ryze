/*
 *
 *  * The MIT License (MIT)
 *  *
 *  * Copyright (c) 2025.  Lorem XiaoMiSum (mi_xiao@qq.com)
 *  *
 *  * Permission is hereby granted, free of charge, to any person obtaining
 *  * a copy of this software and associated documentation files (the
 *  * 'Software'), to deal in the Software without restriction, including
 *  *  without limitation the rights to use, copy, modify, merge, publish,
 *  *  distribute, sublicense, and/or sell copies of the Software, and to
 *  *  permit persons to whom the Software is furnished to do so, subject to
 *  *  the following conditions:
 *  *
 *  * The above copyright notice and this permission notice shall be
 *  *  included in all copies or substantial portions of the Software.
 *  *
 *  * THE SOFTWARE IS PROVIDED 'AS IS', WITHOUT WARRANTY OF ANY KIND,
 *  *  EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
 *  *  MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.
 *  *  IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY
 *  *  CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT,
 *  *  TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE
 *  *  SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 *
 *
 */

package io.github.xiaomisum.ryze;

import groovy.lang.Closure;
import groovy.lang.DelegatesTo;
import io.github.xiaomisum.ryze.builder.DefaultChildrenBuilder;
import io.github.xiaomisum.ryze.protocol.debug.sampler.DebugSampler;
import io.github.xiaomisum.ryze.protocol.email.sampler.EMailSampler;
import io.github.xiaomisum.ryze.protocol.http.sampler.HTTPSampler;
import io.github.xiaomisum.ryze.protocol.jdbc.sampler.JDBCSampler;
import io.github.xiaomisum.ryze.protocol.redis.sampler.RedisSampler;
import io.github.xiaomisum.ryze.support.Customizer;
import io.github.xiaomisum.ryze.testelement.TestElement;
import io.github.xiaomisum.ryze.testelement.TestSuite;
import io.github.xiaomisum.ryze.testelement.TestSuiteResult;
import io.github.xiaomisum.ryze.testelement.sampler.AbstractSampler;
import io.github.xiaomisum.ryze.testelement.sampler.SampleResult;

/**
 * 魔法盒子 提供函数式测试用例执行入口
 * <p>
 * <b>⚠️ 已废弃：</b>此类已标记为废弃，请使用 {@link Ryze} 类代替。
 * 所有方法已迁移至 Ryze，此类仅保留向后兼容的委托调用。
 * </p>
 *
 * @author xiaomi
 * @deprecated 请使用 {@link Ryze} 类代替。所有方法已迁移至 Ryze，此类仅保留向后兼容的委托调用。
 */
@Deprecated
public class MagicBox {

    /**
     * @deprecated 请使用 {@link Ryze#suite(Closure)} 代替
     */
    @Deprecated
    public static TestSuiteResult suite(@DelegatesTo(strategy = Closure.DELEGATE_ONLY, value = TestSuite.Builder.class) Closure<?> closure) {
        return Ryze.suite(closure);
    }

    /**
     * @deprecated 请使用 {@link Ryze#suite(String, Closure)} 代替
     */
    @Deprecated
    public static TestSuiteResult suite(String title,
                                        @DelegatesTo(strategy = Closure.DELEGATE_ONLY, value = TestSuite.Builder.class) Closure<?> closure) {
        return Ryze.suite(title, closure);
    }

    /**
     * @deprecated 请使用 {@link Ryze#suite(String, Closure, Customizer)} 代替
     */
    @Deprecated
    public static TestSuiteResult suite(String title,
                                        @DelegatesTo(strategy = Closure.DELEGATE_ONLY, value = TestSuite.Builder.class) Closure<?> closure,
                                        Customizer<DefaultChildrenBuilder> customizer) {
        return Ryze.suite(title, closure, customizer);
    }

    /**
     * @deprecated 请使用 {@link Ryze#suite(Customizer)} 代替
     */
    @Deprecated
    public static TestSuiteResult suite(Customizer<TestSuite.Builder> customizer) {
        return Ryze.suite(customizer);
    }

    /**
     * @deprecated 请使用 {@link Ryze#suite(String, Customizer)} 代替
     */
    @Deprecated
    public static TestSuiteResult suite(String title, Customizer<TestSuite.Builder> customizer) {
        return Ryze.suite(title, customizer);
    }

    /**
     * @deprecated 请使用 {@link Ryze#suite(String, Customizer, Customizer)} 代替
     */
    @Deprecated
    public static TestSuiteResult suite(String title, Customizer<TestSuite.Builder> customizer, Customizer<DefaultChildrenBuilder> children) {
        return Ryze.suite(title, customizer, children);
    }

    /**
     * @deprecated 请使用 {@link Ryze#http(Closure)} 代替
     */
    @Deprecated
    public static Result http(@DelegatesTo(strategy = Closure.DELEGATE_ONLY, value = HTTPSampler.Builder.class) Closure<?> closure) {
        return Ryze.http(closure);
    }

    /**
     * @deprecated 请使用 {@link Ryze#http(String, Closure)} 代替
     */
    @Deprecated
    public static Result http(String title,
                              @DelegatesTo(strategy = Closure.DELEGATE_ONLY, value = HTTPSampler.Builder.class) Closure<?> closure) {
        return Ryze.http(title, closure);
    }

    /**
     * @deprecated 请使用 {@link Ryze#http(Customizer)} 代替
     */
    @Deprecated
    public static Result http(Customizer<HTTPSampler.Builder> customizer) {
        return Ryze.http(customizer);
    }

    /**
     * @deprecated 请使用 {@link Ryze#http(String, Customizer)} 代替
     */
    @Deprecated
    public static Result http(String title, Customizer<HTTPSampler.Builder> customizer) {
        return Ryze.http(title, customizer);
    }

    /**
     * @deprecated 请使用 {@link Ryze#jdbc(Closure)} 代替
     */
    @Deprecated
    public static Result jdbc(@DelegatesTo(strategy = Closure.DELEGATE_ONLY, value = JDBCSampler.Builder.class) Closure<?> closure) {
        return Ryze.jdbc(closure);
    }

    /**
     * @deprecated 请使用 {@link Ryze#jdbc(String, Closure)} 代替
     */
    @Deprecated
    public static Result jdbc(String title,
                              @DelegatesTo(strategy = Closure.DELEGATE_ONLY, value = JDBCSampler.Builder.class) Closure<?> closure) {
        return Ryze.jdbc(title, closure);
    }

    /**
     * @deprecated 请使用 {@link Ryze#jdbc(Customizer)} 代替
     */
    @Deprecated
    public static Result jdbc(Customizer<JDBCSampler.Builder> customizer) {
        return Ryze.jdbc(customizer);
    }

    /**
     * @deprecated 请使用 {@link Ryze#jdbc(String, Customizer)} 代替
     */
    @Deprecated
    public static Result jdbc(String title, Customizer<JDBCSampler.Builder> customizer) {
        return Ryze.jdbc(title, customizer);
    }

    /**
     * @deprecated 请使用 {@link Ryze#redis(Closure)} 代替
     */
    @Deprecated
    public static Result redis(@DelegatesTo(strategy = Closure.DELEGATE_ONLY, value = RedisSampler.Builder.class) Closure<?> closure) {
        return Ryze.redis(closure);
    }

    /**
     * @deprecated 请使用 {@link Ryze#redis(String, Closure)} 代替
     */
    @Deprecated
    public static Result redis(String title,
                               @DelegatesTo(strategy = Closure.DELEGATE_ONLY, value = RedisSampler.Builder.class) Closure<?> closure) {
        return Ryze.redis(title, closure);
    }

    /**
     * @deprecated 请使用 {@link Ryze#redis(Customizer)} 代替
     */
    @Deprecated
    public static Result redis(Customizer<RedisSampler.Builder> customizer) {
        return Ryze.redis(customizer);
    }

    /**
     * @deprecated 请使用 {@link Ryze#redis(String, Customizer)} 代替
     */
    @Deprecated
    public static Result redis(String title, Customizer<RedisSampler.Builder> customizer) {
        return Ryze.redis(title, customizer);
    }

    /**
     * @deprecated 请使用 {@link Ryze#runTest(String, TestElement)} 代替
     */
    @Deprecated
    public static <R extends Result> R runTest(String title, TestElement<R> testElement) {
        return Ryze.runTest(title, testElement);
    }

    /**
     * @deprecated 请使用 {@link Ryze#apply(AbstractSampler.Builder, Closure)} 代替
     */
    @Deprecated
    @SuppressWarnings({"rawtypes", "unchecked"})
    public static <B extends AbstractSampler.Builder, R extends SampleResult> R apply(
            @DelegatesTo.Target B builder,
            @DelegatesTo(strategy = Closure.DELEGATE_ONLY) Closure<?> closure) {
        return Ryze.apply(builder, closure);
    }

    /**
     * @deprecated 请使用 {@link Ryze#apply(String, AbstractSampler.Builder, Closure)} 代替
     */
    @Deprecated
    @SuppressWarnings({"rawtypes", "unchecked"})
    public static <B extends AbstractSampler.Builder, R extends SampleResult> R apply(
            String title,
            @DelegatesTo.Target B builder,
            @DelegatesTo(strategy = Closure.DELEGATE_ONLY) Closure<?> closure) {
        return Ryze.apply(title, builder, closure);
    }

    /**
     * @deprecated 请使用 {@link Ryze#apply(AbstractSampler.Builder, Customizer)} 代替
     */
    @Deprecated
    @SuppressWarnings({"rawtypes"})
    public static <B extends AbstractSampler.Builder, R extends SampleResult> R apply(
            B builder, Customizer<B> customizer) {
        return Ryze.apply(builder, customizer);
    }

    /**
     * @deprecated 请使用 {@link Ryze#apply(String, AbstractSampler.Builder, Customizer)} 代替
     */
    @Deprecated
    @SuppressWarnings({"rawtypes", "unchecked"})
    public static <B extends AbstractSampler.Builder, R extends SampleResult> R apply(
            String title, B builder, Customizer<B> customizer) {
        return Ryze.apply(title, builder, customizer);
    }

    /**
     * @deprecated 请使用 {@link Ryze#debug(Closure)} 代替
     */
    @Deprecated
    public static Result debug(@DelegatesTo(strategy = Closure.DELEGATE_ONLY, value = DebugSampler.Builder.class) Closure<?> closure) {
        return Ryze.debug(closure);
    }

    /**
     * @deprecated 请使用 {@link Ryze#debug(String, Closure)} 代替
     */
    @Deprecated
    public static Result debug(String title,
                               @DelegatesTo(strategy = Closure.DELEGATE_ONLY, value = DebugSampler.Builder.class) Closure<?> closure) {
        return Ryze.debug(title, closure);
    }

    /**
     * @deprecated 请使用 {@link Ryze#debug(Customizer)} 代替
     */
    @Deprecated
    public static Result debug(Customizer<DebugSampler.Builder> customizer) {
        return Ryze.debug(customizer);
    }

    /**
     * @deprecated 请使用 {@link Ryze#debug(String, Customizer)} 代替
     */
    @Deprecated
    public static Result debug(String title, Customizer<DebugSampler.Builder> customizer) {
        return Ryze.debug(title, customizer);
    }

    /**
     * @deprecated 请使用 {@link Ryze#mail(Closure)} 代替
     */
    @Deprecated
    public static Result mail(@DelegatesTo(strategy = Closure.DELEGATE_ONLY, value = EMailSampler.Builder.class) Closure<?> closure) {
        return Ryze.mail(closure);
    }

    /**
     * @deprecated 请使用 {@link Ryze#mail(String, Closure)} 代替
     */
    @Deprecated
    public static Result mail(String title,
                              @DelegatesTo(strategy = Closure.DELEGATE_ONLY, value = EMailSampler.Builder.class) Closure<?> closure) {
        return Ryze.mail(title, closure);
    }

    /**
     * @deprecated 请使用 {@link Ryze#mail(Customizer)} 代替
     */
    @Deprecated
    public static Result mail(Customizer<EMailSampler.Builder> customizer) {
        return Ryze.mail(customizer);
    }

    /**
     * @deprecated 请使用 {@link Ryze#mail(String, Customizer)} 代替
     */
    @Deprecated
    public static Result mail(String title, Customizer<EMailSampler.Builder> customizer) {
        return Ryze.mail(title, customizer);
    }
}
