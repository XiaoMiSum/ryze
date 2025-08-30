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

package io.github.xiaomisum.ryze.extractor;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.annotation.JSONField;
import io.github.xiaomisum.ryze.TestStatus;
import io.github.xiaomisum.ryze.builder.IBuilder;
import io.github.xiaomisum.ryze.context.ContextWrapper;
import io.github.xiaomisum.ryze.testelement.sampler.SampleResult;
import io.github.xiaomisum.ryze.support.ValidateResult;
import org.apache.commons.lang3.StringUtils;

/**
 * 抽象提取器基类，提供提取器的通用实现和标准处理流程
 * 
 * <p>该抽象类实现了提取器的核心处理逻辑，包括：
 * <ul>
 *   <li>提取器参数的验证</li>
 *   <li>测试结果的处理流程</li>
 *   <li>提取结果的存储机制</li>
 *   <li>异常处理和默认值机制</li>
 * </ul></p>
 * 
 * <p>具体的提取器实现类需要继承该类并实现
 * {@link #extract(SampleResult)}方法来定义具体的提取逻辑。</p>
 * 
 * <p>在Ryze框架中，提取器用于从测试结果（如HTTP响应、数据库查询结果等）中提取数据，
 * 并将提取的数据存储到上下文变量中供后续测试步骤使用。这使得测试用例可以实现动态数据处理和复杂业务场景的测试。</p>
 *
 * @author xiaomi
 */
@SuppressWarnings({"rawtypes", "unchecked"})
public abstract class AbstractExtractor implements Extractor, ExtractorConstantsInterface {

    /**
     * 提取字段表达式，根据不同提取器类型有不同的含义
     * 
     * <p>具体含义取决于提取器的实现类型：
     * <ul>
     *   <li>JSON提取器：JsonPath表达式</li>
     *   <li>正则表达式提取器：正则表达式</li>
     *   <li>HTTP头提取器：HTTP头字段名</li>
     * </ul></p>
     * 
     * <p>该字段是提取器的核心配置，定义了从测试结果中提取数据的具体方式。</p>
     */
    @JSONField(name = FIELD)
    protected String field;

    /**
     * 默认值，当提取失败时使用的备选值
     * 
     * <p>当提取器无法成功提取数据时，如果配置了默认值，
     * 系统会使用该值作为提取结果而不是抛出异常。</p>
     * 
     * <p>默认值机制提高了测试用例的健壮性，避免因某些非关键数据提取失败导致整个测试用例失败。</p>
     */
    @JSONField(name = DEFAULT_VALUE)
    protected Object defaultValue;

    /**
     * 引用名称，提取结果将以此名称存储到上下文变量中
     * 
     * <p>提取成功后，结果会以该名称作为键存储到测试上下文的本地变量映射中，
     * 后续测试步骤可以通过该名称引用提取的值。例如：${token}。</p>
     * 
     * <p>引用名称是提取器与测试用例其他部分交互的关键，确保了数据在不同测试步骤间的传递。</p>
     */
    @JSONField(name = REF_NAME)
    protected String refName;


    /**
     * 构造一个新的抽象提取器实例
     * <p>初始化所有字段为默认值，需要通过setter方法或构建器进行配置。</p>
     */
    public AbstractExtractor() {
    }

    /**
     * 验证提取器配置的有效性
     * 
     * <p>验证以下配置项：
     * <ul>
     *   <li>{@link #refName} 是否为空</li>
     *   <li>{@link #field} 是否为空</li>
     * </ul></p>
     * 
     * <p>验证失败时会返回包含详细错误信息的ValidateResult对象，
     * 而不是直接抛出异常，这样可以在测试执行前统一处理所有配置错误。</p>
     * 
     * @return 验证结果，包含所有验证错误信息
     */
    @Override
    public ValidateResult validate() {
        ValidateResult result = new ValidateResult();
        if (StringUtils.isBlank(refName)) {
            result.append("\n提取引用名称 %s 字段值缺失或为空，当前值：%s", REF_NAME, toString());
        }
        if (StringUtils.isBlank(field)) {
            result.append("\n提取表达式 %s 字段值缺失或为空，当前值：%s", field, toString());
        }
        return result;
    }

    /**
     * 处理提取器执行流程
     * 
     * <p>处理流程包括：
     * <ol>
     *   <li>检查测试执行状态</li>
     *   <li>获取测试结果</li>
     *   <li>执行具体提取逻辑</li>
     *   <li>处理提取结果或异常</li>
     *   <li>存储提取结果到上下文变量</li>
     * </ol></p>
     * 
     * <p>该方法实现了提取器的完整执行生命周期，确保在各种情况下都能正确处理提取过程。</p>
     * 
     * @param context 测试上下文，包含测试结果和变量信息
     * @throws RuntimeException 当提取过程中发生无法处理的错误且未设置默认值时抛出
     */
    @Override
    public void process(ContextWrapper context) {
        if (!context.getTestResult().getStatus().isPassed()) {
            // 非 ryze-testng 框架下测试失败不会抛出异常，取样步骤结果失败，无需执行验证器
            return;
        }
        if (context.getTestResult() instanceof SampleResult result) {
            try {
                var res = StringUtils.isBlank(result.getResponse().bytesAsString()) ? broken() : extract(result);
                if (TestStatus.passed.equals(res.getStatus())) {
                    context.getLocalVariablesWrapper().put(refName, res.getValue());
                    return;
                }
                if (TestStatus.failed.equals(res.getStatus()) && defaultValue != null) {
                    res.setStatus(TestStatus.passed);
                    context.getLocalVariablesWrapper().put(refName, defaultValue);
                }
                if (res.getStatus().isBroken() || res.getStatus().isFailed()) {
                    throw new RuntimeException(res.getMessage(), res.getException());
                }
                return;
            } catch (Exception e) {
                if (defaultValue != null) {
                    // 提取发生异常，且设置了默认值，则使用默认值
                    context.getLocalVariablesWrapper().put(refName, defaultValue);
                    return;
                }
                throw new RuntimeException(e);
            }

        }
        throw new RuntimeException("不支持提取的测试组件: " + context.getTestElement().getClass());
    }

