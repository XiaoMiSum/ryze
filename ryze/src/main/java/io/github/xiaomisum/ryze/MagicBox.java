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

import groovy.lang.Closure;
import groovy.lang.DelegatesTo;
import io.github.xiaomisum.ryze.builder.DefaultChildrenBuilder;
import io.github.xiaomisum.ryze.testelement.AbstractTestElement;
import io.github.xiaomisum.ryze.testelement.TestElement;
import io.github.xiaomisum.ryze.testelement.TestSuite;
import io.github.xiaomisum.ryze.testelement.TestSuiteResult;
import io.github.xiaomisum.ryze.protocol.debug.sampler.DebugSampler;
import io.github.xiaomisum.ryze.protocol.http.sampler.HTTPSampler;
import io.github.xiaomisum.ryze.protocol.jdbc.sampler.JDBCSampler;
import io.github.xiaomisum.ryze.protocol.redis.sampler.RedisSampler;
import io.github.xiaomisum.ryze.support.Customizer;
import io.github.xiaomisum.ryze.support.groovy.Groovy;
import org.apache.commons.lang3.StringUtils;

/**
 * 魔法盒子 提供函数式测试用例执行入口
 * <p>
 * MagicBox类是Ryze测试框架的核心入口类之一，提供了多种便捷方法来执行不同类型的测试用例。
 * 它支持通过函数式编程风格（使用闭包或自定义器）来构建和执行测试元素，包括测试套件(TestSuite)和各种协议的取样器(Sampler)。
 * </p>
 * <p>
 * 主要功能包括：
 * <ul>
 *   <li>测试套件(TestSuite)的构建与执行</li>
 *   <li>HTTP协议测试的构建与执行</li>
 *   <li>JDBC数据库测试的构建与执行</li>
 *   <li>Redis测试的构建与执行</li>
 *   <li>调试取样器的构建与执行</li>
 * </ul>
 * </p>
 *
 * @author xiaomi
 */
public class MagicBox {

    /**
     * 执行测试套件，使用闭包方式构建测试套件配置
     * <p>
     * 该方法通过Groovy闭包来配置测试套件，使用默认空标题。闭包中的方法调用会被委托给[TestSuite.Builder](TestSuite.Builder)对象，
     * 允许以声明式的方式定义测试套件的结构和配置。执行完成后返回[TestSuiteResult](TestSuiteResult)结果对象，
     * 包含测试套件及其所有子元素的执行结果信息。
     * </p>
     *
     * @param closure Groovy闭包，用于配置测试套件，闭包中的方法调用会被委托给[TestSuite.Builder](TestSuite.Builder)
     * @return 测试套件执行结果，包含所有子测试元素的结果
     * @see #suite(String, Closure)
     * @see TestSuite.Builder
     * @see TestSuiteResult
     */
    public static TestSuiteResult suite(@DelegatesTo(strategy = Closure.DELEGATE_ONLY, value = TestSuite.Builder.class) Closure<?> closure) {
        return suite("", closure);
    }

    /**
     * 执行测试套件，使用闭包方式构建测试套件配置并指定标题
     * <p>
     * 该方法通过Groovy闭包来配置测试套件，允许指定测试套件标题。闭包中的方法调用会被委托给[TestSuite.Builder](TestSuite.Builder)对象，
     * 允许以声明式的方式定义测试套件的结构和配置。执行完成后返回[TestSuiteResult](TestSuiteResult)结果对象，
     * 包含测试套件及其所有子元素的执行结果信息。
     * </p>
     *
     * @param title   测试套件标题
     * @param closure Groovy闭包，用于配置测试套件，闭包中的方法调用会被委托给[TestSuite.Builder](TestSuite.Builder)
     * @return 测试套件执行结果，包含所有子测试元素的结果
     * @see #suite(String, Closure, Customizer)
     * @see TestSuite.Builder
     * @see TestSuiteResult
     */
    public static TestSuiteResult suite(String title,
                                        @DelegatesTo(strategy = Closure.DELEGATE_ONLY, value = TestSuite.Builder.class) Closure<?> closure) {
        return suite(title, closure, null);
    }

