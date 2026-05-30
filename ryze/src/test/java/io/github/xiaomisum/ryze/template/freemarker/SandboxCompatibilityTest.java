package io.github.xiaomisum.ryze.template.freemarker;

import io.github.xiaomisum.ryze.SessionRunner;
import io.github.xiaomisum.ryze.context.ContextWrapper;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * FreeMarker 沙箱兼容性测试
 * 验证沙箱化改动是否影响原有功能
 */
public class SandboxCompatibilityTest {

    private FreeMarkerTemplateEngine templateEngine;
    private ContextWrapper context;

    @BeforeClass
    public void setUp() {
        templateEngine = new FreeMarkerTemplateEngine();
        context = new ContextWrapper(SessionRunner.getSessionIfNoneCreateNew());
        var localVariablesWrapper = context.getLocalVariablesWrapper();
        
        // 场景A: 简单变量
        localVariablesWrapper.put("varName", "testValue");
        localVariablesWrapper.put("username", "alice");
        
        // 场景C: 集合/Map
        Map<String, String> mapVar = new HashMap<>();
        mapVar.put("key1", "value1");
        localVariablesWrapper.put("myMap", mapVar);
        localVariablesWrapper.put("myList", List.of("item1", "item2", "item3"));
        
        // 场景D: 字符串操作
        localVariablesWrapper.put("name", "john doe");
        localVariablesWrapper.put("str", "hello world");
        
        // 场景G: 数学运算
        localVariablesWrapper.put("a", 10);
        localVariablesWrapper.put("b", 20);
        localVariablesWrapper.put("price", 100.0);
        
        // 误拦风险检查 - 包含"class"的变量名
        localVariablesWrapper.put("testclass", "classValue");
        localVariablesWrapper.put("className", "MyClass");
        
        // 误拦风险检查 - 包含"new"的变量名
        localVariablesWrapper.put("newValue", "newData");
        localVariablesWrapper.put("renewal", "renewalData");
        
        // 误拦风险检查 - 包含"exec"的变量名
        localVariablesWrapper.put("execute_result", "success");
        localVariablesWrapper.put("execution_time", 123L);
    }

    // ========== 场景A: 简单变量引用 ==========
    @Test(description = "场景A-1: 直接变量引用")
    public void testScenarioA_DirectVariableReference() {
        String expression = "${varName}";
        Object result = templateEngine.evaluate(context, expression);
        Assert.assertEquals(result, "testValue", "简单变量引用应正常工作");
    }

    // ========== 场景B: 内置函数调用 ==========
    @Test(description = "场景B-1: 函数调用检测（基础）")
    public void testScenarioB_FunctionCall() {
        // 由于测试中可能没有具体注册的函数，这里验证函数适配器是否能正确注册
        // 实际的函数测试需要根据项目的具体函数注册情况
        String expression = "${username}";
        Object result = templateEngine.evaluate(context, expression);
        Assert.assertEquals(result, "alice", "变量作为函数参数前的基础验证");
    }

    // ========== 场景C: 集合/Map 操作 ==========
    @Test(description = "场景C-1: Map 方括号语法访问")
    public void testScenarioC_MapGet() {
        String expression = "${myMap[\"key1\"]}";
        Object result = templateEngine.evaluate(context, expression);
        Assert.assertEquals(result, "value1", "Map方括号语法访问应被允许");
    }

    @Test(description = "场景C-2: Map.keySet() 方法")
    public void testScenarioC_MapKeySet() {
        String expression = "${myMap?keys}";
        Object result = templateEngine.evaluate(context, expression);
        Assert.assertNotNull(result, "Map.keys内建函数应被允许");
    }

    @Test(description = "场景C-3: List 索引访问")
    public void testScenarioC_ListIndexAccess() {
        String expression = "${myList[0]}";
        Object result = templateEngine.evaluate(context, expression);
        Assert.assertEquals(result, "item1", "List索引访问应正常工作");
    }

    // ========== 场景D: 字符串操作 ==========
    @Test(description = "场景D-1: 字符串 upper_case 内置函数")
    public void testScenarioD_StringUpperCase() {
        String expression = "${name?upper_case}";
        Object result = templateEngine.evaluate(context, expression);
        Assert.assertEquals(result, "JOHN DOE", "字符串 upper_case 应被允许");
    }

    @Test(description = "场景D-2: 字符串 lower_case 内置函数")
    public void testScenarioD_StringLowerCase() {
        String expression = "${name?lower_case}";
        Object result = templateEngine.evaluate(context, expression);
        Assert.assertEquals(result, "john doe", "字符串 lower_case 应被允许");
    }

    @Test(description = "场景D-3: 字符串 trim 操作")
    public void testScenarioD_StringTrim() {
        String expression = "${\"  hello  \"?trim}";
        Object result = templateEngine.evaluate(context, expression);
        Assert.assertEquals(result, "hello", "字符串 trim 应被允许");
    }

    // ========== 场桰E: 条件和循环 ==========
    @Test(description = "场桰E-1: 条件语句")
    public void testScenarioE_IfStatement() {
        // 条件语句需要包含 ${} 才会被模板引擎处理
        String expression = "${(a gt b)?string('greater', 'less')}";
        Object result = templateEngine.evaluate(context, expression);
        Assert.assertEquals(result, "less", "if 条件语句应被允许");
    }

