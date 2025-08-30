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
import io.github.xiaomisum.ryze.testelement.TestElementConstantsInterface;
import io.github.xiaomisum.ryze.testelement.processor.Processor;
import org.apache.commons.lang3.tuple.Pair;

import java.lang.reflect.Type;
import java.util.Map;
import java.util.Objects;


/**
 * 处理器对象反序列化器基类
 * <p>
 * 该类实现了FastJSON的ObjectReader接口，是前置处理器和后置处理器反序列化器的基类。
 * 提供了通用的处理器对象反序列化逻辑，包括类型识别和配置标准化。
 * </p>
 * <p>
 * 反序列化逻辑：
 * 1. 根据具体子类类型确定是前置处理器还是后置处理器
 * 2. 根据testClass字段查找对应的处理器类
 * 3. 标准化配置结构，将配置属性移到config字段下
 * </p>
 *
 * @author xiaomi
 * Created at 2025/7/19 14:14
 */
public abstract class ProcessorObjectReader implements ObjectReader<Processor>, TestElementConstantsInterface {
    /**
     * 从JSON读取器中读取并构建处理器对象
     * <p>
     * 该方法会解析JSON数据，根据testClass字段确定具体的处理器类型，
     * 并创建对应的处理器对象实例。
     * </p>
     *
     * @param jsonReader JSON读取器
     * @param fieldType  字段类型
     * @param fieldName  字段名称，用于区分前置处理器和后置处理器
     * @param features   特性标志
     * @return 解析后的处理器对象
     * @throws JSONException 当没有匹配的处理器或JSON格式错误时抛出
     */
    @Override
    public Processor readObject(JSONReader jsonReader, Type fieldType, Object fieldName, long features) {
        var testElementMap = jsonReader.readObject();
        var pair = checkTestElement(testElementMap, fieldName);
        standardizeConfig(testElementMap, pair.getRight());
        return JSON.parseObject(JSON.toJSONString(testElementMap), pair.getLeft());
    }

    /**
     * 检查并确定处理器的类型
     * <p>
     * 根据testClass字段在应用配置中查找对应的处理器类。
     * 通过判断当前实例类型确定是前置处理器还是后置处理器。
     * </p>
     *
     * @param testElementMap 处理器Map
     * @param fieldName      字段名称，用于错误信息
     * @return 包含处理器类和键的Pair对象
     * @throws JSONException 当没有匹配的处理器时抛出
     */
    private Pair<Class<? extends Processor>, String> checkTestElement(Map<String, Object> testElementMap, Object fieldName) {
        var keyMap = getClass().equals(PreprocessorObjectReader.class) ?
                ApplicationConfig.getPreprocessorKeyMap() : ApplicationConfig.getPostprocessorKeyMap();
        var key = testElementMap.get(TEST_CLASS).toString().toLowerCase();
        var clazz = keyMap.get(key);
        if (Objects.nonNull(clazz)) {
            return Pair.of(clazz, key);
        }
        throw new JSONException("没有匹配的 %s 处理器, JSON String: %s".formatted(fieldName, JSON.toJSONString(testElementMap)));
    }

    /**
     * 标准化配置结构
     * <p>
     * 该方法会重新组织处理器的结构，将配置属性移到config字段下，
     * 并保留标准配置项如ID、TITLE等。
     * </p>
     *
     * @param elementMap 元素Map
     * @param key        处理器键
     */
    private void standardizeConfig(Map<String, Object> elementMap, String key) {
        if (elementMap.containsKey(CONFIG)) {
            return;
        }
        // 刪除標準配置項
        elementMap.remove(TEST_CLASS); // 删除测试类
        elementMap.remove(VARIABLES);
        var id = elementMap.remove(ID);
        var title = elementMap.remove(TITLE);
        var disabled = elementMap.remove(DISABLED);
        var interceptors = elementMap.remove(INTERCEPTORS);
        var metadata = elementMap.remove(METADATA);
        var extractors = elementMap.remove(EXTRACTORS);
        var config = new JSONObject(elementMap);
        // 清空當前MAP
        elementMap.clear();
        // 重新設置標準化處理器配置
        elementMap.putAll(JSONObject.of(TEST_CLASS, key, ID, id, TITLE, title, DISABLED, disabled, INTERCEPTORS, interceptors, CONFIG, config, METADATA, metadata, EXTRACTORS, extractors));
    }
}