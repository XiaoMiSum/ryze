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

package io.github.xiaomisum.ryze.protocol.proto.util;

import io.github.xiaomisum.ryze.support.dataloader.AbstractDataLoaderHandler;

import java.io.File;
import java.io.FileInputStream;
import java.lang.reflect.Type;

public class LocalPathLoader extends AbstractDataLoaderHandler {

    /**
     * 从本地文件系统加载数据
     * <p>
     * 该方法会尝试从本地文件系统加载指定的文件，并根据文件扩展名选择合适的解析器进行解析。
     * 支持的协议前缀包括"file:"和直接路径。
     * </p>
     *
     * @param source 数据源路径，支持"file:"前缀，例如"file:/path/to/test.json"或"/home/user/data.yaml"
     * @param type   目标类型(Type)，用于泛型类型的数据转换
     * @return 解析后的数据对象
     * @throws Exception 当文件不存在、不支持的格式或解析过程中发生错误时抛出
     */
    @Override
    public Object loadData(String source, Type type) throws Exception {
        var filePath = source;
        if (source.startsWith("file:")) {
            filePath = source.substring(5);
        }
        var file = new File(filePath);
        if (file.exists() && file.isFile()) {
            try (var stream = new FileInputStream(file)) {
                return stream.readAllBytes();
            }
        }
        if (next != null) {
            return next.loadData(source, type);
        }
        throw new IllegalArgumentException("Unsupported data source: " + source);
    }
}