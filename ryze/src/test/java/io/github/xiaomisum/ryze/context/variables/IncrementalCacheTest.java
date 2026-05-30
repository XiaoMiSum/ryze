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

package io.github.xiaomisum.ryze.context.variables;

import io.github.xiaomisum.ryze.config.RyzeVariables;
import io.github.xiaomisum.ryze.context.Context;
import io.github.xiaomisum.ryze.context.GlobalContext;
import io.github.xiaomisum.ryze.context.SessionContext;
import io.github.xiaomisum.ryze.testelement.TestElementConfigureGroup;
import io.github.xiaomisum.ryze.testelement.TestElementConstantsInterface;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * AbstractVariablesWrapper 增量缓存策略测试
 *
 * @author xiaomi
 */
public class IncrementalCacheTest {

    private AllVariablesWrapper wrapper;
    private SessionContext sessionContext;

    @BeforeMethod
    public void setUp() {
        GlobalContext globalContext = new GlobalContext();
        TestElementConfigureGroup globalConfig = new TestElementConfigureGroup();
        RyzeVariables globalVars = new RyzeVariables();
        globalVars.put("globalKey", "globalValue");
        globalConfig.put(TestElementConstantsInterface.VARIABLES, globalVars);
        globalContext.setConfigGroup(globalConfig);

        sessionContext = new SessionContext();
        TestElementConfigureGroup sessionConfig = new TestElementConfigureGroup();
        RyzeVariables sessionVars = new RyzeVariables();
        sessionVars.put("sessionKey", "sessionValue");
        sessionConfig.put(TestElementConstantsInterface.VARIABLES, sessionVars);
        sessionContext.setConfigGroup(sessionConfig);

        List<Context> contextChain = new ArrayList<>();
        contextChain.add(globalContext);
        contextChain.add(sessionContext);

        wrapper = new AllVariablesWrapper(contextChain);
    }

    @Test
    public void testCacheHitWithoutModification() {
        // 首次调用构建缓存
        Map<String, Object> first = wrapper.mergeVariables();
        // 第二次调用应命中缓存（返回相同内容的不可变视图）
        Map<String, Object> second = wrapper.mergeVariables();

        // 验证内容一致
        Assert.assertEquals(first, second);
        // 验证包含预期变量
        Assert.assertEquals(first.get("globalKey"), "globalValue");
        Assert.assertEquals(first.get("sessionKey"), "sessionValue");
    }

    @Test
    public void testIncrementalUpdateWithFewDirtyKeys() {
        // 首次调用构建缓存
        Map<String, Object> initial = wrapper.mergeVariables();
        Assert.assertNull(initial.get("newKey1"));

        // put 少量变量（< INCREMENTAL_THRESHOLD = 20），应走增量路径
        wrapper.put("newKey1", "newValue1");
        wrapper.put("newKey2", "newValue2");
        wrapper.put("newKey3", "newValue3");

        // 验证 mergeVariables() 返回的 Map 包含新值
        Map<String, Object> updated = wrapper.mergeVariables();
        Assert.assertEquals(updated.get("newKey1"), "newValue1");
        Assert.assertEquals(updated.get("newKey2"), "newValue2");
        Assert.assertEquals(updated.get("newKey3"), "newValue3");
        // 原有值不丢失
        Assert.assertEquals(updated.get("globalKey"), "globalValue");
        Assert.assertEquals(updated.get("sessionKey"), "sessionValue");
    }

    @Test
    public void testFullRebuildWhenExceedingThreshold() {
        // 首次调用构建缓存
        wrapper.mergeVariables();

        // put 超过 INCREMENTAL_THRESHOLD(20) 个不同 key，应走全量重建路径
        for (int i = 0; i < 25; i++) {
            wrapper.put("bulkKey" + i, "bulkValue" + i);
        }

        // 验证全量重建后结果正确
        Map<String, Object> rebuilt = wrapper.mergeVariables();
        for (int i = 0; i < 25; i++) {
            Assert.assertEquals(rebuilt.get("bulkKey" + i), "bulkValue" + i);
        }
        // 原有值不丢失
        Assert.assertEquals(rebuilt.get("globalKey"), "globalValue");
        Assert.assertEquals(rebuilt.get("sessionKey"), "sessionValue");
    }