    /**
     * 执行测试套件，使用闭包方式构建测试套件配置并指定标题和子元素自定义器
     * <p>
     * 该方法通过Groovy闭包来配置测试套件，并可选地通过自定义器来配置子元素。闭包中的方法调用会被委托给[TestSuite.Builder](TestSuite.Builder)对象，
     * 允许以声明式的方式定义测试套件的结构和配置。执行完成后返回[TestSuiteResult](TestSuiteResult)结果对象，
     * 包含测试套件及其所有子元素的执行结果信息。
     * </p>
     * <p>
     * 业务流程：
     * <ol>
     *   <li>创建[TestSuite.Builder](TestSuite.Builder)实例</li>
     *   <li>通过Groovy闭包配置测试套件</li>
     *   <li>如果提供了子元素自定义器，则使用它来配置子元素</li>
     *   <li>构建测试套件对象</li>
     *   <li>执行测试套件并返回结果</li>
     * </ol>
     * </p>
     *
     * @param title      测试套件标题
     * @param closure    Groovy闭包，用于配置测试套件，闭包中的方法调用会被委托给[TestSuite.Builder](TestSuite.Builder)
     * @param customizer 子元素自定义器，用于配置测试套件的子元素列表
     * @return 测试套件执行结果，包含所有子测试元素的结果
     * @see TestSuite.Builder
     * @see TestSuiteResult
     * @see DefaultChildrenBuilder
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
     * <p>
     * 该方法通过[Customizer](Customizer)来自定义测试套件配置，使用默认空标题。自定义器中的方法调用会直接作用于[TestSuite.Builder](TestSuite.Builder)对象，
     * 允许以编程方式定义测试套件的结构和配置。执行完成后返回[TestSuiteResult](TestSuiteResult)结果对象，
     * 包含测试套件及其所有子元素的执行结果信息。
     * </p>
     *
     * @param customizer 测试套件自定义器，用于配置测试套件
     * @return 测试套件执行结果，包含所有子测试元素的结果
     * @see #suite(String, Customizer)
     * @see TestSuite.Builder
     * @see TestSuiteResult
     */
    public static TestSuiteResult suite(Customizer<TestSuite.Builder> customizer) {
        return suite("", customizer);
    }

    /**
     * 执行测试套件，使用自定义器方式构建测试套件配置并指定标题
     * <p>
     * 该方法通过[Customizer](Customizer)来自定义测试套件配置，并指定测试套件标题。自定义器中的方法调用会直接作用于[TestSuite.Builder](TestSuite.Builder)对象，
     * 允许以编程方式定义测试套件的结构和配置。执行完成后返回[TestSuiteResult](TestSuiteResult)结果对象，
     * 包含测试套件及其所有子元素的执行结果信息。
     * </p>
     *
     * @param title      测试套件标题
     * @param customizer 测试套件自定义器，用于配置测试套件
     * @return 测试套件执行结果，包含所有子测试元素的结果
     * @see #suite(String, Customizer, Customizer)
     * @see TestSuite.Builder
     * @see TestSuiteResult
     */
    public static TestSuiteResult suite(String title, Customizer<TestSuite.Builder> customizer) {
        return suite(title, customizer, null);
    }

