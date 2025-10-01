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

package io.github.xiaomisum.ryze.support.dataloader.parser;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONArray;
import io.github.xiaomisum.ryze.support.yaml.IncludeConstructor;
import org.yaml.snakeyaml.Yaml;

import java.io.File;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Objects;

/**
 * YAML解析器
 * <p>
 * 该类专门用于解析YAML格式的数据，支持将YAML字符串转换为指定类型的对象。
 * 使用SnakeYAML作为底层解析库，支持YAML的高级特性如引用和包含。
 * </p>
 *
 * @author xiaomi
 */
public class YamlParser {

    /**
     * 解析YAML字符串
     * <p>
     * 将YAML字符串解析为指定类型的对象。根据目标类型自动判断是否为集合类型，
     * 并采用相应的解析策略。支持YAML的!include和!import特性。
     * </p>
     *
     * @param yaml       YAML字符串
     * @param type       目标类型(Type)，用于泛型类型的数据转换
     * @param targetType 目标类型(Class)，用于确定数据类型
     * @param file       源文件对象，用于处理相对路径引用
     * @return 解析后的对象
     */
    public static Object parse(String yaml, Type type, Class<?> targetType, File file) {
        return parseYaml(yaml, type, targetType, file);
    }

    /**
     * 解析YAML字符串为核心方法
     * <p>
     * 使用SnakeYAML解析YAML字符串，支持!include和!import特性。
     * 根据目标类型判断是否为List类型，并采用相应的解析策略。
     * 对于单个YAML对象，如果目标类型是List，则会将其包装为数组进行解析。
     * </p>
     *
     * @param yaml       YAML字符串
     * @param type       目标类型(Type)，用于泛型类型的数据转换
     * @param targetType 目标类型(Class)，用于确定数据类型
     * @param file       源文件对象，用于处理相对路径引用
     * @return 解析后的对象
     */
    private static Object parseYaml(String yaml, Type type, Class<?> targetType, File file) {
        var result = new Yaml(new IncludeConstructor(Objects.isNull(file) || file.isDirectory() || !file.exists() ? null : file)).load(yaml);
        if (List.class.isAssignableFrom(targetType)) {
            return result instanceof List<?> ? JSON.parseObject(JSON.toJSONString(result), type) :
                    JSON.parseObject(JSONArray.of(result).toJSONString(), type);
        }
        return targetType.equals(Object.class) ? result : JSON.parseObject(JSON.toJSONString(result), type);
    }
}