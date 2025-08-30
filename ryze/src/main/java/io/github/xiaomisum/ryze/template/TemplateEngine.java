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

package io.github.xiaomisum.ryze.template;

import io.github.xiaomisum.ryze.context.ContextWrapper;
import io.github.xiaomisum.ryze.support.Collections;
import io.github.xiaomisum.ryze.support.Computable;

import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * 模板引擎接口，用于处理变量替换和函数执行
 * <p>
 * TemplateEngine是Ryze框架中表达式计算的核心组件，负责将包含变量和函数的模板字符串
 * 转换为实际的值。它支持多种数据类型的表达式计算，包括简单字符串、Map、List等。
 * </p>
 * <p>
 * 主要功能包括：
 * <ul>
 *   <li>字符串表达式计算：处理包含变量和函数的模板字符串</li>
 *   <li>集合类型表达式计算：递归处理Map和List中的表达式</li>
 *   <li>对象表达式计算：处理实现Computable接口的对象</li>
 *   <li>表达式检测：判断字符串是否包含需要计算的表达式</li>
 * </ul>
 * </p>
 * <p>
 * 在Ryze框架中，模板引擎主要用于处理测试用例中的动态数据，如：
 * <ul>
 *   <li>变量引用：${username}、${response.code}</li>
 *   <li>函数调用：${uuid()}、${randomInt(1,100)}</li>
 *   <li>复杂表达式：${"Hello, " + username + "!"}</li>
 * </ul>
 * </p>
 *
 * @author xiaomi
 */
@SuppressWarnings({"unchecked", "rawtypes"})
public interface TemplateEngine {

    /**
     * freemarker 模板引擎表达式匹配正则表达式，用于判断模板中是否有模板变量或函数调用
     * <p>
     * 该正则表达式匹配任何包含${...}形式表达式的字符串，
     * 用于快速判断字符串是否需要进行模板计算。
     * </p>
     */
    Pattern EXPRESSION = Pattern.compile("[\\s\\S]*\\$\\{.+}[\\s\\S]*");

    /**
     * 判断模板字符串是否包含表达式
     * <p>
     * 通过正则表达式检测给定的字符串是否包含${...}形式的表达式。
     * 如果包含则返回true，否则返回false。
     * </p>
     *
     * @param template 待检测的模板字符串
     * @return 如果包含表达式则返回true，否则返回false
     */
    static boolean hasExpression(String template) {
        return EXPRESSION.matcher(template).matches();
    }

    /**
     * 判断模板字符串是否不包含表达式
     * <p>
     * 与 {@link #hasExpression(String)}方法相反，
     * 用于判断给定的字符串是否不包含任何表达式。
     * </p>
     *
     * @param template 待检测的模板字符串
     * @return 如果不包含表达式则返回true，否则返回false
     */
    static boolean noneExpression(String template) {
        return !hasExpression(template);
    }

    /**
     * 对Map中的所有值进行表达式计算
     * <p>
     * 递归处理Map中的每个值，如果值是字符串且包含表达式，则进行计算；
     * 如果值是Map或List，则递归处理；如果值实现了Computable接口，
     * 则调用其evaluate方法进行计算。
     * </p>
     *
     * @param context 测试上下文，提供变量和函数的执行环境
     * @param map     待处理的Map对象
     * @return 处理后的Map对象，其中所有表达式都已被计算
     */
    default Map<String, Object> evaluate(ContextWrapper context, Map<String, Object> map) {
        if (map == null) {
            return null;
        }
        // 替换不可变Map
        if (Collections.isUnmodifiableMap(map)) {
            map = Collections.newHashMap(map);
        }
        map.replaceAll((k, v) -> evaluate(context, v));
        return map;
    }

    /**
     * 对List中的所有元素进行表达式计算
     * <p>
     * 递归处理List中的每个元素，如果元素是字符串且包含表达式，则进行计算；
     * 如果元素是Map或List，则递归处理；如果元素实现了Computable接口，
     * 则调用其evaluate方法进行计算。
     * </p>
     *
     * @param context 测试上下文，提供变量和函数的执行环境
     * @param list    待处理的List对象
     * @return 处理后的List对象，其中所有表达式都已被计算
     */
    default List<Object> evaluate(ContextWrapper context, List<Object> list) {
        if (list == null) {
            return null;
        }
        // 替换不可变List
        if (Collections.isUnmodifiableList(list)) {
            list = Collections.newArrayList(list);
        }
        list.replaceAll(v -> evaluate(context, v));
        return list;
    }

    /**
     * 对对象进行表达式计算
     * <p>
     * 根据对象的类型选择相应的处理方式：
     * <ul>
     *   <li>字符串：调用字符串表达式计算方法</li>
     *   <li>Computable实现：调用其evaluate方法</li>
     *   <li>Map：调用Map表达式计算方法</li>
     *   <li>List：调用List表达式计算方法</li>
     *   <li>null：返回null</li>
     *   <li>其他：返回原对象</li>
     * </ul>
     * </p>
     *
     * @param context 测试上下文，提供变量和函数的执行环境
     * @param object  待处理的对象
     * @return 处理后的对象
     */
    default Object evaluate(ContextWrapper context, Object object) {
        return switch (object) {
            case String template -> evaluate(context, template);
            case Computable<?> computable -> computable.evaluate(context);
            case Map map -> evaluate(context, map);
            case List list -> evaluate(context, list);
            case null -> null;
            default -> object;
        };
    }

    /**
     * 对表达式字符串进行计算
     * <p>
     * 这是模板引擎的核心方法，负责将包含变量和函数的表达式字符串
     * 转换为实际的值。具体的实现取决于使用的模板引擎类型。
     * </p>
     *
     * @param context    测试上下文，提供变量和函数的执行环境
     * @param expression 待计算的表达式字符串
     * @return 计算结果
     */
    Object evaluate(ContextWrapper context, String expression);

    /**
     * 使用给定的模型数据对表达式字符串进行计算
     * <p>
     * 该方法允许使用自定义的模型数据进行表达式计算，
     * 而不依赖于测试上下文中的变量和函数。
     * </p>
     *
     * @param model      模型数据，包含表达式中使用的变量
     * @param expression 待计算的表达式字符串
     * @return 计算结果
     */
    Object evaluate(Map<String, Object> model, String expression);

}