    /**
     * 执行测试套件，使用自定义器方式构建测试套件配置并指定标题和子元素自定义器
     * <p>
     * 该方法通过[Customizer](Customizer)来自定义测试套件配置，并可选地通过另一个自定义器来配置子元素。自定义器中的方法调用会直接作用于[TestSuite.Builder](TestSuite.Builder)对象，
     * 允许以编程方式定义测试套件的结构和配置。执行完成后返回[TestSuiteResult](TestSuiteResult)结果对象，
     * 包含测试套件及其所有子元素的执行结果信息。
     * </p>
     * <p>
     * 业务流程：
     * <ol>
     *   <li>创建[TestSuite.Builder](TestSuite.Builder)实例</li>
     *   <li>通过自定义器配置测试套件</li>
     *   <li>如果提供了子元素自定义器，则使用它来配置子元素</li>
     *   <li>构建测试套件对象</li>
     *   <li>执行测试套件并返回结果</li>
     * </ol>
     * </p>
     *
     * @param title      测试套件标题
     * @param customizer 测试套件自定义器，用于配置测试套件
     * @param children   子元素自定义器，用于配置测试套件的子元素列表
     * @return 测试套件执行结果，包含所有子测试元素的结果
     * @see TestSuite.Builder
     * @see TestSuiteResult
     * @see DefaultChildrenBuilder
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
     * <p>
     * 该方法通过Groovy闭包来配置HTTP取样器，使用默认空标题。闭包中的方法调用会被委托给[HTTPSampler.Builder](HTTPSampler.Builder)对象，
     * 允许以声明式的方式定义HTTP请求的URL、方法、头信息、参数等配置。执行完成后返回[Result](Result)结果对象，
     * 包含HTTP请求的执行结果信息，如响应状态码、响应头、响应体等数据。
     * </p>
     *
     * @param closure Groovy闭包，用于配置HTTP取样器，闭包中的方法调用会被委托给[HTTPSampler.Builder](HTTPSampler.Builder)
     * @return HTTP测试执行结果
     * @see #http(String, Closure)
     * @see HTTPSampler.Builder
     * @see Result
     */
    public static Result http(@DelegatesTo(strategy = Closure.DELEGATE_ONLY, value = HTTPSampler.Builder.class) Closure<?> closure) {
        return http("", closure);
    }

    /**
     * 执行HTTP测试，使用闭包方式构建HTTP取样器配置并指定标题
     * <p>
     * 该方法通过Groovy闭包来配置HTTP取样器，并指定测试标题。闭包中的方法调用会被委托给[HTTPSampler.Builder](HTTPSampler.Builder)对象，
     * 允许以声明式的方式定义HTTP请求的URL、方法、头信息、参数等配置。执行完成后返回[Result](Result)结果对象，
     * 包含HTTP请求的执行结果信息，如响应状态码、响应头、响应体等数据。
     * </p>
     * <p>
     * 业务流程：
     * <ol>
     *   <li>创建[HTTPSampler.Builder](HTTPSampler.Builder)实例</li>
     *   <li>通过Groovy闭包配置HTTP取样器</li>
     *   <li>构建HTTP取样器对象</li>
     *   <li>执行HTTP测试并返回结果</li>
     * </ol>
     * </p>
     *
     * @param title   HTTP测试标题
     * @param closure Groovy闭包，用于配置HTTP取样器，闭包中的方法调用会被委托给[HTTPSampler.Builder](HTTPSampler.Builder)
     * @return HTTP测试执行结果
     * @see HTTPSampler.Builder
     * @see Result
     */
    public static Result http(String title,
                              @DelegatesTo(strategy = Closure.DELEGATE_ONLY, value = HTTPSampler.Builder.class) Closure<?> closure) {
        var builder = HTTPSampler.builder();
        Groovy.call(closure, builder);
        return MagicBox.runTest(title, builder.build());
    }

    /**
     * 执行HTTP测试，使用自定义器方式构建HTTP取样器配置
     * <p>
     * 该方法通过[Customizer](Customizer)来自定义HTTP取样器配置，使用默认空标题。自定义器中的方法调用会直接作用于[HTTPSampler.Builder](HTTPSampler.Builder)对象，
     * 允许以编程方式定义HTTP请求的URL、方法、头信息、参数等配置。执行完成后返回[Result](Result)结果对象，
     * 包含HTTP请求的执行结果信息，如响应状态码、响应头、响应体等数据。
     * </p>
     *
     * @param customizer HTTP取样器自定义器，用于配置HTTP取样器
     * @return HTTP测试执行结果
     * @see #http(String, Customizer)
     * @see HTTPSampler.Builder
     * @see Result
     */
    public static Result http(Customizer<HTTPSampler.Builder> customizer) {
        return http("", customizer);
    }

