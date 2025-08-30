/*
 *
 *  * The MIT License (MIT)
 *  *
 *  * Copyright (c) 2025.  Lorem XiaoMiSum (mi_xiao@qq.com)
 *  *
 *  * Permission is hereby granted, free of charge, to any person obtaining
 *  * a copy of this software and associated documentation files (the
 *  * 'Software'), to deal in the Software without restriction, including
 *  * without limitation the rights to use, copy, modify, merge, publish,
 *  * distribute, sublicense, and/or sell copies of the Software, and to
 *  * permit persons to whom the Software is furnished to do so, subject to
 *  * the following conditions:
 *  *
 *  * The above copyright notice and this permission notice shall be
 *  * included in all copies or substantial portions of the Software.
 *  *
 *  * THE SOFTWARE IS PROVIDED 'AS IS', WITHOUT WARRANTY OF ANY KIND,
 *  * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
 *  * MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.
 *  * IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY
 *  * CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT,
 *  * TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE
 *  * SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 *
 *
 */

package io.github.xiaomisum.ryze;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import io.github.xiaomisum.ryze.support.TestDataLoader;

import java.util.Map;

import static io.github.xiaomisum.ryze.testelement.TestElementConstantsInterface.TEST_CLASS;

/**
 * Ryze测试框架入口类
 * <p>
 * 该类是Ryze测试框架的主要入口，提供启动测试执行的方法。主要功能包括：
 * 1. 提供多种方式启动测试（文件路径、Map数据、JsonTree对象）
 * 2. 管理测试会话生命周期
 * 3. 解析测试用例并执行测试
 * </p>
 *
 * @author xiaomi
 */
@SuppressWarnings({"unchecked"})
public class Ryze {


    private final JsonTree testcase;

    /**
     * 构造函数，初始化测试用例
     *
     * @param testcase 标准化后的测试用例对象
     */
    public Ryze(JsonTree testcase) {
        this.testcase = testcase;
    }

    /**
     * 通过文件路径启动测试执行
     * <p>
     * 执行流程：
     * 1. 从指定文件路径加载测试数据
     * 2. 调用重载方法继续执行
     * </p>
     *
     * @param filePath 测试用例文件路径
     * @return 测试执行结果
     */
    public static Result start(String filePath) {
        JSONObject testcase = TestDataLoader.toJavaObject(filePath, JSONObject.class);
        return start(testcase);
    }

    /**
     * 通过Map数据启动测试执行
     * <p>
     * 执行流程：
     * 1. 将Map数据转换为JsonTree对象
     * 2. 调用重载方法继续执行
     * </p>
     *
     * @param testcase Map格式的测试用例数据
     * @return 测试执行结果
     */
    public static Result start(Map<String, Object> testcase) {
        return start(new JsonTree(testcase));
    }

    /**
     * 通过JsonTree对象启动测试执行
     * <p>
     * 执行流程：
     * 1. 创建Ryze实例并执行测试
     * 2. 测试执行完成后清理会话资源
     * </p>
     *
     * @param testcase 标准化后的测试用例对象
     * @return 测试执行结果
     */
    public static Result start(JsonTree testcase) {
        try {
            return new Ryze(testcase).runTest();
        } finally {
            SessionRunner.removeSession();
        }
    }

    /**
     * 执行测试的核心方法
     * <p>
     * 执行流程：
     * 1. 根据测试类标识获取对应的测试元素类
     * 2. 将测试用例数据转换为对应的测试元素对象
     * 3. 获取或创建测试会话并执行测试
     * </p>
     *
     * @return 测试执行结果
     */
    private Result runTest() {
        var clazz = ApplicationConfig.getTestElementKeyMap().get(testcase.getString(TEST_CLASS));
        return SessionRunner.getSessionIfNoneCreateNew().runTest(JSON.parseObject(testcase.toJSONString(), clazz));
    }
}