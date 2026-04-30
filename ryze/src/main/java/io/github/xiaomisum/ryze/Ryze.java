/*
 *
 *  * The MIT License (MIT)
 *  *
 *  * Copyright (c) 2025.  Lorem XiaoMiSum (mi_xiao@qq.com)
 *  *
 *  * Permission is hereby granted, free of charge, to any person obtaining
 *  * a copy of this software and associated documentation files (the
 *  * 'Software'), to deal in the Software without restriction, including
 *  * without limitation the rights to use, copy, modify, merge, publish,
 *  * distribute, sublicense, and/or sell copies of the Software, and to
 *  * permit persons to whom the Software is furnished to do so, subject to
 *  * the following conditions:
 *  *
 *  * The above copyright notice and this permission notice shall be
 *  * included in all copies or substantial portions of the Software.
 *  *
 *  * THE SOFTWARE IS PROVIDED 'AS IS', WITHOUT WARRANTY OF ANY KIND,
 *  * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
 *  * MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.
 *  * IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY
 *  * CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT,
 *  * TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE
 *  * SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 *
 *
 */

package io.github.xiaomisum.ryze;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import groovy.lang.Closure;
import groovy.lang.DelegatesTo;
import io.github.xiaomisum.ryze.builder.DefaultChildrenBuilder;
import io.github.xiaomisum.ryze.protocol.debug.sampler.DebugSampler;
import io.github.xiaomisum.ryze.protocol.email.sampler.EMailSampler;
import io.github.xiaomisum.ryze.protocol.http.sampler.HTTPSampler;
import io.github.xiaomisum.ryze.protocol.jdbc.sampler.JDBCSampler;
import io.github.xiaomisum.ryze.protocol.redis.sampler.RedisSampler;
import io.github.xiaomisum.ryze.report.AllureTestCaseHelper;
import io.github.xiaomisum.ryze.support.Customizer;
import io.github.xiaomisum.ryze.support.dataloader.TestDataLoaderChain;
import io.github.xiaomisum.ryze.support.groovy.Groovy;
import io.github.xiaomisum.ryze.testelement.AbstractTestElement;
import io.github.xiaomisum.ryze.testelement.TestElement;
import io.github.xiaomisum.ryze.testelement.TestSuite;
import io.github.xiaomisum.ryze.testelement.TestSuiteResult;
import io.github.xiaomisum.ryze.testelement.sampler.AbstractSampler;
import io.github.xiaomisum.ryze.testelement.sampler.SampleResult;
import org.apache.commons.lang3.StringUtils;

import java.util.Map;

/**
 * Ryze测试框架统一入口类
 * <p>
 * 该类是Ryze测试框架的主要入口，提供两种测试执行方式：
 * <ol>
 *   <li><b>数据驱动模式</b>：通过文件路径、Map数据启动测试（start方法）</li>
 *   <li><b>函数式编程模式</b>：通过闭包或自定义器构建并执行测试元素（suite/http/jdbc/redis等方法）</li>
 * </ol>
 * </p>
 * <p>
 * 主要功能包括：
 * <ul>
 *   <li>提供多种方式启动测试（文件路径、Map数据、JsonTree对象）</li>
 *   <li>管理测试会话生命周期</li>
 *   <li>解析测试用例并执行测试</li>
 *   <li>测试套件(TestSuite)的构建与执行</li>
 *   <li>HTTP/JDBC/Redis/Debug/Mail等协议测试的构建与执行</li>
 *   <li>通用协议取样器的apply方法</li>
 * </ul>
 * </p>
 *
 * @author xiaomi
 */
@SuppressWarnings({"unchecked", "rawtypes"})
public class Ryze {


    private final Map<String, Object> testcase;

    /**
     * 构造函数，初始化测试用例
     *
     * @param testcase 标准化后的测试用例对象
     */
    public Ryze(Map<String, Object> testcase) {
        this.testcase = testcase;
    }

