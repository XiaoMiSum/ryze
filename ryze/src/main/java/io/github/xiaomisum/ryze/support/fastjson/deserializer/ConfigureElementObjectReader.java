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
import com.alibaba.fastjson2.JSONObject;
import com.alibaba.fastjson2.JSONReader;
import com.alibaba.fastjson2.reader.ObjectReader;
import io.github.xiaomisum.ryze.ApplicationConfig;
import io.github.xiaomisum.ryze.testelement.configure.ConfigureElement;
import org.apache.commons.lang3.tuple.Pair;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import static io.github.xiaomisum.ryze.testelement.TestElementConstantsInterface.CONFIG;
import static io.github.xiaomisum.ryze.testelement.TestElementConstantsInterface.TEST_CLASS;
import static io.github.xiaomisum.ryze.testelement.configure.ConfigureElementConstantsInterface.REF_NAME;
import static io.github.xiaomisum.ryze.testelement.configure.ConfigureElementConstantsInterface.VARIABLE_NAME;

/**
 * 配置元件对象反序列化器
 * <p>
 * 该类实现了FastJSON的ObjectReader接口，用于将JSON数据反序列化为ConfigureElement对象。
 * 支持自动识别和创建不同类型的配置元件对象。
 * </p>
 * <p>
 * 反序列化逻辑：
 * 1. 将所有键转换为小写以保证一致性
 * 2. 根据testClass字段查找对应的配置元件类
 * 3. 标准化配置结构，将配置属性移到config字段下
 * 4. 处理variableName和refName的兼容性
 * </p>
 *
 * @author xiaomi
 * Created at 2025/7/19 14:14
 */
@SuppressWarnings({"rawtypes"})
public class ConfigureElementObjectReader implements ObjectReader<ConfigureElement> {
    /**
     * 从JSON读取器中读取并构建配置元件对象
     * <p>
     * 该方法会解析JSON数据，根据testClass字段确定具体的配置元件类型，
     * 并创建对应的配置元件对象实例。
     * </p>
     *
     * @param jsonReader JSON读取器
     * @param fieldType  字段类型
     * @param fieldName  字段名称
     * @param features   特性标志
     * @return 解析后的配置元件对象
     * @throws JSONException 当没有匹配的配置元件或JSON格式错误时抛出
     */
    @Override
    public ConfigureElement readObject(JSONReader jsonReader, Type fieldType, Object fieldName, long features) {
        var testElementMap1 = jsonReader.readObject();
        var elementMap = new HashMap<String, Object>();
        // 转换所有key 为小写
        for (Map.Entry<String, Object> entry : testElementMap1.entrySet()) {
            elementMap.put(entry.getKey().toLowerCase(), entry.getValue());
        }
        var pair = checkTestElement(elementMap);
        standardizeConfig(elementMap, pair.getRight());
        var rawData = JSON.toJSONString(elementMap);
        return JSON.parseObject(rawData, pair.getLeft());
    }

    /**
     * 检查并确定配置元件的类型
     * <p>
     * 根据testClass字段在应用配置中查找对应的配置元件类。
     * </p>
     *
     * @param elementMap 配置元件Map
     * @return 包含配置元件类和键的Pair对象
     * @throws JSONException 当没有匹配的配置元件时抛出
     */
    private Pair<Class<? extends ConfigureElement>, String> checkTestElement(Map<String, Object> elementMap) {
        var keyMap = ApplicationConfig.getConfigureElementKeyMap();
        var key = elementMap.get(TEST_CLASS).toString().toLowerCase();
        var clazz = keyMap.get(key);
        if (Objects.nonNull(clazz)) {
            return Pair.of(clazz, key);
        }
        throw new JSONException("没有匹配的配置元件, JSON String: " + JSON.toJSONString(elementMap));
    }

    /**
     * 标准化配置结构
     * <p>
     * 该方法会重新组织配置元件的结构，将配置属性移到config字段下，
     * 并处理variableName和refName的兼容性。
     * </p>
     *
     * @param elementMap 配置元件Map
     * @param key        配置元件键
     */
    private void standardizeConfig(Map<String, Object> elementMap, String key) {
        elementMap.remove(TEST_CLASS); // 删除测试类
        var variableName = elementMap.remove(VARIABLE_NAME);
        var refName = Objects.isNull(variableName) ? elementMap.remove(REF_NAME) : variableName;
        // 删除 TestElement 的 config 或创建一个新的 config
        var _config = elementMap.containsKey(CONFIG) ? new JSONObject((Map) elementMap.remove(CONFIG)) : new JSONObject();
        if (_config.isEmpty()) {
            // config 没有属性，则将 testElementMap 的所有属性添加到 config 中
            _config.putAll(elementMap);
            elementMap.clear();
        }
        elementMap.putAll(JSONObject.of(TEST_CLASS, key, REF_NAME, refName, CONFIG, _config));
    }
}