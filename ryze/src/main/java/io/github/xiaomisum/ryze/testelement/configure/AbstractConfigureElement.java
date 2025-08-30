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

package io.github.xiaomisum.ryze.testelement.configure;

import com.alibaba.fastjson2.annotation.JSONField;
import io.github.xiaomisum.ryze.Result;
import io.github.xiaomisum.ryze.config.ConfigureItem;
import io.github.xiaomisum.ryze.context.ContextWrapper;
import io.github.xiaomisum.ryze.testelement.AbstractTestElement;

/**
 * 抽象配置元件类，提供配置元件的通用实现
 * 
 * <p>配置元件是Ryze框架中用于配置测试环境的特殊测试元件，
 * 它们在测试执行前初始化测试环境，如数据库连接、HTTP客户端等。</p>
 * 
 * <p>配置元件的主要特点：
 * <ul>
 *   <li>在测试执行前进行初始化</li>
 *   <li>可以在测试上下文中存储配置信息供后续测试步骤使用</li>
 *   <li>支持通过引用名称在上下文中标识和访问</li>
 *   <li>可以实现Closeable接口以支持资源清理</li>
 * </ul></p>
 * 
 * <p>配置元件的执行流程：
 * <ol>
 *   <li>检查是否已初始化，如未初始化则进行初始化</li>
 *   <li>对配置项进行表达式计算</li>
 *   <li>调用抽象方法doProcess执行具体的配置逻辑</li>
 *   <li>返回测试结果</li>
 * </ol></p>
 *
 * @param <SELF> 实际的配置元件类型，用于实现流畅的构建器模式
 * @param <CONFIG> 配置项类型，用于存储协议相关的配置信息
 * @param <R> 测试结果类型，表示执行结果的具体实现
 * @author xiaomi
 * Created at 2025/7/19 15:47
 */
@SuppressWarnings({"rawtypes", "unchecked"})
public abstract class AbstractConfigureElement<SELF extends AbstractConfigureElement<SELF, CONFIG, R>, CONFIG extends ConfigureItem<CONFIG>, R extends Result>
        extends AbstractTestElement<AbstractConfigureElement<SELF, CONFIG, R>, CONFIG, R>
        implements ConfigureElement<R>, ConfigureElementConstantsInterface {

    /**
     * 配置元件的引用名称，用于在上下文中标识和访问该配置元件
     * <p>通过该名称可以在测试上下文中存储和获取配置元件的相关信息，
     * 实现配置元件与其他测试步骤之间的数据共享。</p>
     */
    @JSONField(name = REF_NAME, ordinal = 1)
    protected String refName;

    /**
     * 基于构建器的构造函数
     * 
     * @param builder 构建器实例
     */
    public AbstractConfigureElement(Builder<SELF, ?, CONFIG, ?, R> builder) {
        super(builder);
        this.refName = builder.refName;
    }

    /**
     * 默认构造函数
     */
    public AbstractConfigureElement() {
    }


    /**
     * 处理配置元件的执行逻辑
     * 
     * <p>该方法实现了配置元件的通用执行流程：
     * <ol>
     *   <li>检查是否已初始化，如未初始化则进行初始化</li>
     *   <li>对配置项进行表达式计算</li>
     *   <li>调用抽象方法doProcess执行具体的配置逻辑</li>
     *   <li>返回测试结果</li>
     * </ol></p>
     * 
     * @param context 测试上下文
     * @return 测试结果
     */
    public R process(ContextWrapper context) {
        if (!initialized) {
            initialized();
        }
        runtime.config = (CONFIG) context.evaluate(runtime.config);
        var result = getTestResult();
        doProcess(context);
        return result;
    }


    /**
     * 执行具体的配置逻辑，由子类实现
     * 
     * <p>每个具体的配置元件实现类都需要实现此方法来定义其特定的配置逻辑，
     * 如初始化连接、设置参数等。</p>
     * 
     * @param context 测试上下文
     */
    protected abstract void doProcess(ContextWrapper context);

    /**
     * 获取引用名称
     * 
     * @return 引用名称
     */
    public String getRefName() {
        return refName;
    }

    /**
     * 设置引用名称
     * 
     * @param refName 引用名称
     */
    public void setRefName(String refName) {
        this.refName = refName;
    }

    /**
     * 配置元件构建器抽象类
     * 
     * <p>提供构建配置元件实例的通用方法，具体配置元件的构建器应继承此类。</p>
     *
     * @param <ELE> 实际的配置元件类型
     * @param <SELF> 构建器自身类型，用于实现流畅的构建器模式
     * @param <CONFIG> 配置项类型
     * @param <CONFIGURE_BUILDER> 配置项构建器类型
     * @param <R> 测试结果类型
     */
    public static abstract class Builder<ELE extends AbstractConfigureElement<ELE, CONFIG, R>,
            SELF extends Builder<ELE, SELF, CONFIG, CONFIGURE_BUILDER, R>,
            CONFIG extends ConfigureItem<CONFIG>,
            CONFIGURE_BUILDER extends ConfigureBuilder<?, CONFIG>,
            R extends Result>
            extends AbstractTestElement.Builder<AbstractConfigureElement<ELE, CONFIG, R>, SELF, CONFIG, CONFIGURE_BUILDER, R> {

        /**
         * 配置元件的引用名称
         */
        protected String refName;

        /**
         * 设置引用名称
         * 
         * @param refName 引用名称
         * @return 构建器实例
         */
        public SELF refName(String refName) {
            this.refName = refName;
            return self;
        }
    }
}