    /**
     * 通过文件路径启动测试执行
     * <p>
     * 执行流程：
     * 1. 从指定文件路径加载测试数据
     * 2. 调用重载方法继续执行
     * </p>
     *
     * @param filePath 测试用例文件路径
     * @return 测试执行结果
     */
    public static Result start(String filePath) {
        JSONObject testcase = TestDataLoaderChain.loadTestData(filePath, JSONObject.class);
        return start(testcase);
    }

    /**
     * 通过Map数据启动测试执行
     * <p>
     * 执行流程：
     * 1. 将Map数据转换为JsonTree对象
     * 2. 调用重载方法继续执行
     * </p>
     *
     * @param testcase Map格式的测试用例数据
     * @return 测试执行结果
     */
    public static Result start(Map<String, Object> testcase) {
        return new Ryze(testcase).runTest();
    }

    /**
     * 执行测试的核心方法
     * <p>
     * 执行流程：
     * 1. 在 Allure testcase 生命周期内执行测试（TestNG 模式下透传，Main/API 模式下由 Ryze 自建）
     * 2. 将测试用例数据转换为测试元素对象
     * 3. 获取或创建测试会话并执行测试
     * </p>
     *
     * @return 测试执行结果
     */
    private Result runTest() {
        try {
            return AllureTestCaseHelper.runInTestCase(() -> {
                var json = JSON.toJSONString(testcase);
                return SessionRunner.getSessionIfNoneCreateNew().runTest(JSON.parseObject(json, TestElement.class));
            });
        } finally {
            SessionRunner.removeSession();
        }
    }

    // ==================== 函数式编程模式方法（从 MagicBox 迁移） ====================

    /**
     * 执行测试套件，使用闭包方式构建测试套件配置
     *
     * @param closure Groovy闭包，用于配置测试套件
     * @return 测试套件执行结果
     */
    public static TestSuiteResult suite(@DelegatesTo(strategy = Closure.DELEGATE_ONLY, value = TestSuite.Builder.class) Closure<?> closure) {
        return suite("", closure);
    }

    /**
     * 执行测试套件，使用闭包方式构建测试套件配置并指定标题
     *
     * @param title   测试套件标题
     * @param closure Groovy闭包，用于配置测试套件
     * @return 测试套件执行结果
     */
    public static TestSuiteResult suite(String title,
                                        @DelegatesTo(strategy = Closure.DELEGATE_ONLY, value = TestSuite.Builder.class) Closure<?> closure) {
        return suite(title, closure, null);
    }

    /**
     * 执行测试套件，使用闭包方式构建测试套件配置并指定标题和子元素自定义器
     *
     * @param title      测试套件标题
     * @param closure    Groovy闭包，用于配置测试套件
     * @param customizer 子元素自定义器
     * @return 测试套件执行结果
     */
    public static TestSuiteResult suite(String title,
                                        @DelegatesTo(strategy = Closure.DELEGATE_ONLY, value = TestSuite.Builder.class) Closure<?> closure,
                                        Customizer<DefaultChildrenBuilder> customizer) {
        var builder = TestSuite.builder();
        Groovy.call(closure, builder);
        if (customizer != null) {
            var children = DefaultChildrenBuilder.builder();
            customizer.customize(children);
            builder.children(children.build());
        }
        return runTest(title, builder.build());
    }

    /**
     * 执行测试套件，使用自定义器方式构建测试套件配置
     *
     * @param customizer 测试套件自定义器
     * @return 测试套件执行结果
     */
    public static TestSuiteResult suite(Customizer<TestSuite.Builder> customizer) {
        return suite("", customizer);
    }

    /**
     * 执行测试套件，使用自定义器方式构建测试套件配置并指定标题
     *
     * @param title      测试套件标题
     * @param customizer 测试套件自定义器
     * @return 测试套件执行结果
     */
    public static TestSuiteResult suite(String title, Customizer<TestSuite.Builder> customizer) {
        return suite(title, customizer, null);
    }

