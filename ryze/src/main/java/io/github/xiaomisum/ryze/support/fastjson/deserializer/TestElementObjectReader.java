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
import io.github.xiaomisum.ryze.testelement.TestElement;
import io.github.xiaomisum.ryze.support.fastjson.interceptor.JSONInterceptor;
import org.apache.commons.lang3.tuple.Pair;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import static io.github.xiaomisum.ryze.testelement.TestElementConstantsInterface.CONFIG;
import static io.github.xiaomisum.ryze.testelement.TestElementConstantsInterface.TEST_CLASS;

/**
 * 测试元素对象反序列化器
 * <p>
 * 该类实现了FastJSON的ObjectReader接口，用于将JSON数据反序列化为TestElement对象。
 * 支持自动识别和创建不同类型的测试元素对象，如HTTP取样器、Dubbo取样器等。
 * </p>
 * <p>
 * 反序列化逻辑：
 * 1. 将所有键转换为小写以保证一致性
 * 2. 根据testClass字段查找对应的测试元素类
 * 3. 使用JSON拦截器处理配置项的反序列化
 * </p>
 *
 * @author xiaomi
 * Created at 2025/7/19 12:37
 */
@SuppressWarnings({"rawtypes", "unchecked"})
public class TestElementObjectReader implements ObjectReader<TestElement> {

    /**
     * 从JSON读取器中读取并构建测试元素对象
     * <p>
     * 该方法会解析JSON数据，根据testClass字段确定具体的测试元素类型，
     * 并创建对应的测试元素对象实例。
     * </p>
     *
     * @param jsonReader JSON读取器
     * @param fieldType  字段类型
     * @param fieldName  字段名称
     * @param features   特性标志
     * @return 解析后的测试元素对象
     * @throws JSONException 当没有匹配的测试元素或JSON格式错误时抛出
     */
    @Override
    public TestElement readObject(JSONReader jsonReader, Type fieldType, Object fieldName, long features) {
        var testElementMap = jsonReader.readObject();
        var elementMap = new HashMap<String, Object>();
        // 转换所有key 为小写
        for (Map.Entry<String, Object> entry : testElementMap.entrySet()) {
            elementMap.put(entry.getKey().toLowerCase(), entry.getValue());
        }
        var pair = checkTestElement(elementMap);
        JSONInterceptor interceptor = ApplicationConfig.getJsonInterceptorKeyMap().get(pair.getLeft());
        if (interceptor != null) {
            var config = interceptor.deserializeConfigureItem(elementMap.get(CONFIG));
            if (config != null) {
                elementMap.put(CONFIG, config);
            }
        }
        return JSON.parseObject(JSON.toJSONString(elementMap), pair.getLeft());
    }

    /**
     * 检查并确定测试元素的类型
     * <p>
     * 根据testClass字段在应用配置中查找对应的测试元素类。
     * </p>
     *
     * @param elementMap 测试元素Map
     * @return 包含测试元素类和键的Pair对象
     * @throws JSONException 当没有匹配的测试元素时抛出
     */
    private Pair<Class<? extends TestElement>, String> checkTestElement(Map<String, Object> elementMap) {
        var keyMap = ApplicationConfig.getTestElementKeyMap();
        var key = elementMap.get(TEST_CLASS).toString().toLowerCase();
        var clazz = keyMap.get(key);
        if (Objects.nonNull(clazz)) {
            return Pair.of(clazz, key);
        }
        throw new JSONException("没有匹配的测试集或取样器, JSON String: " + JSON.toJSONString(elementMap));
    }


}