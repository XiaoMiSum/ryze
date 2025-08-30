package io.github.xiaomisum.ryze.support.fastjson.deserializer;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONException;
import com.alibaba.fastjson2.JSONReader;
import com.alibaba.fastjson2.reader.ObjectReader;
import io.github.xiaomisum.ryze.ApplicationConfig;
import io.github.xiaomisum.ryze.interceptor.RyzeInterceptor;
import io.github.xiaomisum.ryze.support.Collections;
import org.apache.commons.lang3.tuple.Pair;

import java.lang.reflect.Type;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import static io.github.xiaomisum.ryze.testelement.TestElementConstantsInterface.TEST_CLASS;

/**
 * 拦截器对象反序列化器
 * <p>
 * 该类实现了FastJSON的ObjectReader接口，用于将JSON数据反序列化为RyzeInterceptor对象。
 * 支持多种格式的拦截器配置，包括简单字符串和复杂对象形式。
 * </p>
 * <p>
 * 支持的配置格式：
 * <ul>
 *   <li>简单字符串：直接指定拦截器类名</li>
 *   <li>对象形式：{"InterceptorName": {配置属性}}</li>
 *   <li>标准形式：{"testClass": "InterceptorName", ...}</li>
 * </ul>
 * </p>
 *
 * @author xiaomi
 */
@SuppressWarnings({"unchecked", "rawtypes"})
public class InterceptorObjectReader implements ObjectReader<RyzeInterceptor> {

    /**
     * 从JSON读取器中读取并构建拦截器对象
     * <p>
     * 该方法会解析JSON数据，根据不同的配置格式提取拦截器信息，
     * 并创建对应的拦截器对象实例。
     * </p>
     *
     * @param jsonReader JSON读取器
     * @param fieldType  字段类型
     * @param fieldName  字段名称
     * @param features   特性标志
     * @return 解析后的拦截器对象
     * @throws JSONException 当没有匹配的拦截器或JSON格式错误时抛出
     */
    @Override
    public RyzeInterceptor readObject(JSONReader jsonReader, Type fieldType, Object fieldName, long features) {
        Object anyData = jsonReader.readAny();
        Map<String, Object> testElementMap = Collections.newHashMap();
        standardizeInterceptors(testElementMap, anyData);
        var pair = checkInterceptor(testElementMap);
        return JSON.parseObject(JSON.toJSONString(testElementMap), pair.getLeft());
    }

    /**
     * 标准化拦截器配置
     * <p>
     * 该方法处理不同格式的拦截器配置，将其统一转换为标准格式：
     * 1. 字符串格式：直接作为testClass
     * 2. 对象格式：提取拦截器名称和配置
     * 3. 标准格式：直接使用
     * </p>
     *
     * @param elementMap 元素映射表
     * @param anyData    原始数据
     */
    private void standardizeInterceptors(Map<String, Object> elementMap, Object anyData) {
        if (anyData instanceof String) {
            elementMap.put(TEST_CLASS, anyData);
            return;
        }
        if (Objects.requireNonNull(anyData) instanceof Map map) {
            if (map.containsKey(TEST_CLASS)) {
                elementMap.putAll(map);
                return;
            }
            var entries = (Set<Map.Entry>) map.entrySet();
            entries.forEach(entry -> {
                elementMap.put(TEST_CLASS, entry.getKey());
                elementMap.putAll((Map) entry.getValue());
            });
        }
    }

    /**
     * 检查并确定拦截器的类型
     * <p>
     * 根据testClass字段在应用配置中查找对应的拦截器类。
     * </p>
     *
     * @param elementMap 拦截器Map
     * @return 包含拦截器类和键的Pair对象
     * @throws JSONException 当没有匹配的拦截器时抛出
     */
    private Pair<Class<? extends RyzeInterceptor>, String> checkInterceptor(Map<String, Object> elementMap) {
        var keyMap = ApplicationConfig.getTestInterceptorKeyMap();
        var key = elementMap.get(TEST_CLASS).toString().toLowerCase();
        var clazz = keyMap.get(key);
        if (Objects.nonNull(clazz)) {
            return Pair.of(clazz, key);
        }
        throw new JSONException("没有匹配的Interceptor, JSON String: " + JSON.toJSONString(elementMap));
    }

}