    /**
     * 执行测试套件，使用自定义器方式构建测试套件配置并指定标题和子元素自定义器
     *
     * @param title      测试套件标题
     * @param customizer 测试套件自定义器
     * @param children   子元素自定义器
     * @return 测试套件执行结果
     */
    public static TestSuiteResult suite(String title, Customizer<TestSuite.Builder> customizer, Customizer<DefaultChildrenBuilder> children) {
        var builder = TestSuite.builder();
        customizer.customize(builder);
        if (children != null) {
            var localChildren = DefaultChildrenBuilder.builder();
            children.customize(localChildren);
            builder.children(localChildren.build());
        }
        return runTest(title, builder.build());
    }

    /**
     * 执行HTTP测试，使用闭包方式构建HTTP取样器配置
     *
     * @param closure Groovy闭包，用于配置HTTP取样器
     * @return HTTP测试执行结果
     */
    public static Result http(@DelegatesTo(strategy = Closure.DELEGATE_ONLY, value = HTTPSampler.Builder.class) Closure<?> closure) {
        return http("", closure);
    }

    /**
     * 执行HTTP测试，使用闭包方式构建HTTP取样器配置并指定标题
     *
     * @param title   HTTP测试标题
     * @param closure Groovy闭包，用于配置HTTP取样器
     * @return HTTP测试执行结果
     */
    public static Result http(String title,
                              @DelegatesTo(strategy = Closure.DELEGATE_ONLY, value = HTTPSampler.Builder.class) Closure<?> closure) {
        var builder = HTTPSampler.builder();
        Groovy.call(closure, builder);
        return runTest(title, builder.build());
    }

    /**
     * 执行HTTP测试，使用自定义器方式构建HTTP取样器配置
     *
     * @param customizer HTTP取样器自定义器
     * @return HTTP测试执行结果
     */
    public static Result http(Customizer<HTTPSampler.Builder> customizer) {
        return http("", customizer);
    }

    /**
     * 执行HTTP测试，使用自定义器方式构建HTTP取样器配置并指定标题
     *
     * @param title      HTTP测试标题
     * @param customizer HTTP取样器自定义器
     * @return HTTP测试执行结果
     */
    public static Result http(String title, Customizer<HTTPSampler.Builder> customizer) {
        var builder = HTTPSampler.builder();
        customizer.customize(builder);
        return runTest(title, builder.build());
    }

    /**
     * 执行JDBC数据库测试，使用闭包方式构建JDBC取样器配置
     *
     * @param closure Groovy闭包，用于配置JDBC取样器
     * @return JDBC测试执行结果
     */
    public static Result jdbc(@DelegatesTo(strategy = Closure.DELEGATE_ONLY, value = JDBCSampler.Builder.class) Closure<?> closure) {
        return jdbc("", closure);
    }

    /**
     * 执行JDBC数据库测试，使用闭包方式构建JDBC取样器配置并指定标题
     *
     * @param title   JDBC测试标题
     * @param closure Groovy闭包，用于配置JDBC取样器
     * @return JDBC测试执行结果
     */
    public static Result jdbc(String title,
                              @DelegatesTo(strategy = Closure.DELEGATE_ONLY, value = JDBCSampler.Builder.class) Closure<?> closure) {
        var builder = JDBCSampler.builder();
        Groovy.call(closure, builder);
        return runTest(title, builder.build());
    }

    /**
     * 执行JDBC数据库测试，使用自定义器方式构建JDBC取样器配置
     *
     * @param customizer JDBC取样器自定义器
     * @return JDBC测试执行结果
     */
    public static Result jdbc(Customizer<JDBCSampler.Builder> customizer) {
        return jdbc("", customizer);
    }

    /**
     * 执行JDBC数据库测试，使用自定义器方式构建JDBC取样器配置并指定标题
     *
     * @param title      JDBC测试标题
     * @param customizer JDBC取样器自定义器
     * @return JDBC测试执行结果
     */
    public static Result jdbc(String title, Customizer<JDBCSampler.Builder> customizer) {
        var builder = JDBCSampler.builder();
        customizer.customize(builder);
        return runTest(title, builder.build());
    }

