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

import io.github.xiaomisum.ryze.config.GlobalConfigure;
import io.github.xiaomisum.ryze.config.InterceptorConfigureItem;
import io.github.xiaomisum.ryze.context.GlobalContext;
import io.github.xiaomisum.ryze.interceptor.RyzeInterceptor;
import io.github.xiaomisum.ryze.interceptor.report.AllureReportListener;
import io.github.xiaomisum.ryze.template.TemplateEngine;
import io.github.xiaomisum.ryze.template.freemarker.FreeMarkerTemplateEngine;

import java.util.ArrayList;
import java.util.List;

import static io.github.xiaomisum.ryze.testelement.TestElementConstantsInterface.INTERCEPTORS;

/**
 * 测试框架配置类
 * <p>
 * 该类用于管理测试框架的全局配置信息，包括模板引擎、拦截器、全局上下文等核心组件的配置。
 * 通过该类可以统一管理测试执行环境的各种参数和设置，确保测试执行的一致性和可配置性。
 * </p>
 * <p>
 * Configure类支持通过系统属性配置是否启用Allure报告功能，默认情况下启用该功能。
 * </p>
 *
 * @SuppressWarnings({"rawtypes"}) 忽略原始类型警告
 */
@SuppressWarnings({"rawtypes"})
public class Configure {

    /**
     * 系统属性键名，用于配置是否启用Allure报告
     */
    private static final String RYZE_ENABLE_ALLURE_REPORT = "ryze.report.allure";

    /**
     * 是否启用Allure报告功能的配置项
     */
    private final boolean enableAllureReport;

    /**
     * 模板引擎，可指定使用自定义模板引擎
     * <p>用于处理测试配置中的模板表达式</p>
     */
    private TemplateEngine templateEngine;

    /**
     * 内置拦截器，主要是测试报告和执行日志拦截器
     * <p>用于在测试执行过程中拦截和处理各种事件</p>
     */
    private InterceptorConfigureItem<RyzeInterceptor> builtinRyzeInterceptors;

    /**
     * 全局上下文对象
     * <p>存储测试执行过程中的全局配置和状态信息</p>
     */
    private GlobalContext globalContext;

    /**
     * 默认构造函数
     * <p>
     * 从系统参数（命令行参数-Dryze.report.allure）中获取配置，默认开启Allure报告
     * </p>
     */
    public Configure() {
        // 从系统参数（命令行参数-Dryze.report.allure）中获取配置，默认开启Allure报告
        this(Boolean.parseBoolean(System.getProperty(RYZE_ENABLE_ALLURE_REPORT, "true")));
    }

    /**
     * 带参数的构造函数
     *
     * @param enableAllureReport 是否启用Allure报告功能
     */
    public Configure(boolean enableAllureReport) {
        this.enableAllureReport = enableAllureReport;
    }

    /**
     * 创建默认配置对象
     * <p>
     * 初始化并返回一个包含默认配置的Configure对象，包括模板引擎、拦截器和全局上下文等组件的默认配置。
     * </p>
     *
     * @return 默认配置对象
     */
    public static Configure defaultConfigure() {
        var configure = new Configure();
        configure.setTemplateEngine(new FreeMarkerTemplateEngine());
        var interceptors = new ArrayList<>(ApplicationConfig.getReporterListeners());
        if (!configure.isEnableAllureReport()) {
            interceptors.removeIf(AllureReportListener.class::isInstance);
        }
        InterceptorConfigureItem<RyzeInterceptor> items = new InterceptorConfigureItem<>();
        items.addAll(interceptors);
        configure.setBuiltinInterceptors(items);
        var globalContext = new GlobalContext(new GlobalConfigure());
        // todo 全局配置可以考虑固定位置存放全局变量
        globalContext.getConfigGroup().put(INTERCEPTORS, items);
        configure.setGlobalContext(globalContext);
        return configure;
    }

    /**
     * 判断是否启用Allure报告功能
     *
     * @return 如果启用Allure报告功能返回true，否则返回false
     */
    public boolean isEnableAllureReport() {
        return enableAllureReport;
    }

    /**
     * 获取模板引擎
     *
     * @return 模板引擎实例
     */
    public TemplateEngine getTemplateEngine() {
        return templateEngine;
    }

    /**
     * 设置模板引擎
     *
     * @param templateEngine 模板引擎实例
     */
    public void setTemplateEngine(TemplateEngine templateEngine) {
        this.templateEngine = templateEngine;
    }

    /**
     * 获取内置拦截器列表
     *
     * @return 内置拦截器列表
     */
    public List<RyzeInterceptor> getBuiltinInterceptors() {
        return builtinRyzeInterceptors;
    }

    /**
     * 设置内置拦截器
     *
     * @param builtinRyzeInterceptors 内置拦截器配置项
     */
    public void setBuiltinInterceptors(InterceptorConfigureItem<RyzeInterceptor> builtinRyzeInterceptors) {
        this.builtinRyzeInterceptors = builtinRyzeInterceptors;
    }

    /**
     * 获取全局上下文
     *
     * @return 全局上下文对象
     */
    public GlobalContext getGlobalContext() {
        return globalContext;
    }

    /**
     * 设置全局上下文
     *
     * @param globalContext 全局上下文对象
     */
    public void setGlobalContext(GlobalContext globalContext) {
        this.globalContext = globalContext;
    }
}