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
import io.github.xiaomisum.ryze.protocol.http.config.HTTPConfigureItem;
import io.github.xiaomisum.ryze.template.TemplateEngine;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FreeMarkerTemplateEngineTest {

    private TemplateEngine templateEngine;
    private ContextWrapper context;

    @BeforeClass
    public void setUp() {
        templateEngine = new FreeMarkerTemplateEngine();
        context = new ContextWrapper(SessionRunner.getSessionIfNoneCreateNew());
        var localVariablesWrapper = context.getLocalVariablesWrapper();
        localVariablesWrapper.put("username", "testuser");
        localVariablesWrapper.put("age", 25);
        localVariablesWrapper.put("firstName", "John");
        localVariablesWrapper.put("lastName", "Doe");
    }

    @Test
    public void testEvaluateWithNullExpression() {
        Object result = templateEngine.evaluate(context, (Object) null);
        Assert.assertNull(result);
    }

    @Test
    public void testEvaluateWithNoExpression() {
        String expression = "plain text";
        Object result = templateEngine.evaluate(context, expression);
        Assert.assertEquals(result, expression);
    }

    @Test
    public void testEvaluateWithSimpleVariable() {
        // 测试简单变量引用
        String expression = "${username}";
        Object result = templateEngine.evaluate(context, expression);
        Assert.assertEquals(result, "testuser");

        // 测试数字变量引用
        expression = "${age}";
        result = templateEngine.evaluate(context, expression);
        Assert.assertEquals(result, 25);
    }

    @Test
    public void testEvaluateWithModelAndSimpleVariable() {
        // 准备测试数据
        Map<String, Object> model = new HashMap<>();
        model.put("product", "laptop");
        model.put("price", 1200.50);

        // 测试简单变量引用
        String expression = "${product}";
        Object result = templateEngine.evaluate(model, expression);
        Assert.assertEquals(result, "laptop");

        // 测试数字变量引用
        expression = "${price}";
        result = templateEngine.evaluate(model, expression);
        Assert.assertEquals(result, 1200.50);
    }

    @Test
    public void testEvaluateWithComplexExpression() {
        // 测试复杂表达式
        String expression = "${firstName + \" \" + lastName}";
        Object result = templateEngine.evaluate(context, expression);
        Assert.assertEquals(result, "John Doe");
    }

    @Test
    public void testEvaluateWithModelAndComplexExpression() {
        // 准备测试数据
        Map<String, Object> model = new HashMap<>();
        model.put("quantity", 3);
        model.put("unitPrice", 15.75);

        // 测试算术表达式
        String expression = "${quantity * unitPrice}";
        Object result = templateEngine.evaluate(model, expression);
        // result 转为 string类型
        Assert.assertEquals(result.toString(), "47.25");
    }

    @Test
    public void testEvaluateWithComplexExpression2() {
        // 准备测试数据
        var localVariablesWrapper = context.getLocalVariablesWrapper();
        localVariablesWrapper.put("quantity", 3);
        localVariablesWrapper.put("unitPrice", 15.75);

        // 测试算术表达式
        Map<String, Object> object = new HashMap<>();
        object.put("expression", "${quantity * unitPrice}");
        Map<String, Object> result = templateEngine.evaluate(context, object);
        // result 转为 string类型
        Assert.assertEquals(result.get("expression").toString(), "47.25");

        // 清理数据
        localVariablesWrapper.remove("quantity");
        localVariablesWrapper.remove("unitPrice");
    }

    @Test
    public void testEvaluateWithComplexExpression3() {
        // 准备测试数据
        var localVariablesWrapper = context.getLocalVariablesWrapper();
        localVariablesWrapper.put("quantity", 4);
        localVariablesWrapper.put("unitPrice", 15.75);

        // 测试算术表达式
        List<Object> list = List.of("${quantity * unitPrice}");
        List<Object> result = templateEngine.evaluate(context, list);
        // result 转为 string类型
        Assert.assertEquals(result.getFirst().toString(), "63.00");

        // 清理数据
        localVariablesWrapper.remove("quantity");
        localVariablesWrapper.remove("unitPrice");
    }

    @Test
    public void testEvaluateWithComplexExpression4() {
        // 测试算术表达式
        HTTPConfigureItem config = new HTTPConfigureItem();
        config.setHost("${username}");
        templateEngine.evaluate(context, config);
        // result 转为 string类型
        Assert.assertEquals(config.getHost(), context.getLocalVariablesWrapper().get("username"));
    }

    @Test
    public void testEvaluateWithNonExistentVariable() {
        // 测试不存在的变量
        String expression = "${nonExistentVar}";
        Object result = templateEngine.evaluate(context, expression);
        Assert.assertNull(result);
    }

    @Test
    public void testTemplateEngineHasExpression() {
        // 测试包含表达式的字符串
        String templateWithExpression = "Hello ${name}";
        Assert.assertTrue(TemplateEngine.hasExpression(templateWithExpression));

        // 测试不包含表达式的字符串
        String templateWithoutExpression = "Hello World";
        Assert.assertFalse(TemplateEngine.hasExpression(templateWithoutExpression));
    }

    @Test
    public void testTemplateEngineNoneExpression() {
        // 测试不包含表达式的字符串
        String templateWithoutExpression = "Hello World";
        Assert.assertTrue(TemplateEngine.noneExpression(templateWithoutExpression));

        // 测试包含表达式的字符串
        String templateWithExpression = "Hello ${name}";
        Assert.assertFalse(TemplateEngine.noneExpression(templateWithExpression));
    }
}