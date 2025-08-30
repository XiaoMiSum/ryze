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

package io.github.xiaomisum.ryze.interceptor;

import com.alibaba.fastjson2.annotation.JSONType;
import io.github.xiaomisum.ryze.context.ContextWrapper;
import io.github.xiaomisum.ryze.testelement.TestElement;
import io.github.xiaomisum.ryze.support.fastjson.deserializer.InterceptorObjectReader;

/**
 * 拦截器接口
 * <p>
 * 执行顺序为：preHandle -> 自定义业务 -> postHandle -> afterCompletion
 * <p>
 * 多个拦截器之间是串行执行的，即：
 * 拦截器 A preHandle -> 拦截器B preHandle ... -> 自定义业务 -> 拦截器 B postHandle -> 拦截器 A postHandle ...
 * -> 拦截器 A afterCompletion -> 拦截器 B afterCompletion ...
 *
 * @author xiaomi
 */
@JSONType(deserializer = InterceptorObjectReader.class)
@SuppressWarnings({"unchecked", "rawtypes"})
public interface RyzeInterceptor<T extends TestElement> {

    int getOrder();

    /**
     * 计算当前测试元件对象是否需要应用该拦截器
     *
     * @param context 测试上下文
     * @return 如果对 testElement 使用当前拦截器则为 true，否则为 false。
     */
    boolean supports(ContextWrapper context);

    /**
     * 执行测试组件业务前执行
     *
     * @param context 测试上下文
     * @param runtime TestElement 运行时数据
     */
    default boolean preHandle(ContextWrapper context, T runtime) {
        return true;
    }


    /**
     * 执行测试组件业务后执行
     *
     * @param context 测试上下文
     * @param runtime TestElement 运行时数据
     */
    default void postHandle(ContextWrapper context, T runtime) {
    }

    /**
     * 拦截器最终处理，一般用于打印日志
     *
     * @param context 测试上下文
     */
    default void afterCompletion(ContextWrapper context) {
    }

}
