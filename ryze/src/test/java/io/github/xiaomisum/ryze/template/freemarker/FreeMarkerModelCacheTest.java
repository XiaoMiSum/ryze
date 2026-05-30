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

import io.github.xiaomisum.ryze.SessionRunner;
import io.github.xiaomisum.ryze.context.ContextWrapper;
import io.github.xiaomisum.ryze.template.TemplateEngine;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.HashMap;
import java.util.Map;

/**
 * FreeMarkerTemplateEngine 模型缓存（ThreadLocal）测试
 *
 * @author xiaomi
 */
public class FreeMarkerModelCacheTest {

    private FreeMarkerTemplateEngine engine;
    private ContextWrapper context;

    @BeforeMethod
    public void setUp() {
        engine = new FreeMarkerTemplateEngine();
        SessionRunner sessionRunner = SessionRunner.getSessionIfNoneCreateNew();
        context = new ContextWrapper(sessionRunner);
    }

    @AfterMethod
    public void tearDown() {
        FreeMarkerTemplateEngine.clearThreadLocalCache();
        SessionRunner.removeSession();
    }

    @Test
    public void testEvaluateSimpleExpression() {
        // 使用模型缓存后，简单表达式 ${varName} 仍然正确解析
        context.getLocalVariablesWrapper().put("testVar", "hello");

        Object result = engine.evaluate(context, "${testVar}");
        Assert.assertEquals(result, "hello");
    }

    @Test
    public void testEvaluateMultipleExpressionsInSameThread() {
        // 同一线程连续多次 evaluate，验证结果正确（model 正确 clear+重填）
        var localVars = context.getLocalVariablesWrapper();
        localVars.put("var1", "value1");
        localVars.put("var2", "value2");

        Object result1 = engine.evaluate(context, "${var1}");
        Assert.assertEquals(result1, "value1");

        Object result2 = engine.evaluate(context, "${var2}");
        Assert.assertEquals(result2, "value2");

        // 修改变量后再评估
        localVars.put("var1", "updated");
        Object result3 = engine.evaluate(context, "${var1}");
        Assert.assertEquals(result3, "updated");
    }

    @Test
    public void testClearThreadLocalCache() {
        // 调用 clearThreadLocalCache() 不抛异常
        FreeMarkerTemplateEngine.clearThreadLocalCache();

        // 清理后仍可正常使用
        context.getLocalVariablesWrapper().put("afterClear", "works");
        Object result = engine.evaluate(context, "${afterClear}");
        Assert.assertEquals(result, "works");
    }

    @Test
    public void testEvaluateWithNullExpression() {
        // expression 为 null 时的安全处理
        Map<String, Object> model = new HashMap<>();
        model.put("key", "value");

        Object result = engine.evaluate(model, null);
        Assert.assertNull(result);
    }

    @Test
    public void testEvaluateWithNullContext() {
        // context 为 null 时使用 model 方式计算
        Map<String, Object> model = new HashMap<>();
        model.put("name", "world");

        Object result = engine.evaluate(model, "${name}");
        Assert.assertEquals(result, "world");
    }

    @Test
    public void testModelCacheIsolationBetweenEvaluations() {
        // 确保两次 evaluate 之间 model 不会残留数据
        var localVars = context.getLocalVariablesWrapper();
        localVars.put("existVar", "exists");

        Object result1 = engine.evaluate(context, "${existVar}");
        Assert.assertEquals(result1, "exists");

        // 移除变量后，应返回原始表达式（因为变量不存在了）
        localVars.remove("existVar");
        Object result2 = engine.evaluate(context, "${existVar}");
        Assert.assertEquals(result2, "${existVar}", "变量移除后应返回原始表达式");
    }

    @Test
    public void testEvaluateWithPlainText() {
        // 纯文本不含表达式，应直接返回
        Object result = engine.evaluate(context, "plain text without expression");
        Assert.assertEquals(result, "plain text without expression");
    }
}
