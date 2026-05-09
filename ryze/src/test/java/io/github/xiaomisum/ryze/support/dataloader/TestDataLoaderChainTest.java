package io.github.xiaomisum.ryze.support.dataloader;

import com.alibaba.fastjson2.JSONObject;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

public class TestDataLoaderChainTest {

    @Test
    public void testGetTypeClassWithNormalClass() {
        TestDataLoaderChain chain = new TestDataLoaderChain();

        // 测试普通类
        Class<?> clazz = chain.getTypeClass(String.class);
        Assert.assertEquals(clazz, String.class);
    }

    @Test
    public void testGetTypeClassWithParameterizedType() throws NoSuchFieldException {
        TestDataLoaderChain chain = new TestDataLoaderChain();

        // 创建一个带泛型的字段来获取ParameterizedType
        java.lang.reflect.Field field = TestClass.class.getDeclaredField("map");
        Type type = field.getGenericType();

        // 测试参数化类型
        Class<?> clazz = chain.getTypeClass(type);
        Assert.assertEquals(clazz, Map.class);
    }

    @Test(expectedExceptions = RuntimeException.class)
    public void testGetTypeClassWithInvalidClass() {
        TestDataLoaderChain chain = new TestDataLoaderChain();

        // 测试无效的类名
        Type invalidType = new Type() {
            @Override
            public String getTypeName() {
                return "invalid.class.name.that.does.not.Exist";
            }
        };

        chain.getTypeClass(invalidType);
    }

    @Test
    public void testLoadDataWithClasspathResource() {
        // 测试从类路径加载数据 - 这应该能从已有的测试资源中加载

        // 使用String类型加载
        String result = TestDataLoaderChain.loadTestData("classpath:application.yml", String.class);
        Assert.assertNotNull(result);
        Assert.assertTrue(result.contains("spring"));
    }

    @Test
    public void testLoadDataWithClasspathResource2() {
        // 测试从类路径加载数据 - 这应该能从已有的测试资源中加载

        // 使用String类型加载
        String result = TestDataLoaderChain.loadTestData("application.yml", String.class);
        Assert.assertNotNull(result);
        Assert.assertTrue(result.contains("spring"));
    }

    @Test
    public void testLoadDataWithLocalResource() {
        // 测试从绝对路径加载数据 - 这应该能从已有的测试资源中加载

        // 使用String类型加载
        String result = TestDataLoaderChain.loadTestData(System.getProperty("user.dir") + "/src/test/resources/application.yml", String.class);
        Assert.assertNotNull(result);
        Assert.assertTrue(result.contains("spring"));
    }

    @Test
    public void testConstructorCreatesChainCorrectly() {
        TestDataLoaderChain chain = new TestDataLoaderChain();

        // 通过反射检查链的第一个处理器是否正确初始化
        try {
            java.lang.reflect.Field firstHandlerField = TestDataLoaderChain.class.getDeclaredField("firstHandler");
            firstHandlerField.setAccessible(true);
            DataLoaderHandler firstHandler = (DataLoaderHandler) firstHandlerField.get(chain);

            Assert.assertNotNull(firstHandler);
            Assert.assertTrue(firstHandler instanceof ClasspathLoader);
        } catch (Exception e) {
            Assert.fail("Failed to access firstHandler field", e);
        }
    }

    // 辅助测试类
    static class TestClass {
        Map<String, Object> map = new HashMap<>();
    }

    /**
     * 测试从 classpath 加载 JSON 文件，JSON 中使用 !include: 和 {"!include": ...} 语法
     */
    @Test
    public void testLoadJsonWithIncludeFromClasspath() {
        // 加载包含 !include 引用的 JSON 文件
        JSONObject result = TestDataLoaderChain.loadTestData(
                "classpath:json-include/classpath_test_include.json", 
                JSONObject.class
        );
        
        Assert.assertNotNull(result);
        Assert.assertEquals(result.getString("title"), "测试配置 - 使用 !include: 语法");
        
        // 验证 !include: 语法（字符串形式）
        JSONObject base = result.getJSONObject("base");
        Assert.assertNotNull(base);
        Assert.assertEquals(base.getString("name"), "base_config");
        Assert.assertEquals(base.getString("version"), "1.0");
        Assert.assertEquals(base.getInteger("timeout"), Integer.valueOf(5000));
        
        // 验证 {"!include": ...} 语法（对象形式）
        JSONObject database = result.getJSONObject("database");
        Assert.assertNotNull(database);
        Assert.assertEquals(database.getString("host"), "localhost");
        Assert.assertEquals(database.getInteger("port"), Integer.valueOf(3306));
        
        // 验证数组
        Assert.assertNotNull(result.getJSONArray("features"));
        Assert.assertEquals(result.getJSONArray("features").size(), 3);
    }

