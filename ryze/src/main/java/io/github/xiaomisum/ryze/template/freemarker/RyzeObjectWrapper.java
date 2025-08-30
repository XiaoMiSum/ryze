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

import freemarker.template.*;

import java.util.Date;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.Map;

/**
 * ObjectWrapper 对象包装器，将Java对象包装为FreeMarker的TemplateModel类型
 *
 * <p>该类是FreeMarker模板引擎的对象包装器实现，负责将Java对象转换为FreeMarker可以处理的TemplateModel对象。
 * 它扩展了FreeMarker的 {@link DefaultObjectWrapper}类，
 * 提供了对框架中常用类型的特殊处理。</p>
 *
 * <p>主要功能包括：
 * <ul>
 *   <li>将Java对象包装为FreeMarker模板模型</li>
 *   <li>处理常用数据类型如字符串、数字、日期、集合等</li>
 *   <li>支持自定义类型包装逻辑</li>
 * </ul></p>
 *
 * @author xiaomi
 */
@SuppressWarnings("rawtypes")
public class RyzeObjectWrapper extends DefaultObjectWrapper {

    /**
     * 构造函数
     *
     * @param incompatibleImprovements 版本兼容性改进设置
     */
    public RyzeObjectWrapper(Version incompatibleImprovements) {
        super(incompatibleImprovements);
    }

    /**
     * 将Java对象包装为TemplateModel对象
     *
     * <p>该方法根据对象类型选择合适的TemplateModel实现进行包装：
     * <ul>
     *   <li>null值包装为null</li>
     *   <li>已为TemplateModel类型的对象直接返回</li>
     *   <li>字符串包装为SimpleScalar</li>
     *   <li>数字包装为SimpleNumber</li>
     *   <li>日期类型包装为SimpleDate</li>
     *   <li>Map类型包装为DefaultMapAdapter</li>
     *   <li>布尔值包装为TemplateBooleanModel</li>
     *   <li>迭代器包装为DefaultIteratorAdapter</li>
     *   <li>枚举包装为DefaultEnumerationAdapter</li>
     *   <li>数组包装为DefaultArrayAdapter</li>
     *   <li>其他类型调用handleUnknownType处理</li>
     * </ul></p>
     *
     * @param obj 待包装的Java对象
     * @return 包装后的TemplateModel对象
     * @throws TemplateModelException 模板模型异常
     */
    public TemplateModel wrap(Object obj) throws TemplateModelException {
        return switch (obj) {
            case null -> super.wrap(null);
            case TemplateModel templateModel -> templateModel;
            case String s -> new SimpleScalar(s);
            case Number number -> new SimpleNumber(number);
            case java.sql.Date date -> new SimpleDate(date);
            case java.sql.Time time -> new SimpleDate(time);
            case java.sql.Timestamp timestamp -> new SimpleDate(timestamp);
            case Date date -> new SimpleDate(date, getDefaultDateType());
            case Map map -> DefaultMapAdapter.adapt(map, this);
            case Boolean b -> b ? TemplateBooleanModel.TRUE : TemplateBooleanModel.FALSE;
            case Iterator iterator -> DefaultIteratorAdapter.adapt(iterator, this);
            case Enumeration enumeration -> DefaultEnumerationAdapter.adapt(enumeration, this);
            default -> {
                Class<?> objClass = obj.getClass();
                yield objClass.isArray() ? DefaultArrayAdapter.adapt(obj, this) : handleUnknownType(obj);
            }
        };
    }

}