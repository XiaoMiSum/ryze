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

package io.github.xiaomisum.ryze.support.testng.annotation;

import io.github.xiaomisum.ryze.testelement.TestElement;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * ryze 框架支持，标记一个TestNg测试方法需支持ryze框架
 * <p>
 * 该注解用于标记TestNG测试方法，表明该方法是一个Ryze测试方法。
 * 在测试执行前会在@BeforeMethod中调用创建session，
 * 在测试执行后会在@AfterMethod中调用销毁session。
 * </p>
 * <p>
 * Ryze测试方法的特点：
 * <ul>
 *   <li>支持从外部文件加载测试数据</li>
 *   <li>自动管理测试框架会话</li>
 *   <li>支持测试用例的并行执行</li>
 *   <li>支持测试数据切片</li>
 * </ul>
 * </p>
 *
 * @author xiaomi
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.ANNOTATION_TYPE})
public @interface RyzeTest {

    /**
     * 数据源：文件路径
     * <p>
     * 指定测试数据文件的路径，支持多种协议：
     * <ul>
     *   <li>本地文件：/path/to/file.json 或 file:/path/to/file.yaml</li>
     *   <li>类路径资源：classpath:file.json</li>
     * </ul>
     * </p>
     *
     * @return 数据源文件路径
     */
    String value() default "";

    /**
     * 测试用例是否并行执行
     * <p>
     * 当设置为true时，测试方法会对数据源中的每条数据并行执行。
     * 适用于独立的测试用例，可以显著提高执行效率。
     * </p>
     *
     * @return true表示并行执行，false表示顺序执行
     */
    boolean parallel() default false;

    /**
     * 测试用例的参数类型，默认为 {@link TestElement} 类型
     *
     * @return 测试用例类型
     * @see Datasource
     */
    Class<?> type() default TestElement.class;

    /**
     * 测试用例的分片，默认为空，表示不分片
     *
     * @return 切片规则
     * @see Datasource
     */
    String slice() default "";
}