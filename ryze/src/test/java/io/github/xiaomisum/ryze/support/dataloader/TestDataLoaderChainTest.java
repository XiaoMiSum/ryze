package io.github.xiaomisum.ryze.support.dataloader;

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
}