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

package io.github.xiaomisum.ryze.template.freemarker;

import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.LoadingCache;
import freemarker.template.*;
import freemarker.template.utility.DeepUnwrap;
import io.github.xiaomisum.ryze.ApplicationConfig;
import io.github.xiaomisum.ryze.context.ContextWrapper;
import io.github.xiaomisum.ryze.template.TemplateEngine;
import io.github.xiaomisum.ryze.template.Vkw;

import java.io.StringWriter;
import java.util.*;
import java.util.regex.Pattern;


/**
 * FreeMarker 模板引擎实现类
 * 
 * <p>该类是框架的模板引擎实现，基于FreeMarker模板引擎提供表达式计算功能。
 * 它支持变量替换、函数调用、条件判断等模板操作。</p>
 * 
 * <p>主要功能包括：
 * <ul>
 *   <li>表达式计算：支持变量引用、函数调用等表达式计算</li>
 *   <li>模板缓存：通过缓存机制提高模板执行效率</li>
 *   <li>类型处理：支持返回原始类型或字符串类型的结果</li>
 *   <li>函数适配：将框架内置函数适配到模板引擎中</li>
 * </ul></p>
 * 
 * <p>执行流程：
 * <ol>
 *   <li>检查表达式是否需要计算，不需要则直接返回</li>
 *   <li>判断是否为简单变量引用，是则直接获取变量值</li>
 *   <li>判断是否需要返回原始类型</li>
 *   <li>注册内置函数和变量到模板模型</li>
 *   <li>执行模板计算并返回结果</li>
 * </ol></p>
 *
 * @author xiaomi
 */
public class FreeMarkerTemplateEngine implements TemplateEngine {

    /**
     * FreeMarker表达式匹配模式，用于识别${variable}形式的表达式
     */
    private static final Pattern FREEMARKER_EXPRESSION = Pattern.compile("^\\$\\{([a-zA-Z\\u4e00-\\u9fff$_][a-zA-Z\\u4e00-\\u9fff0-9$_]*)}$");
    
    /**
     * 简单表达式匹配模式，用于识别${...}形式的表达式
     */
    private static final Pattern SIMPLE_EXPRESSION = Pattern.compile("^\\$\\{((?![{}])[\\S ])+}$");

    /**
     * 模板缓存，用于缓存已编译的模板以降低内存开销
     */
    private static final LoadingCache<String, Template> templateCache = Caffeine.newBuilder().maximumSize(1000)
            .build(key -> new Template("ryze-freemarker-template", key, SingletonHolder.cfg));


    /**
     * 基于模型和表达式进行计算
     * 
     * <p>该方法使用提供的模型数据对表达式进行计算，支持变量替换和函数调用。</p>
     *
     * @param model 表达式计算所需的模型数据
     * @param expression 待计算的表达式
     * @return 表达式计算结果
     */
    @Override
    public Object evaluate(Map<String, Object> model, String expression) {
        if (expression == null) {
            return "null";
        }
        // 没有表达式时无需计算，直接返回
        if (TemplateEngine.noneExpression(expression)) {
            return expression;
        }

        if (FREEMARKER_EXPRESSION.matcher(expression).matches()) {
            return model.get(expression.substring(2, expression.length() - 1));
        }

        // 判断是否需要返回原始类型，对于符合条件的模板计算结果，尽量返回原始类型，而不是字符串
        // 由于FreeMarker 没有获取模板计算结果原始数据的api，这里通过将表达式包装为 ${ryze_object_handler(expression)}
        // 以取巧的方式获取模板计算结果原始数据
        var objectHandler = shouldReturnOriginalType(expression) ? new TemplateObjectHandler() : null;
        if (Objects.nonNull(objectHandler)) {
            model.put(TemplateObjectHandler.NAME, objectHandler);
            expression = String.format("${%s(%s)}", TemplateObjectHandler.NAME, expression.substring(2, expression.length() - 1));
        }
        try (StringWriter writer = new StringWriter()) {
            Template template = templateCache.get(expression);
            template.process(model, writer);
            return (Objects.nonNull(objectHandler)) ? objectHandler.object : writer.toString();
        } catch (Exception e) {
            throw new RuntimeException("模板 " + expression + " 执行失败", e);
        }
    }

