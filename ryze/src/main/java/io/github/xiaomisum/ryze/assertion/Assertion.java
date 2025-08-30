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

package io.github.xiaomisum.ryze.assertion;

import com.alibaba.fastjson2.annotation.JSONType;
import io.github.xiaomisum.ryze.context.ContextWrapper;
import io.github.xiaomisum.ryze.support.Validatable;
import io.github.xiaomisum.ryze.support.fastjson.deserializer.AssertionObjectReader;

/**
 * 断言接口，定义了测试断言的基本规范
 *
 * <p>所有测试断言都需要实现该接口，提供断言逻辑的实现。
 * 断言用于验证测试结果是否符合预期，在测试执行过程中自动验证实际结果与期望结果的一致性。</p>
 *
 * <p>断言通过{@link #assertThat(ContextWrapper)}方法执行具体的验证逻辑，
 * 并根据验证结果设置测试状态。如果断言失败，将抛出AssertionError异常。</p>
 *
 * @author xiaomi
 * @see ContextWrapper 上下文包装类
 * @see Validatable 可验证接口
 */
@JSONType(deserializer = AssertionObjectReader.class)
public interface Assertion extends Validatable {

    /**
     * 执行断言验证逻辑
     *
     * <p>该方法在测试执行过程中被调用，用于验证实际结果与期望结果是否一致。
     * 如果验证失败，会设置测试结果状态为失败，并抛出AssertionError异常。</p>
     *
     * @param contextWrapper 上下文对象，包含测试执行过程中的变量和状态信息
     */
    void assertThat(ContextWrapper contextWrapper);
}