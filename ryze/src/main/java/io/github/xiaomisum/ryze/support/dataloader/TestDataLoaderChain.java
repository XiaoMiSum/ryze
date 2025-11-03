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

import io.github.xiaomisum.ryze.support.RyzeServiceLoader;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.List;

/**
 * 测试数据加载链
 * <p>
 * 该类实现了责任链模式，用于从不同数据源加载测试数据。
 * 处理链顺序为：本地文件加载器 → 类路径资源加载器 → 其他通过SPI加载的数据加载器。
 * </p>
 * <p>
 * 支持的数据源类型：
 * <ul>
 *   <li>本地文件系统文件（以file:开头或直接路径）</li>
 *   <li>类路径资源（以classpath:开头或直接路径）</li>
 *   <li>通过SPI扩展的其他数据源</li>
 * </ul>
 * </p>
 *
 * @author xiaomi
 */
@SuppressWarnings({"unchecked"})
public class TestDataLoaderChain {

    private static final TestDataLoaderChain chain = new TestDataLoaderChain();
    private final DataLoaderHandler firstHandler;

    /**
     * 构造测试数据加载链
     * <p>
     * 初始化处理链，按照优先级顺序构建处理链：
     * 1. 本地文件加载器 - 处理本地文件系统中的文件
     * 2. 类路径资源加载器 - 处理类路径中的资源文件
     * 3. 通过SPI加载的其他数据加载器 - 处理其他自定义数据源
     * </p>
     */
    public TestDataLoaderChain() {
        // 构建处理链：Classpath资源 → 本地文件 → Other DataLoader
        var firstHandler = new ClasspathLoader();
        var secondHandler = new LocalFileLoader();
        // 设置链的顺序
        firstHandler.setNextHandler(secondHandler);
        List<DataLoaderHandler> loaders = RyzeServiceLoader.loadAsInstanceListBySPI(DataLoaderHandler.class);
        if (!loaders.isEmpty()) {
            for (int i = 0; i < loaders.size() - 1; i++) {
                loaders.get(i).setNextHandler(loaders.get(i + 1));
            }
            secondHandler.setNextHandler(loaders.getFirst());
        }
        this.firstHandler = firstHandler;
    }

    /**
     * 加载测试数据
     * <p>
     * 通过处理链加载指定数据源的测试数据，并将其转换为目标类型。
     * 处理链会依次尝试不同的加载器，直到找到能够处理该数据源的加载器。
     * </p>
     *
     * @param source 数据源路径，支持多种协议（file:, classpath:等）
     * @param type   目标类型，用于数据转换
     * @param <T>    泛型类型参数
     * @return 加载并转换后的数据对象
     */
    public static <T> T loadTestData(String source, Type type) {
        return chain.loadData(source, type);
    }

    /**
     * 获取Type对应的实际Class
     * <p>
     * 从Type对象中提取实际的Class对象，支持普通类型和参数化类型。
     * </p>
     *
     * @param type Type对象
     * @return 对应的Class对象
     * @throws RuntimeException 当无法找到对应的Class时抛出
     */
    Class<?> getTypeClass(Type type) {
        try {
            if (type instanceof ParameterizedType parameterizedType) {
                return Class.forName(parameterizedType.getRawType().getTypeName());
            }
            return Class.forName(type.getTypeName());
        } catch (Exception e) {
            throw new RuntimeException("Class Not Found", e);
        }
    }

    /**
     * 通过处理链加载数据
     * <p>
     * 使用处理链中的各个加载器依次尝试加载数据，直到成功或遍历完所有加载器。
     * </p>
     *
     * @param source 数据源路径
     * @param type   目标类型
     * @param <T>    泛型类型参数
     * @return 加载并转换后的数据对象
     * @throws RuntimeException 当所有加载器都无法处理该数据源时抛出
     */
    public <T> T loadData(String source, Type type) {
        try {
            var result = firstHandler.loadData(source, type);
            return (T) getTypeClass(type).cast(result);
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }
}