    /**
     * 判断表达式是否应该返回原始类型
     * 
     * <p>对于简单的变量引用表达式，应该返回原始类型而不是字符串类型。</p>
     *
     * @param template 待判断的表达式
     * @return 是否应该返回原始类型
     */
    private boolean shouldReturnOriginalType(String template) {
        if (template.startsWith("${") && template.endsWith("}")) {
            if (SIMPLE_EXPRESSION.matcher(template).matches()) {
                return true;
            }
            try {
                Template t = templateCache.get(template);
                return Class.forName("freemarker.core.DollarVariable").isInstance(t.getRootTreeNode());
            } catch (ClassNotFoundException e) {
                throw new RuntimeException(e);
            }
        }
        return false;
    }

    /**
     * 基于上下文和表达式进行计算
     * 
     * <p>该方法使用提供的上下文对表达式进行计算，会自动注册内置函数和变量。</p>
     *
     * @param context 测试上下文
     * @param expression 待计算的表达式
     * @return 表达式计算结果
     */
    @Override
    public Object evaluate(ContextWrapper context, String expression) {
        if (expression == null) {
            return "null";
        }
        // 没有表达式时无需计算，直接返回
        if (!TemplateEngine.hasExpression(expression)) {
            return expression;
        }

        // 注册内置函数和内置变量
        Map<String, Object> model = new HashMap<>();
        // 注册函数集，以FreeMarkerFunctionAdapter 对参数解包后执行
        ApplicationConfig.getFunctions().forEach(f -> model.put(f.key(), new FreeMarkerFunctionAdapter(context, f)));
        // 注册 ContextWrapper 相关对象
        if (context != null) {
            // 注册上下文
            model.put(Vkw.ctx.name(), context);
            model.put(Vkw.context.name(), context);
            // 注册变量 - 框架执行过程中会将所有上级变量都合并到当前context的localVariablesWrapper中，这里仅需要将 localVariablesWrapper 注册到 model 中
            Optional.ofNullable(context.getLocalVariablesWrapper()).ifPresent(localVars -> {
                model.putAll(localVars.mergeVariables());
                model.put(Vkw.vars.name(), localVars);
            });
        }
        return evaluate(model, expression);
    }

    /**
     * 单例持有者，用于延迟初始化FreeMarker配置
     */
    private static class SingletonHolder {

        /**
         * FreeMarker配置实例
         */
        private static final Configuration cfg;

        static {
            cfg = new Configuration(Configuration.VERSION_2_3_32);
            // 模板文件编码
            cfg.setDefaultEncoding("UTF-8");
            // 模板运行异常处理器
            cfg.setTemplateExceptionHandler(new RyzeTemplateExceptionHandler());
            cfg.setLogTemplateExceptions(false);
            // 将模板处理过程中抛出的 unchecked 异常包装为 TemplateException
            cfg.setWrapUncheckedExceptions(true);
            // Do not fall back to higher scopes when reading a null loop variable:
            cfg.setFallbackOnNullLoopVariable(false);
            // To accomodate to how JDBC returns values; see Javadoc!
            cfg.setSQLDateAndTimeTimeZone(TimeZone.getDefault());
            cfg.setShowErrorTips(false);
            // 设置自定义对象包装器
            cfg.setObjectWrapper(new RyzeObjectWrapper(cfg.getIncompatibleImprovements()));
            cfg.setBooleanFormat("c");
            cfg.setNumberFormat("c");
            cfg.clearTemplateCache();
        }
    }

    /**
     * 模板对象处理器，用于获取模板计算结果的原始对象
     * 
     * <p>由于FreeMarker没有直接获取原始对象的API，通过该处理器以取巧的方式获取计算结果的原始对象。</p>
     */
    protected static class TemplateObjectHandler implements TemplateMethodModelEx {

        /**
         * 处理器名称
         */
        protected static final String NAME = "ryze_object_handler";
        
        /**
         * 存储处理结果的对象
         */
        private Object object;

        /**
         * 执行方法，保存参数中的第一个对象并返回标识字符串
         *
         * @param arguments 参数列表
         * @return 标识字符串
         * @throws TemplateModelException 模板模型异常
         */
        @Override
        public Object exec(List arguments) throws TemplateModelException {
            object = DeepUnwrap.unwrap((TemplateModel) arguments.getFirst());
            // object 可能为 null, 返回null 会导致异常。通过 getObject 显式获取 object
            return "name";
        }
    }


}