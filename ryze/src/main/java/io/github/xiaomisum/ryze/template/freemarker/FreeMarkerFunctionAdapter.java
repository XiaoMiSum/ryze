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

import freemarker.template.TemplateMethodModelEx;
import freemarker.template.TemplateModel;
import freemarker.template.TemplateModelException;
import freemarker.template.utility.DeepUnwrap;
import io.github.xiaomisum.ryze.context.ContextWrapper;
import io.github.xiaomisum.ryze.function.Args;
import io.github.xiaomisum.ryze.function.Function;

import java.util.List;

/**
 * FreeMarker 函数适配器，将 FreeMarker函数转为 {@link  Function} 实现类执行
 *
 * <p>该适配器用于在FreeMarker模板引擎中调用框架内置函数。
 * 它实现了FreeMarker的 {@link TemplateMethodModelEx}接口，
 * 将FreeMarker函数调用转换为框架函数执行。</p>
 *
 * <p>主要功能包括：
 * <ul>
 *   <li>将FreeMarker模板中的函数调用适配到框架函数</li>
 *   <li>处理参数类型转换，将TemplateModel包装类解包为原始对象</li>
 *   <li>在函数执行时提供上下文环境</li>
 * </ul></p>
 *
 * @author xiaomi
 */
public record FreeMarkerFunctionAdapter(ContextWrapper context, Function function) implements TemplateMethodModelEx {

    /**
     * 执行函数调用
     *
     * <p>该方法将FreeMarker传递的参数列表转换为框架函数所需的参数格式，
     * 然后调用实际的函数实现进行处理。</p>
     *
     * @param args FreeMarker传递的参数列表
     * @return 函数执行结果
     */
    @SuppressWarnings("all")
    public Object exec(List args) {
        Args list = new Args();
        args.forEach(item -> {
            try {
                // 将 TemplateModel 包装类对象解包为模板中传递的原始 Object 对象
                list.add(DeepUnwrap.unwrap((TemplateModel) item));
            } catch (TemplateModelException e) {
                e.printStackTrace();
            }
        });
        return function.execute(context, list);
    }

}