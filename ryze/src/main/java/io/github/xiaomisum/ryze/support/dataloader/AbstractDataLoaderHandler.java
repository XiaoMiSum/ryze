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

/**
 * 数据加载处理器抽象基类
 * <p>
 * 该类实现了责任链模式中的基础功能，作为所有数据加载处理器的父类。
 * 提供了设置下一个处理器的通用实现，子类只需实现具体的数据加载逻辑。
 * </p>
 * <p>
 * 责任链模式说明：
 * 当前处理器无法处理数据时，会将请求传递给链中的下一个处理器，
 * 直到找到能够处理的处理器或遍历完整个链。
 * </p>
 *
 * @author xiaomi
 */
abstract class AbstractDataLoaderHandler implements DataLoaderHandler {

    /**
     * 链中的下一个处理器
     * <p>
     * 用于构建处理链，当前处理器无法处理时会传递给下一个处理器
     * </p>
     */
    protected DataLoaderHandler next;

    /**
     * 设置链中的下一个处理器
     * <p>
     * 该方法用于构建责任链，将多个处理器串联起来形成处理链。
     * 当前处理器无法处理某种数据源时，会调用下一个处理器进行处理。
     * </p>
     *
     * @param next 下一个数据加载处理器，可以为 null 表示链的末尾
     */
    @Override
    public void setNextHandler(DataLoaderHandler next) {
        this.next = next;
    }

}