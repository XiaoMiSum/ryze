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

package io.github.xiaomisum.ryze.function.builtin;

import io.github.xiaomisum.ryze.context.ContextWrapper;
import io.github.xiaomisum.ryze.function.Args;
import io.github.xiaomisum.ryze.function.Function;

/**
 * 系统属性获取函数实现类
 *
 * <p>该类实现了Function接口，用于获取系统属性值。可以根据属性名获取系统属性，
 * 如果属性不存在则可以指定默认值。</p>
 *
 * <p>使用示例:
 * <pre>
 * ${property("java.version")}
 * ${property("user.home", "/default/path")}
 * </pre>
 * </p>
 *
 * @author xiaomi
 * @see Function
 * @see System#getProperty(String)
 * @see System#getProperty(String, String)
 */
public class Property implements Function {
    /**
     * 获取函数键名
     *
     * @return 函数键名 "property"
     */
    @Override
    public String key() {
        return "property";
    }

    /**
     * 执行系统属性获取操作
     *
     * <p>根据提供的参数获取系统属性值。支持1个或2个参数：
     * <ol>
     *   <li>propertyName: 属性名称，必需参数</li>
     *   <li>defaultValue: 默认值，可选参数</li>
     * </ol>
     * </p>
     *
     * <p>使用示例：
     * <pre>
     * ${property("java.version")}                    // 获取java.version属性值
     * ${property("user.home", "/default/path")}      // 获取user.home属性值，如果不存在则返回默认值
     * </pre>
     * </p>
     *
     * @param context 执行上下文环境
     * @param args    函数参数，包含属性名称和可选的默认值
     * @return 系统属性值或默认值
     * @throws IllegalArgumentException 当参数数量不是1个或2个时抛出异常
     */
    @Override
    public Object execute(ContextWrapper context, Args args) {
        checkMethodArgCount(args, 1, 2);
        return args.size() == 1 ? System.getProperty(args.getFirstString()) : System.getProperty(args.getFirstString(), args.getLastString());
    }
}