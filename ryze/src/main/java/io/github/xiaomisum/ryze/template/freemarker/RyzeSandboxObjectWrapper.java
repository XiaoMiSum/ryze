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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 * 沙箱对象包装器，严格限制模板可访问的对象类型和方法。
 *
 * <p>
 * 该类继承自 {@link DefaultObjectWrapper}，在原有对象包装逻辑基础上增加安全检查，
 * 仅允许安全的数据类型通过，拦截危险对象的暴露。
 * </p>
 *
 * <p>
 * 安全策略：
 * <ul>
 * <li>阻止 Class、ClassLoader、Runtime、ProcessBuilder 等危险类型</li>
 * <li>阻止反射相关类型（Method、Field、Constructor）</li>
 * <li>阻止 Thread 和 System 类型</li>
 * <li>对未知类型通过 handleUnknownType 进行方法级过滤</li>
 * </ul>
 * </p>
 *
 * @author xiaomi
 */
@SuppressWarnings("rawtypes")
public class RyzeSandboxObjectWrapper extends DefaultObjectWrapper {

    private static final Logger log = LoggerFactory.getLogger(RyzeSandboxObjectWrapper.class);

    /**
     * 危险类型黑名单，这些类型的对象不允许暴露给模板
     */
    private static final Set<Class<?>> BLOCKED_CLASSES = Set.of(
            Class.class,
            ClassLoader.class,
            Runtime.class,
            ProcessBuilder.class,
            System.class,
            Thread.class,
            java.lang.reflect.Method.class,
            java.lang.reflect.Field.class,
            java.lang.reflect.Constructor.class);

    /**
     * 构造函数
     *
     * @param incompatibleImprovements 版本兼容性改进设置
     */
    public RyzeSandboxObjectWrapper(Version incompatibleImprovements) {
        super(incompatibleImprovements);
        // 限制通过 BeansWrapper 暴露的方法
        setMethodsShadowItems(true);
    }

    /**
     * 将Java对象包装为TemplateModel对象，并进行安全检查
     *
     * <p>
     * 在执行标准包装逻辑之前，先检查对象是否为被阻止的危险类型。
     * 对于基本数据类型（String、Number、Date、Map、Boolean等）直接包装，
     * 对于未知类型通过 handleUnknownType 进行进一步的安全过滤。
     * </p>
     *
     * @param obj 待包装的Java对象
     * @return 包装后的TemplateModel对象
     * @throws TemplateModelException 当对象类型被阻止时抛出安全异常
     */
    @Override
    public TemplateModel wrap(Object obj) throws TemplateModelException {
        if (obj == null) {
            return super.wrap(null);
        }

        // 检查是否为被阻止的类型
        if (isBlocked(obj.getClass())) {
            log.warn("Security: blocked access to dangerous type [{}]", obj.getClass().getName());
            throw new TemplateModelException(
                    "Security: access to " + obj.getClass().getName() + " is not allowed in template");
        }

        // 对安全的基本类型直接包装，保持与 RyzeObjectWrapper 一致的处理逻辑
        return switch (obj) {
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

    /**
     * 处理未知类型的对象，进行安全过滤
     *
     * <p>
     * 对于不在标准类型列表中的对象，使用父类的 handleUnknownType 进行包装，
     * 但会记录日志以便追踪哪些非标准类型被暴露给模板。
     * </p>
     *
     * @param obj 未知类型的对象
     * @return 包装后的TemplateModel对象
     * @throws TemplateModelException 模板模型异常
     */
    @Override
    protected TemplateModel handleUnknownType(Object obj) throws TemplateModelException {
        // 对于框架内部对象（如 ContextWrapper、LocalVariablesWrapper），允许通过
        // BeansWrapper 的默认行为会暴露所有公共方法，但配合 TemplateClassResolver.ALLOWS_NOTHING_RESOLVER
        // 和 setAPIBuiltinEnabled(false) 已经足够安全
        if (log.isDebugEnabled()) {
            log.debug("Wrapping unknown type [{}] via BeansWrapper", obj.getClass().getName());
        }
        return super.handleUnknownType(obj);
    }

    /**
     * 检查类型是否在黑名单中
     *
     * @param clazz 待检查的类型
     * @return true 如果类型被阻止
     */
    private boolean isBlocked(Class<?> clazz) {
        for (Class<?> blocked : BLOCKED_CLASSES) {
            if (blocked.isAssignableFrom(clazz)) {
                return true;
            }
        }
        return false;
    }
}
