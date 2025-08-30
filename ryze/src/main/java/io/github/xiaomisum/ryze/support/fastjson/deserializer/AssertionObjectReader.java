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

package io.github.xiaomisum.ryze.support.fastjson.deserializer;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONException;
import com.alibaba.fastjson2.JSONReader;
import com.alibaba.fastjson2.reader.ObjectReader;
import io.github.xiaomisum.ryze.ApplicationConfig;
import io.github.xiaomisum.ryze.assertion.Assertion;
import io.github.xiaomisum.ryze.assertion.builtin.JSONAssertion;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;

import java.lang.reflect.Type;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

import static io.github.xiaomisum.ryze.assertion.AssertionConstantsInterface.RULE;
import static io.github.xiaomisum.ryze.testelement.TestElementConstantsInterface.TEST_CLASS;

/**
 * 断言对象反序列化器
 * <p>
 * 该类实现了FastJSON的ObjectReader接口，用于将JSON数据反序列化为Assertion对象。
 * 支持自动识别和创建不同类型的断言对象，如JSON断言等。
 * </p>
 * <p>
 * 反序列化逻辑：
 * 1. 如果未指定testClass，则默认使用JSONAssertion
 * 2. 如果未指定rule，则默认使用"=="
 * 3. 根据testClass查找对应的断言类并创建实例
 * </p>
 *
 * @author xiaomi
 * Created at 2025/7/19 14:54
 */
public class AssertionObjectReader implements ObjectReader<Assertion> {

    /**
     * 从JSON读取器中读取并构建断言对象
     * <p>
     * 该方法会解析JSON数据，根据testClass字段确定具体的断言类型，
     * 并创建对应的断言对象实例。
     * </p>
     *
     * @param jsonReader JSON读取器
     * @param fieldType  字段类型
     * @param fieldName  字段名称
     * @param features   特性标志
     * @return 解析后的断言对象
     * @throws JSONException 当没有匹配的断言器或JSON格式错误时抛出
     */
    @Override
    public Assertion readObject(JSONReader jsonReader, Type fieldType, Object fieldName, long features) {
        var testElementMap = jsonReader.readObject();
        var testClass = testElementMap.get(TEST_CLASS);
        if (Objects.isNull(testClass) || StringUtils.isBlank(testClass.toString())) {
            testElementMap.put(TEST_CLASS, JSONAssertion.class.getSimpleName());
        }
        var pair = checkTestElement(testElementMap);
        var rule = testElementMap.get(RULE);
        if (Objects.isNull(rule) || StringUtils.isBlank(rule.toString())) {
            testElementMap.put(RULE, "==");
        }
        return JSON.parseObject(JSON.toJSONString(testElementMap), pair.getLeft());
    }

    /**
     * 检查并确定测试元素的类型
     * <p>
     * 根据testClass字段在应用配置中查找对应的断言类。
     * </p>
     *
     * @param testElementMap 测试元素Map
     * @return 包含断言类和键的Pair对象
     * @throws JSONException 当没有匹配的断言器时抛出
     */
    private Pair<Class<? extends Assertion>, String> checkTestElement(Map<String, Object> testElementMap) {
        var keyMap = ApplicationConfig.getAssertionKeyMap();
        var key = testElementMap.get(TEST_CLASS).toString().toLowerCase(Locale.ROOT);
        var clazz = keyMap.get(key);
        if (Objects.nonNull(clazz)) {
            return Pair.of(clazz, key);
        }
        throw new JSONException("没有匹配的验证器, JSON String: " + JSON.toJSONString(testElementMap));
    }

}