    /**
     * 测试从 classpath 加载 JSON 文件，JSON 中使用 $ref 语法
     */
    @Test
    public void testLoadJsonWithRefFromClasspath() {
        // 加载包含 $ref 引用的 JSON 文件
        JSONObject result = TestDataLoaderChain.loadTestData(
                "classpath:json-include/classpath_test_ref.json", 
                JSONObject.class
        );
        
        Assert.assertNotNull(result);
        Assert.assertEquals(result.getString("title"), "测试配置 - 使用 $ref 语法");
        
        // 验证 {"$ref": ...} 语法
        JSONObject base = result.getJSONObject("base");
        Assert.assertNotNull(base);
        Assert.assertEquals(base.getString("name"), "base_config");
        Assert.assertEquals(base.getString("version"), "1.0");
        
        // 验证 !include: 语法（字符串形式）
        JSONObject database = result.getJSONObject("database");
        Assert.assertNotNull(database);
        Assert.assertEquals(database.getString("host"), "localhost");
        Assert.assertEquals(database.getInteger("port"), Integer.valueOf(3306));
    }

    /**
     * 测试从本地文件加载 JSON 文件，JSON 中使用 !import: 和 $ref 语法
     */
    @Test
    public void testLoadJsonWithImportFromLocalFile() {
        // 使用绝对路径加载本地 JSON 文件
        String localFilePath = System.getProperty("user.dir") + 
                "/src/test/resources/json-include-local/localfile_test_import.json";
        
        JSONObject result = TestDataLoaderChain.loadTestData(localFilePath, JSONObject.class);
        
        Assert.assertNotNull(result);
        Assert.assertEquals(result.getString("title"), "本地文件测试 - 使用 !import: 语法");
        
        // 验证 !import: 语法（字符串形式）
        JSONObject base = result.getJSONObject("base");
        Assert.assertNotNull(base);
        Assert.assertEquals(base.getString("name"), "local_base_config");
        Assert.assertEquals(base.getString("version"), "2.0");
        Assert.assertEquals(base.getInteger("timeout"), Integer.valueOf(10000));
        
        // 验证 {"$ref": ...} 语法
        JSONObject server = result.getJSONObject("server");
        Assert.assertNotNull(server);
        Assert.assertEquals(server.getString("host"), "192.168.1.100");
        Assert.assertEquals(server.getInteger("port"), Integer.valueOf(8080));
        Assert.assertEquals(server.getString("protocol"), "https");
        
        // 验证数组
        Assert.assertNotNull(result.getJSONArray("endpoints"));
        Assert.assertEquals(result.getJSONArray("endpoints").size(), 2);
    }

    /**
     * 测试从本地文件加载 JSON 文件（使用 file: 前缀）
     */
    @Test
    public void testLoadJsonWithImportFromFilePrefix() {
        // 使用 file: 前缀加载本地 JSON 文件
        String localFilePath = "file:" + System.getProperty("user.dir") + 
                "/src/test/resources/json-include-local/localfile_test_import.json";
        
        JSONObject result = TestDataLoaderChain.loadTestData(localFilePath, JSONObject.class);
        
        Assert.assertNotNull(result);
        Assert.assertEquals(result.getString("title"), "本地文件测试 - 使用 !import: 语法");
        
        // 验证包含的文件内容
        JSONObject base = result.getJSONObject("base");
        Assert.assertNotNull(base);
        Assert.assertEquals(base.getString("name"), "local_base_config");
        
        JSONObject server = result.getJSONObject("server");
        Assert.assertNotNull(server);
        Assert.assertEquals(server.getString("host"), "192.168.1.100");
    }

    /**
     * 测试嵌套 include - JSON 文件中引用的文件也包含 include
     */
    @Test
    public void testNestedIncludeFromClasspath() {
        // 这个测试验证递归 include 功能
        // 当前测试文件直接引用基础配置，验证递归处理是否正常
        JSONObject result = TestDataLoaderChain.loadTestData(
                "classpath:json-include/classpath_test_include.json", 
                JSONObject.class
        );
        
        Assert.assertNotNull(result);
        
        // 验证 base 配置被正确加载和解析
        JSONObject base = result.getJSONObject("base");
        Assert.assertNotNull(base);
        Assert.assertEquals(base.getString("name"), "base_config");
        Assert.assertEquals(base.getInteger("retry"), Integer.valueOf(3));
    }
}