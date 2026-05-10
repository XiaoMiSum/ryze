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
import com.alibaba.fastjson2.JSONObject;
import io.github.xiaomisum.ryze.support.dataloader.TestDataLoaderChain;

import java.io.File;
import java.lang.reflect.Type;
import java.nio.file.Paths;
import java.util.Map;

/**
 * JSON解析器
 * <p>
 * 该类专门用于解析JSON格式的数据，支持将JSON字符串转换为指定类型的对象。
 * 使用FastJSON2作为底层解析库，提供高效的JSON解析能力。
 * </p>
 * <p>
 * 支持的文件引入语法（作为属性值使用）：
 * <ul>
 *   <li>字符串值：("!include: file.json") - 引入整个文件内容</li>
 *   <li>字符串值：("!import: file.json") - 同上，引入整个文件内容</li>
 *   <li>对象值：{"!include": "file.json"} - 引入整个文件内容</li>
 *   <li>对象值：{"!import": "file.json"} - 同上，引入整个文件内容</li>
 *   <li>对象值：{"$ref": "file.json"} - JSON 标准引用语法</li>
 *   <li>数组中的引用：["!include: file1.json", {"!include": "file2.json"}, {"$ref": "file3.json"}]</li>
 * </ul>
 * </p>
 *
 * @author xiaomi
 */
public class JsonParser {

    private static final String INCLUDE_PREFIX = "!include: ";
    private static final String IMPORT_PREFIX = "!import: ";
    private static final String INCLUDE_KEY = "!include";
    private static final String IMPORT_KEY = "!import";
    private static final String REF_KEY = "$ref";

    /**
     * 解析JSON字符串
     * <p>
     * 将JSON字符串解析为指定类型的对象。根据目标类型自动判断是否为集合类型，
     * 并采用相应的解析策略。
     * </p>
     * <p>
     * 支持在JSON中使用 !include、!import 和 $ref 语法引用外部文件。
     * </p>
     *
     * @param json JSON字符串
     * @param type 目标类型(Type)，用于泛型类型的数据转换
     * @return 解析后的对象
     */
    public static Object parse(String json, Type type) {
        return parse(json, type, null);
    }

    /**
     * 解析JSON字符串（支持基础路径）
     * <p>
     * 将JSON字符串解析为指定类型的对象，并处理其中的 include/import 引用。
     * 相对路径将基于 basePath 进行解析。
     * </p>
     *
     * @param json     JSON字符串
     * @param type     目标类型(Type)，用于泛型类型的数据转换
     * @param basePath 基础路径，用于解析相对路径，可以为 null
     * @return 解析后的对象
     */
    public static Object parse(String json, Type type, File basePath) {
        Object parsed = JSON.parse(json);
        Object processed = processNode(parsed, basePath);
        return convertToType(processed, type);
    }

    /**
     * 递归处理 JSON 节点
     *
     * @param node     JSON 节点
     * @param basePath 基础路径（File 或 String）
     * @return 处理后的节点
     */
    private static Object processNode(Object node, File basePath) {
        if (node == null) {
            return null;
        }
        return switch (node) {
            case JSONObject jsonObject -> processJsonObject(jsonObject, basePath);
            case JSONArray jsonArray -> processJsonArray(jsonArray, basePath);
            case String str -> processStringInclude(str, basePath);
            default -> node;
        };
    }

    /**
     * 处理 JSON 对象
     *
     * @param jsonObject JSON 对象
     * @param basePath   基础路径
     * @return 处理后的对象
     */
    private static Object processJsonObject(JSONObject jsonObject, File basePath) {
        // 检查是否是 include/import 引用（对象形式）
        if (jsonObject.containsKey(INCLUDE_KEY)) {
            String filename = jsonObject.getString(INCLUDE_KEY);
            return loadAndProcessFile(filename, basePath);
        }

        if (jsonObject.containsKey(IMPORT_KEY)) {
            String filename = jsonObject.getString(IMPORT_KEY);
            return loadAndProcessFile(filename, basePath);
        }

        // 检查是否是 JSON 标准 $ref 引用
        if (jsonObject.containsKey(REF_KEY)) {
            String filename = jsonObject.getString(REF_KEY);
            return loadAndProcessFile(filename, basePath);
        }

        // 递归处理所有字段
        JSONObject result = new JSONObject();
        for (Map.Entry<String, Object> entry : jsonObject.entrySet()) {
            result.put(entry.getKey(), processNode(entry.getValue(), basePath));
        }
        return result;
    }

    /**
     * 处理 JSON 数组
     *
     * @param jsonArray JSON 数组
     * @param basePath  基础路径
     * @return 处理后的数组
     */
    private static JSONArray processJsonArray(JSONArray jsonArray, File basePath) {
        JSONArray result = new JSONArray();
        for (Object item : jsonArray) {
            result.add(processNode(item, basePath));
        }
        return result;
    }

    /**
     * 处理字符串形式的 include/import
     * <p>
     * 支持格式：
     * - "!include: filename.json"
     * - "!import: filename.json"
     * </p>
     *
     * @param value    字符串值
     * @param basePath 基础路径
     * @return 如果是 include/import 语法则返回文件内容，否则返回原字符串
     */
    private static Object processStringInclude(String value, File basePath) {
        String trimmed = value.trim();

        // 检查是否以 !include: 或 !import: 开头
        if (trimmed.startsWith(INCLUDE_PREFIX)) {
            String filename = trimmed.substring(INCLUDE_PREFIX.length()).trim();
            return loadAndProcessFile(filename, basePath);
        }

        if (trimmed.startsWith(IMPORT_PREFIX)) {
            String filename = trimmed.substring(IMPORT_PREFIX.length()).trim();
            return loadAndProcessFile(filename, basePath);
        }

        // 不是 include/import 语法，返回原值
        return value;
    }

    /**
     * 加载并处理外部文件
     *
     * @param filename 文件名（可以是相对路径或绝对路径）
     * @param basePath 基础路径
     * @return 文件内容（已处理其中的 include/import）
     */
    private static Object loadAndProcessFile(String filename, File basePath) {
        try {
            // 解析文件路径
            String path = resolveFilePath(filename, basePath);

            // 加载文件内容
            Object loaded = TestDataLoaderChain.loadTestData(path, Object.class);

            // 递归处理加载的内容（支持嵌套 include）
            return processNode(loaded, basePath);
        } catch (Exception e) {
            throw new RuntimeException("加载 !include/!import JSON 文件错误: " + filename, e);
        }
    }

    /**
     * 解析文件路径
     *
     * @param filename 文件名
     * @param basePath 基础路径
     * @return 绝对路径
     */
    private static String resolveFilePath(String filename, File basePath) {
        if (Paths.get(filename).isAbsolute()) {
            return filename;
        }
        if (basePath == null) {
            return filename;
        }
        return new File(basePath.isFile() ? basePath.getParentFile() : basePath, filename).getAbsolutePath();
    }

    /**
     * 将处理后的对象转换为目标类型
     *
     * @param obj  处理后的对象
     * @param type 目标类型
     * @return 转换后的对象
     */
    private static Object convertToType(Object obj, Type type) {
        if (obj == null || type == null) {
            return obj;
        }
        if (obj instanceof String str) {
            return JSON.parseObject(str, type);
        }
        return JSON.parseObject(JSON.toJSONString(obj), type);
    }
}
