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

import freemarker.core.Environment;
import freemarker.template.TemplateException;
import freemarker.template.TemplateExceptionHandler;

import java.io.Writer;

/**
 * 自定义的模板异常处理器，用于处理FreeMarker模板执行过程中的异常
 *
 * <p>该类实现了FreeMarker的 {@link TemplateExceptionHandler}接口，
 * 提供了针对框架的模板异常处理逻辑。当模板执行过程中发生异常时，
 * 该处理器会捕获异常并提供更友好的错误信息。</p>
 *
 * <p>主要功能包括：
 * <ul>
 *   <li>捕获和处理模板执行异常</li>
 *   <li>提供详细的错误信息</li>
 *   <li>处理特殊对象包装器相关的异常</li>
 * </ul></p>
 *
 * @author xiaomi
 */
public class RyzeTemplateExceptionHandler implements TemplateExceptionHandler {

    /**
     * 处理模板异常
     *
     * <p>当FreeMarker模板执行过程中发生异常时，该方法会被调用。
     * 它会构建包含详细错误信息的异常并重新抛出。</p>
     *
     * @param te  模板异常
     * @param env 模板执行环境
     * @param out 输出写入器
     * @throws TemplateException 模板异常
     */
    @Override
    public void handleTemplateException(TemplateException te, Environment env, Writer out) throws TemplateException {
        StringBuilder builder = new StringBuilder();
        String template = env.getCurrentTemplate().toString();
        if (template.contains(FreeMarkerTemplateEngine.TemplateObjectHandler.NAME)) {
            template = String.format("${%s}", template.substring(FreeMarkerTemplateEngine.TemplateObjectHandler.NAME.length() + 3, template.length() - 2));
        }
        builder.append("\n")
                .append(te.getMessageWithoutStackTop())
                .append("\nryze-freemarker-template：")
                .append(template);
        throw new RuntimeException(builder.toString(), te);
    }

}