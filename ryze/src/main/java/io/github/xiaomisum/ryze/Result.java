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

import com.alibaba.fastjson2.annotation.JSONField;
import io.github.xiaomisum.ryze.result.VariableRecord;
import io.github.xiaomisum.ryze.testelement.TestElementConstantsInterface;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 测试结果抽象基类
 * <p>
 * 该类定义了测试执行结果的基本结构和属性，包括测试元素的标识、标题、执行状态、开始时间、
 * 结束时间以及可能抛出的异常信息。所有具体的测试结果类都应该继承此类，以保证测试结果数据结构的一致性。
 * </p>
 *
 * @author mi.xiao
 * @since 2021/6/15 20:44
 */
public abstract class Result implements Serializable, TestElementConstantsInterface {

    /**
     * 测试元素ID
     * <p>唯一标识一个测试元素实例</p>
     */
    private final String id;

    /**
     * 测试元素标题
     * <p>描述测试元素的名称或标题</p>
     */
    private final String title;

    /**
     * 测试执行状态
     * <p>标识测试元素的执行结果状态，默认为通过状态</p>
     */
    private TestStatus status = TestStatus.passed;

    /**
     * 测试开始时间
     * <p>记录测试元素开始执行的时间戳</p>
     */
    private LocalDateTime startTime;

    /**
     * 测试结束时间
     * <p>记录测试元素执行完成的时间戳</p>
     */
    private LocalDateTime endTime;

    /**
     * 测试过程中抛出的异常
     * <p>如果测试执行过程中发生异常，将异常对象保存在此字段中</p>
     */
    private Throwable throwable;

/**
     * 拦截者标识（通常为拦截器 SimpleName）。
     * <p>null 表示本次执行未被拦截；非 null 时说明业务被跳过，由哪一个拦截器阻止。
     * 该字段让报告层（Allure / 日志）可脱离拦截器链内部结构，仅依赖结果即可呈现
     * "被拦截"事实，避免 passed 的欺骗性展示。</p>
     */
    private String rejectBy;

    /**
     * 测试元件元数据
     * <p>原样写入本元件的 {@link io.github.xiaomisum.ryze.testelement.AbstractTestElement#metadata}，
     * 不做表达式求值、不参与变量替换，仅浅拷贝容器留档。</p>
     */
    @JSONField(name = METADATA)
    private Map<String, Object> metadata;

    /**
     * 本次执行后本元件产生/变更的变量（增量）
     * <p>只记录新增或覆盖的变量，一条 {@link VariableRecord} 同时包含定义形态与执行后值。</p>
     */
    @JSONField(name = VARIABLES)
    private List<VariableRecord> variables;

    /**
     * 前置处理器结果
     * <p>元素即处理器自己的 Result（sample 为 SampleResult），不进入 children。</p>
     */
    @JSONField(name = PREPROCESSORS)
    private List<Result> preprocessors;

    /**
     * 后置处理器结果
     */
    @JSONField(name = POSTPROCESSORS)
    private List<Result> postprocessors;


    /**
     * 构造函数，仅指定标题
     *
     * @param title 测试元素标题
     */
    public Result(String title) {
        this(null, title);
    }

    /**
     * 构造函数，指定ID和标题
     *
     * @param id    测试元素ID
     * @param title 测试元素标题
     */
    public Result(String id, String title) {
        this.id = id;
        this.title = title;
    }

    /**
     * 标记测试开始
     * <p>设置测试开始时间，如果已设置则不重复设置</p>
     */
    public void testStart() {
        if (startTime == null) {
            setStartTime(LocalDateTime.now(ZoneId.systemDefault()));
        }
    }

    /**
     * 标记测试结束
     * <p>设置测试结束时间，如果已设置则不重复设置</p>
     */
    public void testEnd() {
        if (endTime == null) {
            setEndTime(LocalDateTime.now(ZoneId.systemDefault()));
        }
    }

    /**
     * 获取测试元素ID
     *
     * @return 测试元素ID
     */
    public String getId() {
        return id;
    }

    /**
     * 获取测试元素标题
     *
     * @return 测试元素标题
     */
    public String getTitle() {
        return title;
    }

    /**
     * 获取测试执行状态
     *
     * @return 测试执行状态
     */
    public TestStatus getStatus() {
        return status;
    }

    /**
     * 设置测试执行状态
     *
     * @param status 测试执行状态
     */
    public void setStatus(TestStatus status) {
        this.status = status;
    }

    /**
     * 获取测试开始时间
     *
     * @return 测试开始时间
     */
    public LocalDateTime getStartTime() {
        return startTime;
    }

    /**
     * 设置测试开始时间
     *
     * @param startTime 测试开始时间
     */
    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    /**
     * 获取测试结束时间
     *
     * @return 测试结束时间
     */
    public LocalDateTime getEndTime() {
        return endTime;
    }

    /**
     * 设置测试结束时间
     *
     * @param endTime 测试结束时间
     */
    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }

    /**
     * 获取测试过程中抛出的异常
     *
     * @return 测试过程中抛出的异常
     */
    public Throwable getThrowable() {
        return throwable;
    }

    /**
     * 设置测试过程中抛出的异常
     *
     * @param throwable 测试过程中抛出的异常
     */
    public void setThrowable(Throwable throwable) {
        this.throwable = throwable;
    }

    /**
     * 获取拦截者标识
     *
     * @return 拦截者标识（通常为拦截器 SimpleName），未被拦截时返回 null
     */
    public String getRejectBy() {
        return rejectBy;
    }

    /**
     * 设置拦截者标识
     *
     * @param rejectBy 拦截者标识（通常为拦截器 SimpleName）
     */
    public void setRejectBy(String rejectBy) {
        this.rejectBy = rejectBy;
    }

    public Map<String, Object> getMetadata() {
        return metadata;
    }

    public void setMetadata(Map<String, Object> metadata) {
        this.metadata = metadata;
    }

    public List<VariableRecord> getVariables() {
        return variables;
    }

    public void setVariables(List<VariableRecord> variables) {
        this.variables = variables;
    }

    public List<Result> getPreprocessors() {
        return preprocessors;
    }

    public void setPreprocessors(List<Result> preprocessors) {
        this.preprocessors = preprocessors;
    }

    public List<Result> getPostprocessors() {
        return postprocessors;
    }

    public void setPostprocessors(List<Result> postprocessors) {
        this.postprocessors = postprocessors;
    }

    public void addPreprocessor(Result preprocessor) {
        if (preprocessor != null) {
            if (preprocessors == null) {
                preprocessors = new ArrayList<>();
            }
            preprocessors.add(preprocessor);
        }
    }

    public void addPostprocessor(Result postprocessor) {
        if (postprocessor != null) {
            if (postprocessors == null) {
                postprocessors = new ArrayList<>();
            }
            postprocessors.add(postprocessor);
        }
    }
}