    /**
     * 执行HTTP测试，使用自定义器方式构建HTTP取样器配置并指定标题
     * <p>
     * 该方法通过[Customizer](Customizer)来自定义HTTP取样器配置，并指定测试标题。自定义器中的方法调用会直接作用于[HTTPSampler.Builder](HTTPSampler.Builder)对象，
     * 允许以编程方式定义HTTP请求的URL、方法、头信息、参数等配置。执行完成后返回[Result](Result)结果对象，
     * 包含HTTP请求的执行结果信息，如响应状态码、响应头、响应体等数据。
     * </p>
     * <p>
     * 业务流程：
     * <ol>
     *   <li>创建[HTTPSampler.Builder](HTTPSampler.Builder)实例</li>
     *   <li>通过自定义器配置HTTP取样器</li>
     *   <li>构建HTTP取样器对象</li>
     *   <li>执行HTTP测试并返回结果</li>
     * </ol>
     * </p>
     *
     * @param title      HTTP测试标题
     * @param customizer HTTP取样器自定义器，用于配置HTTP取样器
     * @return HTTP测试执行结果
     * @see HTTPSampler.Builder
     * @see Result
     */
    public static Result http(String title, Customizer<HTTPSampler.Builder> customizer) {
        var builder = HTTPSampler.builder();
        customizer.customize(builder);
        return MagicBox.runTest(title, builder.build());
    }

    /**
     * 执行JDBC数据库测试，使用闭包方式构建JDBC取样器配置
     * <p>
     * 该方法通过Groovy闭包来配置JDBC取样器，使用默认空标题。闭包中的方法调用会被委托给[JDBCSampler.Builder](JDBCSampler.Builder)对象，
     * 允许以声明式的方式定义数据库连接信息和SQL语句等配置。执行完成后返回[Result](Result)结果对象，
     * 包含SQL执行的执行结果信息，如查询结果、更新影响行数等数据。
     * </p>
     *
     * @param closure Groovy闭包，用于配置JDBC取样器，闭包中的方法调用会被委托给[JDBCSampler.Builder](JDBCSampler.Builder)
     * @return JDBC测试执行结果
     * @see #jdbc(String, Closure)
     * @see JDBCSampler.Builder
     * @see Result
     */
    public static Result jdbc(@DelegatesTo(strategy = Closure.DELEGATE_ONLY, value = JDBCSampler.Builder.class) Closure<?> closure) {
        return jdbc("", closure);
    }

    /**
     * 执行JDBC数据库测试，使用闭包方式构建JDBC取样器配置并指定标题
     * <p>
     * 该方法通过Groovy闭包来配置JDBC取样器，并指定测试标题。闭包中的方法调用会被委托给[JDBCSampler.Builder](JDBCSampler.Builder)对象，
     * 允许以声明式的方式定义数据库连接信息和SQL语句等配置。执行完成后返回[Result](Result)结果对象，
     * 包含SQL执行的执行结果信息，如查询结果、更新影响行数等数据。
     * </p>
     * <p>
     * 业务流程：
     * <ol>
     *   <li>创建[JDBCSampler.Builder](JDBCSampler.Builder)实例</li>
     *   <li>通过Groovy闭包配置JDBC取样器</li>
     *   <li>构建JDBC取样器对象</li>
     *   <li>执行JDBC测试并返回结果</li>
     * </ol>
     * </p>
     *
     * @param title   JDBC测试标题
     * @param closure Groovy闭包，用于配置JDBC取样器，闭包中的方法调用会被委托给[JDBCSampler.Builder](JDBCSampler.Builder)
     * @return JDBC测试执行结果
     * @see JDBCSampler.Builder
     * @see Result
     */
    public static Result jdbc(String title,
                              @DelegatesTo(strategy = Closure.DELEGATE_ONLY, value = JDBCSampler.Builder.class) Closure<?> closure) {
        var builder = JDBCSampler.builder();
        Groovy.call(closure, builder);
        return MagicBox.runTest(title, builder.build());
    }

