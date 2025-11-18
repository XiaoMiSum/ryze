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

import java.lang.reflect.Type;

/**
 * 数据加载处理器接口
 * <p>
 * 定义了数据加载处理器的标准接口，用于从不同数据源加载和解析数据。
 * 实现类可以处理各种类型的数据源，如本地文件、类路径资源、网络资源等。
 * </p>
 * <p>
 * 该接口采用责任链模式设计，通过{@link DataLoaderHandler#setNextHandler(DataLoaderHandler)}方法将多个处理器串联起来，
 * 形成处理链，提高系统的可扩展性和灵活性。
 * </p>
 *
 * @author xiaomi
 */
public interface DataLoaderHandler {

    /**
     * 加载数据
     * <p>
     * 从指定数据源加载数据，并将其转换为目标类型。
     * 实现类应根据数据源的类型和格式选择合适的加载和解析策略。
     * </p>
     *
     * @param source 数据来源，可以是文件路径、URL或其他形式的数据源标识
     * @param type   目标类型(Type)，用于泛型类型的数据转换
     * @return 数据加载并解析后的对象
     * @throws Exception 错误，可能包括文件不存在、格式不支持、解析错误等
     */
    Object loadData(String source, Type type) throws Exception;

    /**
     * 设置下一个处理器
     * <p>
     * 用于构建责任链，当前处理器无法处理某种数据源时，
     * 会将请求传递给链中的下一个处理器。
     * </p>
     *
     * @param next 下一个处理器，可以为null表示链的末尾
     */
    void setNextHandler(DataLoaderHandler next);
}