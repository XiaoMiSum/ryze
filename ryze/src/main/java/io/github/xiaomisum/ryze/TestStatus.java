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

package io.github.xiaomisum.ryze;

import io.qameta.allure.model.Status;

/**
 * 测试状态枚举
 * <p>
 * 定义了测试执行过程中可能出现的各种状态，用于标识测试元素（如测试套件、取样器等）的执行结果状态。
 * 每种状态都对应一个Allure报告状态，便于生成标准化的测试报告。
 * </p>
 *
 * @author xiaomi
 */
public enum TestStatus {


    /**
     * 禁用状态
     * <p>表示测试元素被禁用，不会被执行</p>
     */
    disabled(Status.SKIPPED),
    
    /**
     * 通过状态
     * <p>表示测试元素执行成功，所有验证都通过</p>
     */
    passed(Status.PASSED),
    
    /**
     * 失败状态
     * <p>表示测试元素执行过程中断言失败</p>
     */
    failed(Status.FAILED),
    
    /**
     * 损坏状态
     * <p>表示测试元素执行过程中发生系统错误或异常</p>
     */
    broken(Status.BROKEN),
    
    /**
     * 跳过状态
     * <p>表示测试元素被跳过执行</p>
     */
    skipped(Status.SKIPPED);

    private final Status allureStatus;

    TestStatus(Status allureStatus) {
        this.allureStatus = allureStatus;
    }

    /**
     * 判断当前状态是否为通过状态
     *
     * @return 如果是通过状态返回true，否则返回false
     */
    public boolean isPassed() {
        return this == passed;
    }

    /**
     * 判断当前状态是否为失败状态
     *
     * @return 如果是失败状态返回true，否则返回false
     */
    public boolean isFailed() {
        return this == failed;
    }

    /**
     * 判断当前状态是否为损坏状态
     *
     * @return 如果是损坏状态返回true，否则返回false
     */
    public boolean isBroken() {
        return this == broken;
    }

    /**
     * 判断当前状态是否为跳过状态
     *
     * @return 如果是跳过状态返回true，否则返回false
     */
    public boolean isSkipped() {
        return this == skipped;
    }

    /**
     * 判断当前状态是否为禁用状态
     *
     * @return 如果是禁用状态返回true，否则返回false
     */
    public boolean isDisabled() {
        return this == disabled;
    }


    /**
     * 获取对应的Allure报告状态
     *
     * @return Allure状态枚举值
     */
    public Status getAllureStatus() {
        return allureStatus;
    }
}