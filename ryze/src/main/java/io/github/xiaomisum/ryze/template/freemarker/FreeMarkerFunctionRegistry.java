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

import io.github.xiaomisum.ryze.ApplicationConfig;
import io.github.xiaomisum.ryze.function.Function;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * FreeMarker 函数注册表缓存。
 * 预加载并缓存所有已注册的 Ryze 函数，避免每次 evaluate() 都遍历和创建适配器。
 *
 * @author xiaomi
 */
public final class FreeMarkerFunctionRegistry {

    private static volatile List<Function> cachedFunctions;
    private static volatile Map<String, Function> functionMap;

    private FreeMarkerFunctionRegistry() {
    }

    /**
     * 获取所有已注册函数（懒加载 + 缓存）
     */
    public static List<Function> getFunctions() {
        if (cachedFunctions == null) {
            synchronized (FreeMarkerFunctionRegistry.class) {
                if (cachedFunctions == null) {
                    cachedFunctions = ApplicationConfig.getFunctions();
                    var map = new ConcurrentHashMap<String, Function>();
                    for (Function f : cachedFunctions) {
                        map.put(f.key(), f);
                    }
                    functionMap = Collections.unmodifiableMap(map);
                }
            }
        }
        return cachedFunctions;
    }

    /**
     * 获取函数名到 Function 对象的映射
     */
    public static Map<String, Function> getFunctionMap() {
        getFunctions(); // 确保已初始化
        return functionMap;
    }
}