    /**
     * 执行Redis测试，使用闭包方式构建Redis取样器配置
     *
     * @param closure Groovy闭包，用于配置Redis取样器
     * @return Redis测试执行结果
     */
    public static Result redis(@DelegatesTo(strategy = Closure.DELEGATE_ONLY, value = RedisSampler.Builder.class) Closure<?> closure) {
        return redis("", closure);
    }

    /**
     * 执行Redis测试，使用闭包方式构建Redis取样器配置并指定标题
     *
     * @param title   Redis测试标题
     * @param closure Groovy闭包，用于配置Redis取样器
     * @return Redis测试执行结果
     */
    public static Result redis(String title,
                               @DelegatesTo(strategy = Closure.DELEGATE_ONLY, value = RedisSampler.Builder.class) Closure<?> closure) {
        var builder = RedisSampler.builder();
        Groovy.call(closure, builder);
        return runTest(title, builder.build());
    }

    /**
     * 执行Redis测试，使用自定义器方式构建Redis取样器配置
     *
     * @param customizer Redis取样器自定义器
     * @return Redis测试执行结果
     */
    public static Result redis(Customizer<RedisSampler.Builder> customizer) {
        return redis("", customizer);
    }

    /**
     * 执行Redis测试，使用自定义器方式构建Redis取样器配置并指定标题
     *
     * @param title      Redis测试标题
     * @param customizer Redis取样器自定义器
     * @return Redis测试执行结果
     */
    public static Result redis(String title, Customizer<RedisSampler.Builder> customizer) {
        var builder = RedisSampler.builder();
        customizer.customize(builder);
        return runTest(title, builder.build());
    }

    /**
     * 执行测试元素的核心方法
     *
     * @param title       测试标题
     * @param testElement 要执行的测试元素
     * @param <R>         测试结果类型
     * @return 测试执行结果
     */
    public static <R extends Result> R runTest(String title, TestElement<R> testElement) {
        if (testElement instanceof AbstractTestElement<?, ?, R> ele && StringUtils.isNotBlank(title)) {
            ele.setTitle(title);
        }
        return SessionRunner.getSession().runTest(testElement);
    }

    /**
     * 执行任意协议的取样器测试，使用闭包方式构建取样器配置（通用入口）
     *
     * @param builder 具体协议取样器的 Builder 实例
     * @param closure Groovy 闭包，用于配置取样器
     * @param <B>     Builder 类型
     * @param <R>     取样结果类型
     * @return 取样器执行结果
     */
    public static <B extends AbstractSampler.Builder, R extends SampleResult> R apply(
            @DelegatesTo.Target B builder,
            @DelegatesTo(strategy = Closure.DELEGATE_ONLY) Closure<?> closure) {
        return apply("", builder, closure);
    }

    /**
     * 执行任意协议的取样器测试，使用闭包方式构建取样器配置并指定标题（通用入口）
     *
     * @param title   测试标题
     * @param builder 具体协议取样器的 Builder 实例
     * @param closure Groovy 闭包，用于配置取样器
     * @param <B>     Builder 类型
     * @param <R>     取样结果类型
     * @return 取样器执行结果
     */
    public static <B extends AbstractSampler.Builder, R extends SampleResult> R apply(
            String title,
            @DelegatesTo.Target B builder,
            @DelegatesTo(strategy = Closure.DELEGATE_ONLY) Closure<?> closure) {
        Groovy.call(closure, builder);
        return runTest(title, (TestElement<R>) builder.build());
    }

    /**
     * 执行任意协议的取样器测试，使用自定义器方式构建取样器配置（通用入口）
     *
     * @param builder    具体协议取样器的 Builder 实例
     * @param customizer 取样器自定义器
     * @param <B>        Builder 类型
     * @param <R>        取样结果类型
     * @return 取样器执行结果
     */
    public static <B extends AbstractSampler.Builder, R extends SampleResult> R apply(
            B builder, Customizer<B> customizer) {
        return apply("", builder, customizer);
    }

