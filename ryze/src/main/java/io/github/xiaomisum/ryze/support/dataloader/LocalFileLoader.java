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

package io.github.xiaomisum.ryze.support.dataloader;

import io.github.xiaomisum.ryze.support.dataloader.parser.JsonParser;
import io.github.xiaomisum.ryze.support.dataloader.parser.YamlParser;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;

/**
 * 本地文件加载器
 * <p>
 * 该加载器专门用于从本地文件系统加载数据文件，支持JSON和YAML格式的文件解析。
 * 能够处理以"file:"前缀开头或直接路径的文件。
 * </p>
 * <p>
 * 支持的文件格式：
 * <ul>
 *   <li>JSON文件(.json)</li>
 *   <li>YAML文件(.yaml, .yml)</li>
 * </ul>
 * </p>
 *
 * @author xiaomi
 */
public class LocalFileLoader extends AbstractDataLoaderHandler {

    /**
     * 从本地文件系统加载数据
     * <p>
     * 该方法会尝试从本地文件系统加载指定的文件，并根据文件扩展名选择合适的解析器进行解析。
     * 支持的协议前缀包括"file:"和直接路径。
     * </p>
     *
     * @param source     数据源路径，支持"file:"前缀，例如"file:/path/to/test.json"或"/home/user/data.yaml"
     * @param type       目标类型(Type)，用于泛型类型的数据转换
     * @param targetType 目标类型(Class)，用于确定数据类型
     * @return 解析后的数据对象
     * @throws Exception 当文件不存在、不支持的格式或解析过程中发生错误时抛出
     */
    @Override
    public Object loadData(String source, Type type, Class<?> targetType) throws Exception {
        var filePath = source;
        if (source.startsWith("file:")) {
            filePath = source.substring(5);
        }
        var file = new File(filePath);
        if (file.exists() && file.isFile()) {
            try (var stream = new FileInputStream(file)) {
                return parseFile(stream, file, type, targetType);
            }
        }
        if (next != null) {
            return next.loadData(source, type, targetType);
        }
        throw new IllegalArgumentException("Unsupported data source: " + source);
    }

    /**
     * 解析本地文件流
     * <p>
     * 根据文件扩展名选择合适的解析器对文件内容进行解析：
     * <ul>
     *   <li>.yaml/.yml文件使用{@link YamlParser}解析</li>
     *   <li>.json文件使用 {@link JsonParser}解析</li>
     * </ul>
     * </p>
     *
     * @param stream     文件输入流
     * @param file       文件对象，用于获取文件名等信息
     * @param type       目标类型(Type)，用于泛型类型的数据转换
     * @param targetType 目标类型(Class)，用于确定数据类型
     * @return 解析后的数据对象
     * @throws Exception 当文件格式不支持或解析过程中发生错误时抛出
     */
    private Object parseFile(InputStream stream, File file, Type type, Class<?> targetType) throws Exception {
        var fileName = file.getName().toLowerCase();
        var content = new String(stream.readAllBytes(), StandardCharsets.UTF_8);
        if (fileName.endsWith(".yaml") || fileName.endsWith(".yml")) {
            return YamlParser.parse(content, type, targetType, file);
        } else if (fileName.endsWith(".json")) {
            return JsonParser.parse(content, type, targetType);
        }
        throw new UnsupportedOperationException("Unsupported file format: " + file.getPath());
    }

}