    @Test
    public void testDirtyKeysTracking() {
        // 首次构建缓存
        wrapper.mergeVariables();

        // put 操作应记录 dirtyKeys
        wrapper.put("trackedKey", "trackedValue");

        // 再次 mergeVariables 应包含新值（说明 dirtyKeys 被正确追踪）
        Map<String, Object> result = wrapper.mergeVariables();
        Assert.assertEquals(result.get("trackedKey"), "trackedValue");

        // remove 操作也应记录 dirtyKeys
        wrapper.remove("trackedKey");
        Map<String, Object> afterRemove = wrapper.mergeVariables();
        // remove 后该 key 应从合并结果中消失（如果上层上下文也没有）
        Assert.assertNull(afterRemove.get("trackedKey"));
    }

    @Test
    public void testInvalidateCacheClearsDirtyKeys() {
        // 首次构建缓存
        wrapper.mergeVariables();

        // put 一些变量
        wrapper.put("tempKey1", "tempValue1");
        wrapper.put("tempKey2", "tempValue2");

        // 调用 invalidateCache() 后 dirtyKeys 清空，走全量重建
        wrapper.invalidateCache();

        // 调用 mergeVariables，不应抛异常，且结果正确
        Map<String, Object> result = wrapper.mergeVariables();
        Assert.assertEquals(result.get("globalKey"), "globalValue");
        Assert.assertEquals(result.get("sessionKey"), "sessionValue");
        // put 的变量存在于 lastContext 中，所以全量重建后仍然可见
        Assert.assertEquals(result.get("tempKey1"), "tempValue1");
        Assert.assertEquals(result.get("tempKey2"), "tempValue2");
    }

    @Test
    public void testConcurrentReadAccess() throws InterruptedException {
        // 先写入一些数据
        for (int i = 0; i < 10; i++) {
            wrapper.put("concKey" + i, "concValue" + i);
        }
        // 构建初始缓存
        wrapper.mergeVariables();

        // 多线程并发读取 mergeVariables，验证无异常且数据一致
        int threadCount = 10;
        ExecutorService executor = Executors.newFixedThreadPool(threadCount);
        CountDownLatch latch = new CountDownLatch(threadCount);
        AtomicInteger errorCount = new AtomicInteger(0);

        for (int t = 0; t < threadCount; t++) {
            executor.submit(() -> {
                try {
                    for (int i = 0; i < 100; i++) {
                        Map<String, Object> merged = wrapper.mergeVariables();
                        // 验证合并结果不为空
                        if (merged == null || merged.isEmpty()) {
                            errorCount.incrementAndGet();
                        }
                        // 验证原始变量始终存在
                        if (!"globalValue".equals(merged.get("globalKey"))) {
                            errorCount.incrementAndGet();
                        }
                    }
                } catch (Exception e) {
                    errorCount.incrementAndGet();
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await();
        executor.shutdown();

        // 验证无异常发生
        Assert.assertEquals(errorCount.get(), 0, "并发读取中发生了错误");
    }

    @Test
    public void testRapidPutAndMerge() {
        // 单线程快速交替 put 和 mergeVariables，验证缓存机制不出错
        for (int i = 0; i < 100; i++) {
            wrapper.put("rapidKey" + i, "rapidValue" + i);
            Map<String, Object> merged = wrapper.mergeVariables();
            Assert.assertNotNull(merged);
            Assert.assertEquals(merged.get("rapidKey" + i), "rapidValue" + i);
        }

        // 最终验证所有值
        Map<String, Object> finalResult = wrapper.mergeVariables();
        Assert.assertEquals(finalResult.get("globalKey"), "globalValue");
        Assert.assertEquals(finalResult.get("sessionKey"), "sessionValue");
        for (int i = 0; i < 100; i++) {
            Assert.assertEquals(finalResult.get("rapidKey" + i), "rapidValue" + i);
        }
    }

    @Test
    public void testVariableOverrideAcrossContexts() {
        // 在 session 上下文中覆盖 global 变量
        wrapper.put("globalKey", "overriddenValue");

        Map<String, Object> result = wrapper.mergeVariables();
        // session 上下文的值应覆盖 global 上下文
        Assert.assertEquals(result.get("globalKey"), "overriddenValue");
    }
}