    /**
     * 执行JDBC数据库测试，使用自定义器方式构建JDBC取样器配置
     * <p>
     * 该方法通过[Customizer](Customizer)来自定义JDBC取样器配置，使用默认空标题。自定义器中的方法调用会直接作用于[JDBCSampler.Builder](JDBCSampler.Builder)对象，
     * 允许以编程方式定义数据库连接信息和SQL语句等配置。执行完成后返回[Result](Result)结果对象，
     * 包含SQL执行的执行结果信息，如查询结果、更新影响行数等数据。
     * </p>
     *
     * @param customizer JDBC取样器自定义器，用于配置JDBC取样器
     * @return JDBC测试执行结果
     * @see #jdbc(String, Customizer)
     * @see JDBCSampler.Builder
     * @see Result
     */
    public static Result jdbc(Customizer<JDBCSampler.Builder> customizer) {
        return jdbc("", customizer);
    }

    /**
     * 执行JDBC数据库测试，使用自定义器方式构建JDBC取样器配置并指定标题
     * <p>
     * 该方法通过[Customizer](Customizer)来自定义JDBC取样器配置，并指定测试标题。自定义器中的方法调用会直接作用于[JDBCSampler.Builder](JDBCSampler.Builder)对象，
     * 允许以编程方式定义数据库连接信息和SQL语句等配置。执行完成后返回[Result](Result)结果对象，
     * 包含SQL执行的执行结果信息，如查询结果、更新影响行数等数据。
     * </p>
     * <p>
     * 业务流程：
     * <ol>
     *   <li>创建[JDBCSampler.Builder](JDBCSampler.Builder)实例</li>
     *   <li>通过自定义器配置JDBC取样器</li>
     *   <li>构建JDBC取样器对象</li>
     *   <li>执行JDBC测试并返回结果</li>
     * </ol>
     * </p>
     *
     * @param title      JDBC测试标题
     * @param customizer JDBC取样器自定义器，用于配置JDBC取样器
     * @return JDBC测试执行结果
     * @see JDBCSampler.Builder
     * @see Result
     */
    public static Result jdbc(String title, Customizer<JDBCSampler.Builder> customizer) {
        var builder = JDBCSampler.builder();
        customizer.customize(builder);
        return MagicBox.runTest(title, builder.build());
    }

    /**
     * 执行Redis测试，使用闭包方式构建Redis取样器配置
     * <p>
     * 该方法通过Groovy闭包来配置Redis取样器，使用默认空标题。闭包中的方法调用会被委托给[RedisSampler.Builder](RedisSampler.Builder)对象，
     * 允许以声明式的方式定义Redis连接信息和命令参数等配置。执行完成后返回[Result](Result)结果对象，
     * 包含Redis命令执行的执行结果信息，如命令返回值等数据。
     * </p>
     *
     * @param closure Groovy闭包，用于配置Redis取样器，闭包中的方法调用会被委托给[RedisSampler.Builder](RedisSampler.Builder)
     * @return Redis测试执行结果
     * @see #redis(String, Closure)
     * @see RedisSampler.Builder
     * @see Result
     */
    public static Result redis(@DelegatesTo(strategy = Closure.DELEGATE_ONLY, value = RedisSampler.Builder.class) Closure<?> closure) {
        return redis("", closure);
    }

    /**
     * 执行Redis测试，使用闭包方式构建Redis取样器配置并指定标题
     * <p>
     * 该方法通过Groovy闭包来配置Redis取样器，并指定测试标题。闭包中的方法调用会被委托给[RedisSampler.Builder](RedisSampler.Builder)对象，
     * 允许以声明式的方式定义Redis连接信息和命令参数等配置。执行完成后返回[Result](Result)结果对象，
     * 包含Redis命令执行的执行结果信息，如命令返回值等数据。
     * </p>
     * <p>
     * 业务流程：
     * <ol>
     *   <li>创建[RedisSampler.Builder](RedisSampler.Builder)实例</li>
     *   <li>通过Groovy闭包配置Redis取样器</li>
     *   <li>构建Redis取样器对象</li>
     *   <li>执行Redis测试并返回结果</li>
     * </ol>
     * </p>
     *
     * @param title   Redis测试标题
     * @param closure Groovy闭包，用于配置Redis取样器，闭包中的方法调用会被委托给[RedisSampler.Builder](RedisSampler.Builder)
     * @return Redis测试执行结果
     * @see RedisSampler.Builder
     * @see Result
     */
    public static Result redis(String title,
                               @DelegatesTo(strategy = Closure.DELEGATE_ONLY, value = RedisSampler.Builder.class) Closure<?> closure) {
        var builder = RedisSampler.builder();
        Groovy.call(closure, builder);
        return runTest(title, builder.build());
    }