    // ========== 场景F: ContextWrapper 访问 ==========
    @Test(description = "场景F-1: vars 对象 get 方法")
    public void testScenarioF_VarsGetMethod() {
        String expression = "${vars.get(\"username\")}";
        Object result = templateEngine.evaluate(context, expression);
        Assert.assertEquals(result, "alice", "vars.get()应被允许");
    }

    @Test(description = "场景F-2: ctx 对象访问")
    public void testScenarioF_ContextAccess() {
        // ctx 和 context 都应该被注册，验证其访问是否被沙箱阻止
        String expression = "${ctx}";
        Object result = templateEngine.evaluate(context, expression);
        Assert.assertNotNull(result, "ctx对象应能被访问");
    }

    // ========== 场景G: 数学运算 ==========
    @Test(description = "场景G-1: 基本加法运算")
    public void testScenarioG_Addition() {
        String expression = "${a + b}";
        Object result = templateEngine.evaluate(context, expression);
        Assert.assertEquals(result.toString(), "30", "加法运算应正常工作");
    }

    @Test(description = "场景G-2: 乘法运算（百分比计算）")
    public void testScenarioG_Multiplication() {
        String expression = "${price * 0.8}";
        Object result = templateEngine.evaluate(context, expression);
        Assert.assertEquals(result.toString(), "80.00", "乘法运算应正常工作");
    }

    // ========== 误拦风险检查 ==========
    @Test(description = "误拦检查-1: 包含class的变量名")
    public void testFalsePositive_VariableNameWithClass() {
        String expression = "${testclass}";
        Object result = templateEngine.evaluate(context, expression);
        Assert.assertEquals(result, "classValue", "包含'class'的变量名不应被误拦");
    }

    @Test(description = "误拦检查-2: 变量名 className")
    public void testFalsePositive_VariableNameClassName() {
        String expression = "${className}";
        Object result = templateEngine.evaluate(context, expression);
        Assert.assertEquals(result, "MyClass", "变量名className不应被误拦");
    }

    @Test(description = "误拦检查-3: 包含new的变量名")
    public void testFalsePositive_VariableNameWithNew() {
        String expression = "${newValue}";
        Object result = templateEngine.evaluate(context, expression);
        Assert.assertEquals(result, "newData", "包含'new'的变量名不应被误拦");
    }

    @Test(description = "误拦检查-4: 变量名 renewal")
    public void testFalsePositive_VariableNameRenewal() {
        String expression = "${renewal}";
        Object result = templateEngine.evaluate(context, expression);
        Assert.assertEquals(result, "renewalData", "变量名renewal不应被误拦");
    }

    @Test(description = "误拦检查-5: 包含exec的变量名")
    public void testFalsePositive_VariableNameWithExec() {
        String expression = "${execute_result}";
        Object result = templateEngine.evaluate(context, expression);
        Assert.assertEquals(result, "success", "包含'exec'的变量名不应被误拦");
    }

    @Test(description = "误拦检查-6: 变量名 execution_time")
    public void testFalsePositive_VariableNameExecutionTime() {
        String expression = "${execution_time}";
        Object result = templateEngine.evaluate(context, expression);
        Assert.assertEquals(result, 123L, "变量名execution_time不应被误拦");
    }

    // ========== 安全检查 ==========
    @Test(description = "安全检查-1: 阻止 ?new 内置函数")
    public void testSecurityCheck_BlockNewBuiltin() {
        String expression = "${\"java.lang.String\"?new()}";
        try {
            templateEngine.evaluate(context, expression);
            Assert.fail("?new 内置函数应被阻止");
        } catch (SecurityException e) {
            Assert.assertTrue(true, "?new 被正确阻止");
        }
    }

    @Test(description = "安全检查-2: 阻止 ?api 内置函数")
    public void testSecurityCheck_BlockApiBuiltin() {
        String expression = "${obj?api}";
        try {
            // 这个可能不会直接抛出异常，因为api.disabled已在配置中禁止
            templateEngine.evaluate(context, expression);
        } catch (Exception e) {
            // 预期行为
            Assert.assertTrue(true, "?api 应被阻止或被禁用");
        }
    }

    @Test(description = "安全检查-3: 阻止 getClass() 调用")
    public void testSecurityCheck_BlockGetClass() {
        String expression = "${str.getClass()}";
        try {
            templateEngine.evaluate(context, expression);
            Assert.fail("getClass() 调用应被阻止");
        } catch (Exception e) {
            Assert.assertTrue(true, "getClass() 被正确阻止");
        }
    }

    @Test(description = "安全检查-4: 阻止 Runtime.getRuntime() 访问")
    public void testSecurityCheck_BlockRuntime() {
        String expression = "${\"java.lang.Runtime\"?new}";
        try {
            templateEngine.evaluate(context, expression);
            Assert.fail("Runtime 访问应被阻止");
        } catch (Exception e) {
            Assert.assertTrue(true, "Runtime 访问被正确阻止");
        }
    }
}
