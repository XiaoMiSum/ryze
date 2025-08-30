/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2022.  Lorem XiaoMiSum (mi_xiao@qq.com)
 *
 * Permission is hereby granted, free of charge, to any person obtaining
 * a copy of this software and associated documentation files (the
 * 'Software'), to deal in the Software without restriction, including
 * without limitation the rights to use, copy, modify, merge, publish,
 * distribute, sublicense, and/or sell copies of the Software, and to
 * permit persons to whom the Software is furnished to do so, subject to
 * the following conditions:
 *
 * The above copyright notice and this permission notice shall be
 * included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED 'AS IS', WITHOUT WARRANTY OF ANY KIND,
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
 * MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.
 * IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY
 * CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT,
 * TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE
 * SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package io.github.xiaomisum.ryze.testelement;

import io.github.xiaomisum.ryze.Result;

import java.util.ArrayList;
import java.util.List;

/**
 * 测试套件结果类
 * <p>
 * 该类表示测试套件的执行结果，除了继承自Result基类的基本属性外，
 * 还包含了子测试元素的结果列表。测试套件可以包含多个子元素（如其他测试套件或取样器），
 * 每个子元素的执行结果都会被添加到children列表中，形成一个树状的结果结构。
 * </p>
 */
public class TestSuiteResult extends Result {

    /**
     * 子测试元素结果列表
     * <p>存储测试套件中所有子元素的执行结果</p>
     */
    private final List<Result> children = new ArrayList<>();

    /**
     * 构造函数，仅指定标题
     *
     * @param title 测试套件标题
     */
    public TestSuiteResult(String title) {
        super(title);
    }

    /**
     * 构造函数，指定ID和标题
     *
     * @param id    测试套件ID
     * @param title 测试套件标题
     */
    public TestSuiteResult(String id, String title) {
        super(id, title);
    }

    /**
     * 获取子测试元素结果列表
     *
     * @return 子测试元素结果列表
     */
    public List<? extends Result> getChildren() {
        return children;
    }

    /**
     * 添加子测试元素结果
     * <p>将子测试元素的执行结果添加到结果列表中</p>
     *
     * @param child 子测试元素结果
     */
    public void addChild(Result child) {
        this.children.add(child);
    }
}