    /**
     * 执行Redis测试，使用自定义器方式构建Redis取样器配置
     * <p>
     * 该方法通过[Customizer](Customizer)来自定义Redis取样器配置，使用默认空标题。自定义器中的方法调用会直接作用于[RedisSampler.Builder](RedisSampler.Builder)对象，
     * 允许以编程方式定义Redis连接信息和命令参数等配置。执行完成后返回[Result](Result)结果对象，
     * 包含Redis命令执行的执行结果信息，如命令返回值等数据。
     * </p>
     *
     * @param customizer Redis取样器自定义器，用于配置Redis取样器
     * @return Redis测试执行结果
     * @see #redis(String, Customizer)
     * @see RedisSampler.Builder
     * @see Result
     */
    public static Result redis(Customizer<RedisSampler.Builder> customizer) {
        return redis("", customizer);
    }

    /**
     * 执行Redis测试，使用自定义器方式构建Redis取样器配置并指定标题
     * <p>
     * 该方法通过[Customizer](Customizer)来自定义Redis取样器配置，并指定测试标题。自定义器中的方法调用会直接作用于[RedisSampler.Builder](RedisSampler.Builder)对象，
     * 允许以编程方式定义Redis连接信息和命令参数等配置。执行完成后返回[Result](Result)结果对象，
     * 包含Redis命令执行的执行结果信息，如命令返回值等数据。
     * </p>
     * <p>
     * 业务流程：
     * <ol>
     *   <li>创建[RedisSampler.Builder](RedisSampler.Builder)实例</li>
     *   <li>通过自定义器配置Redis取样器</li>
     *   <li>构建Redis取样器对象</li>
     *   <li>执行Redis测试并返回结果</li>
     * </ol>
     * </p>
     *
     * @param title      Redis测试标题
     * @param customizer Redis取样器自定义器，用于配置Redis取样器
     * @return Redis测试执行结果
     * @see RedisSampler.Builder
     * @see Result
     */
    public static Result redis(String title, Customizer<RedisSampler.Builder> customizer) {
        var builder = RedisSampler.builder();
        customizer.customize(builder);
        return runTest(title, builder.build());
    }

    /**
     * 执行测试元素的核心方法
     * <p>
     * 该方法是所有测试执行方法的底层实现，负责实际执行测试元素并返回结果。它会先检查测试元素是否为[AbstractTestElement](AbstractTestElement)实例且标题不为空，
     * 如果是则设置测试元素的标题，然后通过[SessionRunner](SessionRunner)执行测试元素并返回结果。
     * </p>
     * <p>
     * 业务流程：
     * <ol>
     *   <li>检查测试元素类型和标题有效性</li>
     *   <li>如果需要，设置测试元素标题</li>
     *   <li>通过[SessionRunner](SessionRunner)获取当前会话并执行测试元素</li>
     *   <li>返回执行结果</li>
     * </ol>
     * </p>
     *
     * @param title       测试标题
     * @param testElement 要执行的测试元素
     * @param <R>         测试结果类型
     * @return 测试执行结果
     * @see SessionRunner
     * @see AbstractTestElement
     * @see TestElement
     */
    public static <R extends Result> R runTest(String title, TestElement<R> testElement) {
        if (testElement instanceof AbstractTestElement<?, ?, R> && StringUtils.isNotBlank(title)) {
            ((AbstractTestElement<?, ?, R>) testElement).setTitle(title);
        }
        return SessionRunner.getSession().runTest(testElement);
    }

