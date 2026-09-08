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

package io.github.xiaomisum.ryze.result;

import io.github.xiaomisum.ryze.TestStatus;

import java.io.Serializable;

/**
 * 单个验证器的执行记录
 * <p>
 * 记录验证器的字段、规则、期望值、实际值与执行状态，随结果树序列化，
 * 供报告层与外部系统消费。状态复用 {@link TestStatus}：{@code passed} 验证通过、
 * {@code failed} 验证失败、{@code skipped} 未执行（默认值）。
 * </p>
 *
 * @author mi.xiao
 */
public class AssertionResult implements Serializable {

    /**
     * 验证字段
     */
    private String field;

    /**
     * 验证规则（== / contains / ...）
     */
    private String rule;

    /**
     * 期望值（已求值）
     */
    private Object expected;

    /**
     * 实际值
     */
    private Object actual;

    /**
     * 执行状态（passed 通过 / failed 失败 / skipped 未执行）
     */
    private TestStatus status = TestStatus.skipped;

    /**
     * 失败/异常消息
     */
    private String message;

    public String getField() {
        return field;
    }

    public void setField(String field) {
        this.field = field;
    }

    public String getRule() {
        return rule;
    }

    public void setRule(String rule) {
        this.rule = rule;
    }

    public Object getExpected() {
        return expected;
    }

    public void setExpected(Object expected) {
        this.expected = expected;
    }

    public Object getActual() {
        return actual;
    }

    public void setActual(Object actual) {
        this.actual = actual;
    }

    /**
     * 是否通过
     *
     * @return 通过返回 true
     */
    public boolean isPassed() {
        return status == TestStatus.passed;
    }

    /**
     * 获取执行状态
     *
     * @return 执行状态
     */
    public TestStatus getStatus() {
        return status;
    }

    /**
     * 设置执行状态
     *
     * @param status 执行状态
     */
    public void setStatus(TestStatus status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}