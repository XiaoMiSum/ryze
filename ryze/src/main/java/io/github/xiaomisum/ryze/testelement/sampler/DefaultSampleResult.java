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

package io.github.xiaomisum.ryze.testelement.sampler;

/**
 * 默认取样结果实现类，提供基本的取样结果功能实现。
 *
 * <p>该类是 {@link SampleResult}的默认实现，
 * 用于不需要特殊处理的取样器。它继承了SampleResult的所有功能，
 * 包括记录执行时间、存储请求响应数据等。</p>
 *
 * @author xiaomi
 */
public class DefaultSampleResult extends SampleResult {

    /**
     * 基于标题的构造函数
     *
     * @param title 结果标题
     */
    public DefaultSampleResult(String title) {
        super(title);
    }

    /**
     * 基于ID和标题的构造函数
     *
     * @param id    结果ID
     * @param title 结果标题
     */
    public DefaultSampleResult(String id, String title) {
        super(id, title);
    }
}