    /**
     * 执行任意协议的取样器测试，使用自定义器方式构建取样器配置并指定标题（通用入口）
     *
     * @param title      测试标题
     * @param builder    具体协议取样器的 Builder 实例
     * @param customizer 取样器自定义器
     * @param <B>        Builder 类型
     * @param <R>        取样结果类型
     * @return 取样器执行结果
     */
    public static <B extends AbstractSampler.Builder, R extends SampleResult> R apply(
            String title, B builder, Customizer<B> customizer) {
        customizer.customize(builder);
        return runTest(title, (TestElement<R>) builder.build());
    }

    /**
     * 执行调试测试，使用闭包方式构建调试取样器配置
     *
     * @param closure Groovy闭包，用于配置调试取样器
     * @return 调试测试执行结果
     */
    public static Result debug(@DelegatesTo(strategy = Closure.DELEGATE_ONLY, value = DebugSampler.Builder.class) Closure<?> closure) {
        return debug("", closure);
    }

    /**
     * 执行调试测试，使用闭包方式构建调试取样器配置并指定标题
     *
     * @param title   调试测试标题
     * @param closure Groovy闭包，用于配置调试取样器
     * @return 调试测试执行结果
     */
    public static Result debug(String title,
                               @DelegatesTo(strategy = Closure.DELEGATE_ONLY, value = DebugSampler.Builder.class) Closure<?> closure) {
        var builder = DebugSampler.builder();
        Groovy.call(closure, builder);
        return runTest(title, builder.build());
    }

    /**
     * 执行调试测试，使用自定义器方式构建调试取样器配置
     *
     * @param customizer 调试取样器自定义器
     * @return 调试测试执行结果
     */
    public static Result debug(Customizer<DebugSampler.Builder> customizer) {
        return debug("", customizer);
    }

    /**
     * 执行调试测试，使用自定义器方式构建调试取样器配置并指定标题
     *
     * @param title      调试测试标题
     * @param customizer 调试取样器自定义器
     * @return 调试测试执行结果
     */
    public static Result debug(String title, Customizer<DebugSampler.Builder> customizer) {
        var builder = DebugSampler.builder();
        customizer.customize(builder);
        return runTest(title, builder.build());
    }

    /**
     * 执行邮件测试，使用闭包方式构建邮件取样器配置
     *
     * @param closure Groovy闭包，用于配置邮件取样器
     * @return 邮件测试执行结果
     */
    public static Result mail(@DelegatesTo(strategy = Closure.DELEGATE_ONLY, value = EMailSampler.Builder.class) Closure<?> closure) {
        return mail("", closure);
    }

    /**
     * 执行邮件测试，使用闭包方式构建邮件取样器配置并指定标题
     *
     * @param title   邮件测试标题
     * @param closure Groovy闭包，用于配置邮件取样器
     * @return 邮件测试执行结果
     */
    public static Result mail(String title,
                              @DelegatesTo(strategy = Closure.DELEGATE_ONLY, value = EMailSampler.Builder.class) Closure<?> closure) {
        var builder = EMailSampler.builder();
        Groovy.call(closure, builder);
        return runTest(title, builder.build());
    }

    /**
     * 执行邮件测试，使用自定义器方式构建邮件取样器配置
     *
     * @param customizer 邮件取样器自定义器
     * @return 邮件测试执行结果
     */
    public static Result mail(Customizer<EMailSampler.Builder> customizer) {
        return mail("", customizer);
    }

    /**
     * 执行邮件测试，使用自定义器方式构建邮件取样器配置并指定标题
     *
     * @param title      邮件测试标题
     * @param customizer 邮件取样器自定义器
     * @return 邮件测试执行结果
     */
    public static Result mail(String title, Customizer<EMailSampler.Builder> customizer) {
        var builder = EMailSampler.builder();
        customizer.customize(builder);
        return runTest(title, builder.build());
    }
}