    /**
     * 执行调试测试，使用闭包方式构建调试取样器配置
     * <p>
     * 该方法通过Groovy闭包来配置调试取样器，使用默认空标题。闭包中的方法调用会被委托给[DebugSampler.Builder](DebugSampler.Builder)对象，
     * 允许以声明式的方式定义调试信息。执行完成后返回[Result](Result)结果对象，主要用于调试和测试框架功能验证。
     * </p>
     *
     * @param closure Groovy闭包，用于配置调试取样器，闭包中的方法调用会被委托给[DebugSampler.Builder](DebugSampler.Builder)
     * @return 调试测试执行结果
     * @see #debug(String, Closure)
     * @see DebugSampler.Builder
     * @see Result
     */
    public static Result debug(@DelegatesTo(strategy = Closure.DELEGATE_ONLY, value = DebugSampler.Builder.class) Closure<?> closure) {
        return debug("", closure);
    }

    /**
     * 执行调试测试，使用闭包方式构建调试取样器配置并指定标题
     * <p>
     * 该方法通过Groovy闭包来配置调试取样器，并指定测试标题。闭包中的方法调用会被委托给[DebugSampler.Builder](DebugSampler.Builder)对象，
     * 允许以声明式的方式定义调试信息。执行完成后返回[Result](Result)结果对象，主要用于调试和测试框架功能验证。
     * </p>
     * <p>
     * 业务流程：
     * <ol>
     *   <li>创建[DebugSampler.Builder](DebugSampler.Builder)实例</li>
     *   <li>通过Groovy闭包配置调试取样器</li>
     *   <li>构建调试取样器对象</li>
     *   <li>执行调试测试并返回结果</li>
     * </ol>
     * </p>
     *
     * @param title   调试测试标题
     * @param closure Groovy闭包，用于配置调试取样器，闭包中的方法调用会被委托给[DebugSampler.Builder](DebugSampler.Builder)
     * @return 调试测试执行结果
     * @see DebugSampler.Builder
     * @see Result
     */
    public static Result debug(String title,
                               @DelegatesTo(strategy = Closure.DELEGATE_ONLY, value = DebugSampler.Builder.class) Closure<?> closure) {
        var builder = DebugSampler.builder();
        Groovy.call(closure, builder);
        return MagicBox.runTest(title, builder.build());
    }

    /**
     * 执行调试测试，使用自定义器方式构建调试取样器配置
     * <p>
     * 该方法通过[Customizer](Customizer)来自定义调试取样器配置，使用默认空标题。自定义器中的方法调用会直接作用于[DebugSampler.Builder](DebugSampler.Builder)对象，
     * 允许以编程方式定义调试信息。执行完成后返回[Result](Result)结果对象，主要用于调试和测试框架功能验证。
     * </p>
     *
     * @param customizer 调试取样器自定义器，用于配置调试取样器
     * @return 调试测试执行结果
     * @see #debug(String, Customizer)
     * @see DebugSampler.Builder
     * @see Result
     */
    public static Result debug(Customizer<DebugSampler.Builder> customizer) {
        return debug("", customizer);
    }

    /**
     * 执行调试测试，使用自定义器方式构建调试取样器配置并指定标题
     * <p>
     * 该方法通过[Customizer](Customizer)来自定义调试取样器配置，并指定测试标题。自定义器中的方法调用会直接作用于[DebugSampler.Builder](DebugSampler.Builder)对象，
     * 允许以编程方式定义调试信息。执行完成后返回[Result](Result)结果对象，主要用于调试和测试框架功能验证。
     * </p>
     * <p>
     * 业务流程：
     * <ol>
     *   <li>创建[DebugSampler.Builder](DebugSampler.Builder)实例</li>
     *   <li>通过自定义器配置调试取样器</li>
     *   <li>构建调试取样器对象</li>
     *   <li>执行调试测试并返回结果</li>
     * </ol>
     * </p>
     *
     * @param title      调试测试标题
     * @param customizer 调试取样器自定义器，用于配置调试取样器
     * @return 调试测试执行结果
     * @see DebugSampler.Builder
     * @see Result
     */
    public static Result debug(String title, Customizer<DebugSampler.Builder> customizer) {
        var builder = DebugSampler.builder();
        customizer.customize(builder);
        return MagicBox.runTest(title, builder.build());
    }
}