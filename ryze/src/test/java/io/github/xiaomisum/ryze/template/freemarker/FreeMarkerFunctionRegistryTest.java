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

import io.github.xiaomisum.ryze.function.Function;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.List;
import java.util.Map;

/**
 * FreeMarkerFunctionRegistry 函数注册表缓存测试
 *
 * @author xiaomi
 */
public class FreeMarkerFunctionRegistryTest {

    @Test
    public void testGetFunctionsReturnsNonEmptyList() {
        // 验证返回的函数列表非空
        List<Function> functions = FreeMarkerFunctionRegistry.getFunctions();
        Assert.assertNotNull(functions);
        Assert.assertFalse(functions.isEmpty(), "函数列表不应为空");
    }

    @Test
    public void testGetFunctionsCacheConsistency() {
        // 多次调用返回相同引用（证明缓存生效）
        List<Function> first = FreeMarkerFunctionRegistry.getFunctions();
        List<Function> second = FreeMarkerFunctionRegistry.getFunctions();
        Assert.assertSame(first, second, "多次调用 getFunctions() 应返回相同引用");
    }

    @Test
    public void testGetFunctionMapContainsExpectedKeys() {
        // 验证 functionMap 包含已知函数
        Map<String, Function> functionMap = FreeMarkerFunctionRegistry.getFunctionMap();
        Assert.assertNotNull(functionMap);
        Assert.assertFalse(functionMap.isEmpty(), "函数映射不应为空");

        // 验证包含一些已知函数 key
        Assert.assertTrue(functionMap.containsKey("uuid"), "应包含 uuid 函数");
        Assert.assertTrue(functionMap.containsKey("timestamp"), "应包含 timestamp 函数");
        Assert.assertTrue(functionMap.containsKey("random"), "应包含 random 函数");
    }

    @Test
    public void testGetFunctionMapCacheConsistency() {
        // 多次调用 getFunctionMap 应返回相同引用
        Map<String, Function> first = FreeMarkerFunctionRegistry.getFunctionMap();
        Map<String, Function> second = FreeMarkerFunctionRegistry.getFunctionMap();
        Assert.assertSame(first, second, "多次调用 getFunctionMap() 应返回相同引用");
    }

    @Test
    public void testFunctionMapAndListConsistency() {
        // 验证 functionMap 和 functionList 内容一致
        List<Function> functions = FreeMarkerFunctionRegistry.getFunctions();
        Map<String, Function> functionMap = FreeMarkerFunctionRegistry.getFunctionMap();

        // functionMap 的 size 应等于 functions 的 size
        Assert.assertEquals(functionMap.size(), functions.size(),
                "functionMap 和 functions 列表大小应一致");

        // 每个函数都应在 map 中可查到
        for (Function f : functions) {
            Assert.assertTrue(functionMap.containsKey(f.key()),
                    "functionMap 应包含函数: " + f.key());
            Assert.assertSame(functionMap.get(f.key()), f,
                    "functionMap 中的函数应与列表中的相同实例");
        }
    }
}