    /**
     * 将提取器对象转换为JSON字符串表示
     * <p>主要用于日志记录和调试，提供提取器配置的可视化表示。</p>
     * 
     * @return 提取器的JSON字符串表示
     */
    @Override
    public String toString() {
        return JSON.toJSONString(this);
    }

    /**
     * 创建提取失败的结果对象
     * 
     * <p>当待提取的字符串为null或空白时调用此方法创建失败结果。</p>
     * 
     * @return 表示提取失败的ExtractResult对象
     */
    private ExtractResult broken() {
        var res = new ExtractResult(getClass().getSimpleName());
        res.setStatus(TestStatus.broken);
        res.setMessage("待提取的字符串为 null 或空白");
        return res;
    }

    /**
     * 执行具体的提取逻辑，由子类实现
     * 
     * <p>每个具体的提取器实现类都需要实现此方法来定义其特定的提取逻辑。</p>
     * 
     * <p>实现时需要注意：
     * <ul>
     *   <li>正确解析field字段定义的提取表达式</li>
     *   <li>从SampleResult中提取目标数据</li>
     *   <li>处理各种可能的异常情况</li>
     *   <li>返回适当的ExtractResult对象</li>
     * </ul></p>
     * 
     * @param context 取样结果上下文，包含待提取的数据
     * @return 提取结果对象，包含提取状态、值和可能的错误信息
     */
    protected abstract ExtractResult extract(SampleResult context);

    /**
     * 获取提取字段表达式
     * 
     * @return 提取字段表达式
     */
    public String getField() {
        return field;
    }

    /**
     * 设置提取字段表达式
     * 
     * @param field 提取字段表达式
     */
    public void setField(String field) {
        this.field = field;
    }

    /**
     * 获取默认值
     * 
     * @return 默认值
     */
    public Object getDefaultValue() {
        return defaultValue;
    }

    /**
     * 设置默认值
     * 
     * @param defaultValue 默认值
     */
    public void setDefaultValue(Object defaultValue) {
        this.defaultValue = defaultValue;
    }

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
     * 提取器基础构建器
     *
     * <p>提供链式调用方式构建提取器实例的抽象构建器类。
     * 具体提取器的构建器应继承此类并提供特定的构建方法。</p>
     *
     * <p>使用构建器模式可以提供更清晰的API，使提取器的配置更加直观和易于理解。</p>
     *
     * @param <SELF> 构建类自身类型，用于实现链式调用
     * @param <EXTRACTOR> 要构建的提取器类型
     */
    public static abstract class Builder<SELF extends Builder<SELF, EXTRACTOR>, EXTRACTOR extends AbstractExtractor>
            implements IBuilder<EXTRACTOR> {
        /**
         * 要构建的提取器实例
         * <p>所有构建方法都会直接修改该实例的属性，最终通过build()方法返回。</p>
         */
        protected final EXTRACTOR extractor;
        
        /**
         * 构建器自身的引用，用于链式调用
         * <p>通过将this强制转换为SELF类型，实现链式调用而无需子类重复实现。</p>
         */
        protected SELF self;


        /**
         * 构造一个新的构建器实例
         * 
         * @param extractor 要构建的提取器实例
         */
        protected Builder(EXTRACTOR extractor) {
            self = (SELF) this;
            this.extractor = extractor;
        }

        /**
         * 设置引用名称
         * <p>设置提取结果存储到上下文变量时使用的键名。</p>
         * 
         * @param refName 引用名称
         * @return 构建器自身实例，用于链式调用
         */
        public SELF refName(String refName) {
            extractor.refName = refName;
            return self;
        }

        /**
         * 设置提取字段表达式
         * <p>根据提取器类型设置相应的提取表达式，如JsonPath、XPath、正则表达式等。</p>
         * 
         * @param field 提取字段表达式
         * @return 构建器自身实例，用于链式调用
         */
        public SELF field(String field) {
            extractor.field = field;
            return self;
        }

        /**
         * 设置默认值
         * <p>当提取失败时使用的备选值，提高测试用例的健壮性。</p>
         * 
         * @param defaultValue 默认值
         * @return 构建器自身实例，用于链式调用
         */
        public SELF defaultValue(Object defaultValue) {
            extractor.defaultValue = defaultValue;
            return self;
        }

        /**
         * 构建并返回提取器实例
         * <p>完成所有配置后调用此方法获取最终的提取器实例。</p>
         * 
         * @return 构建完成的提取器实例
         */
        @Override
        public EXTRACTOR build() {
            return extractor;
        }
    }
}