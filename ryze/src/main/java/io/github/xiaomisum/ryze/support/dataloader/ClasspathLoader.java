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

import java.io.InputStream;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

/**
 * 类路径资源加载器
 * <p>
 * 该加载器专门用于从类路径(classpath)加载资源文件，支持JSON和YAML格式的文件解析。
 * 能够处理以"classpath:"前缀开头或直接以"/"开头的资源路径。
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
public class ClasspathLoader extends AbstractDataLoaderHandler {

    /**
     * 从类路径加载数据
     * <p>
     * 该方法会尝试从类路径加载指定的资源文件，并根据文件扩展名选择合适的解析器进行解析。
     * 支持的协议前缀包括"classpath:"和直接路径。
     * </p>
     *
     * @param source 数据源路径，支持"classpath:"前缀，例如"classpath:test.json"或"/config/data.yaml"
     * @param type   目标类型(Type)，用于泛型类型的数据转换
     * @return 解析后的数据对象
     * @throws Exception 当文件不存在、不支持的格式或解析过程中发生错误时抛出
     */
    @Override
    public Object loadData(String source, Type type) throws Exception {
        var resourcePath = source;
        if (source.startsWith("classpath:")) {
            resourcePath = source.substring(10);
        }
        resourcePath = resourcePath.startsWith("/") ? resourcePath.substring(1) : resourcePath;
        try (var stream = Thread.currentThread().getContextClassLoader().getResourceAsStream(resourcePath)) {
            if (Objects.nonNull(stream)) {
                return parseFile(stream, resourcePath, type);
            }
            if (Objects.nonNull(next)) {
                return next.loadData(source, type);
            }
        }
        throw new UnsupportedOperationException("Unsupported file format: " + source);
    }

    /**
     * 解析类路径中的文件流
     * <p>
     * 根据文件扩展名选择合适的解析器对文件内容进行解析：
     * <ul>
     *   <li>.yaml/.yml文件使用{@link YamlParser}解析</li>
     *   <li>.json文件使用 {@link JsonParser}解析</li>
     * </ul>
     * </p>
     *
     * @param stream       文件输入流
     * @param resourcePath 资源路径，用于判断文件类型
     * @param type         目标类型(Type)，用于泛型类型的数据转换
     * @return 解析后的数据对象
     * @throws Exception 当文件格式不支持或解析过程中发生错误时抛出
     */
    private Object parseFile(InputStream stream, String resourcePath, Type type) throws Exception {
        var fileName = resourcePath.toLowerCase();
        var content = new String(stream.readAllBytes(), StandardCharsets.UTF_8);
        if (fileName.endsWith(".yaml") || fileName.endsWith(".yml")) {
            return YamlParser.parse(content, type, null);
        } else if (fileName.endsWith(".json")) {
            return JsonParser.parse(content, type);
        }
        throw new UnsupportedOperationException("Unsupported file format: " + resourcePath);
    }
}