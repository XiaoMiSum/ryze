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

import io.github.xiaomisum.ryze.Result;
import io.github.xiaomisum.ryze.result.AssertionResult;
import io.github.xiaomisum.ryze.result.ExtractorResult;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;

/**
 * 取样结果抽象类，用于存储和管理取样器执行的结果信息。
 *
 * <p>该类记录了取样器执行过程中的关键信息，包括执行时间、请求数据、响应数据等，
 * 为测试报告生成和结果分析提供数据支持。</p>
 *
 * <p>主要功能包括：
 * <ul>
 *   <li>记录取样开始和结束时间</li>
 *   <li>计算执行时长</li>
 *   <li>存储请求和响应数据</li>
 *   <li>提供数据格式化方法</li>
 * </ul></p>
 *
 * @author xiaomi
 */
public abstract class SampleResult extends Result {

    /**
     * 取样开始时间
     */
    private LocalDateTime sampleStartTime;

    /**
     * 取样结束时间
     */
    private LocalDateTime sampleEndTime;

    /**
     * 请求数据
     */
    private RealRequest request;

    /**
     * 响应数据
     */
    private RealResponse response;

    /**
     * 验证器执行记录
     */
    private List<AssertionResult> assertions;

    /**
     * 提取器执行记录
     */
    private List<ExtractorResult> extractors;

    /**
     * 基于标题的构造函数
     *
     * @param title 结果标题
     */
    public SampleResult(String title) {
        super(title);
    }

    /**
     * 基于ID和标题的构造函数
     *
     * @param id    结果ID
     * @param title 结果标题
     */
    public SampleResult(String id, String title) {
        super(id, title);
    }

    /**
     * 标记取样开始时间
     *
     * <p>如果取样开始时间尚未设置，则记录当前时间作为取样开始时间。</p>
     */
    public void sampleStart() {
        setSampleStartTime(LocalDateTime.now(ZoneId.systemDefault()));
    }

    /**
     * 标记取样结束时间
     *
     * <p>如果取样结束时间尚未设置，则记录当前时间作为取样结束时间。</p>
     */
    public void sampleEnd() {
        if (sampleEndTime == null) {
            setSampleEndTime(LocalDateTime.now(ZoneId.systemDefault()));
        }
    }

    /**
     * 获取取样开始时间
     *
     * @return 取样开始时间
     */
    public LocalDateTime getSampleStartTime() {
        return sampleStartTime;
    }

    /**
     * 设置取样开始时间
     *
     * @param sampleStartTime 取样开始时间
     */
    public void setSampleStartTime(LocalDateTime sampleStartTime) {
        this.sampleStartTime = sampleStartTime;
    }

    /**
     * 获取取样结束时间
     *
     * @return 取样结束时间
     */
    public LocalDateTime getSampleEndTime() {
        return sampleEndTime;
    }

    /**
     * 设置取样结束时间
     *
     * @param sampleEndTime 取样结束时间
     */
    public void setSampleEndTime(LocalDateTime sampleEndTime) {
        this.sampleEndTime = sampleEndTime;
    }

    /**
     * 获取执行时长
     *
     * <p>计算取样开始时间到结束时间的间隔，并格式化为秒为单位的字符串。</p>
     *
     * @return 执行时长字符串，格式为 "X.XX s"
     */
    public String getDuration() {
        if (sampleStartTime == null || sampleEndTime == null) {
            return "";
        }
        var duration = Duration.between(sampleStartTime, sampleEndTime).toMillis() / 1000.00;
        return new BigDecimal(duration).setScale(2, RoundingMode.HALF_UP).doubleValue() + " s";
    }

    /**
     * 获取请求数据
     * <p>请求数据按 JavaBean getter 序列化为结构化快照（暴露面即 getter 集合）。</p>
     *
     * @return 请求数据
     */
    public RealRequest getRequest() {
        return request;
    }

    /**
     * 设置请求数据
     *
     * @param request 请求数据
     */
    public void setRequest(RealRequest request) {
        this.request = request;
    }

    /**
     * 获取响应数据
     * <p>响应数据按 JavaBean getter 序列化为结构化快照（暴露面即 getter 集合）。</p>
     *
     * @return 响应数据
     */
    public RealResponse getResponse() {
        return response;
    }

    /**
     * 设置响应数据
     *
     * @param response 响应数据
     */
    public void setResponse(RealResponse response) {
        this.response = response;
    }

    public List<AssertionResult> getAssertions() {
        return assertions;
    }

    public void setAssertions(List<AssertionResult> assertions) {
        this.assertions = assertions;
    }

    public List<ExtractorResult> getExtractors() {
        return extractors;
    }

    public void setExtractors(List<ExtractorResult> extractors) {
        this.extractors = extractors;
    }

    /**
     * 追加一条验证器执行记录
     *
     * @param assertion 验证器执行记录
     */
    public void addAssertion(AssertionResult assertion) {
        if (assertions == null) {
            assertions = new java.util.ArrayList<>();
        }
        assertions.add(assertion);
    }

    /**
     * 追加一条提取器执行记录
     *
     * @param extractor 提取器执行记录
     */
    public void addExtractor(ExtractorResult extractor) {
        if (extractors == null) {
            extractors = new java.util.ArrayList<>();
        }
        extractors.add(extractor);
    }

    /**
     * 请求数据抽象类
     *
     * <p>该类定义了请求数据的抽象接口，用于格式化请求数据。</p>
     */
    public static abstract class RealRequest {

        public abstract String format();

        public abstract byte[] bytes();

        /**
         * 契约字段输出
         * <p>序列化时按 getter 出字段，{@code format} 契约字段通过该方法保留。</p>
         *
         * @return 格式化文本
         */
        public String getFormat() {
            return format();
        }
    }

    /**
     * 响应数据抽象类
     *
     * <p>该类定义了响应数据的抽象接口，用于格式化响应数据。</p>
     */
    public static abstract class RealResponse {

        protected int status;

        public int status() {
            return status;
        }

        /**
         * 契约字段输出
         * <p>序列化时按 getter 出字段，{@code status} 契约字段通过该方法保留。</p>
         *
         * @return 响应状态码
         */
        public int getStatus() {
            return status();
        }

        public abstract byte[] bytes();

        public abstract String format();

        public String bytesAsString() {
            return new String(bytes());
        }

        /**
         * 契约字段输出
         * <p>序列化时按 getter 出字段，{@code format} 契约字段通过该方法保留。</p>
         *
         * @return 格式化文本
         */
        public String getFormat() {
            return format();
        }
    }

    /**
     * 默认数据实体实现类
     *
     * <p>该类提供了数据格式化的默认实现，直接返回数据的字符串表示。</p>
     */
    public static class DefaultRealRequest extends RealRequest {

        private final byte[] bytes;

        /**
         * 私有构造函数
         *
         * @param bytes 数据的字节表示
         */
        private DefaultRealRequest(byte[] bytes) {
            this.bytes = bytes;
        }

        /**
         * 构建默认数据实体实例
         *
         * @return 默认数据实体实例
         */
        public static DefaultRealRequest build() {
            return new DefaultRealRequest(null);
        }

        /**
         * 构建默认数据实体实例
         *
         * @return 默认数据实体实例
         */
        public static DefaultRealRequest build(byte[] bytes) {
            return new DefaultRealRequest(bytes);
        }

        @Override
        public String format() {
            return bytes == null || bytes.length == 0 ? "ok" : new String(bytes);
        }

        @Override
        public byte[] bytes() {
            return bytes;
        }
    }

    /**
     * 默认数据实体实现类
     *
     * <p>该类提供了数据格式化的默认实现，直接返回数据的字符串表示。</p>
     */
    public static class DefaultRealResponse extends RealResponse {

        private final byte[] bytes;

        /**
         * 私有构造函数
         *
         * @param bytes 数据的字节表示
         */
        private DefaultRealResponse(byte[] bytes) {
            this.bytes = bytes;
            status = 200;
        }

        /**
         * 构建默认数据实体实例
         *
         * @return 默认数据实体实例
         */
        public static DefaultRealResponse build() {
            return new DefaultRealResponse("ok".getBytes());
        }

        /**
         * 构建默认数据实体实例
         *
         * @param bytes 数据的字节表示
         * @return 默认数据实体实例
         */
        public static DefaultRealResponse build(byte[] bytes) {
            return new DefaultRealResponse(bytes);
        }

        @Override
        public byte[] bytes() {
            return bytes;
        }

        /**
         * 格式化数据，直接返回数据的字符串表示
         *
         * @return 数据的字符串表示
         */
        @Override
        public String format() {
            return bytes == null || bytes.length == 0 ? "ok" : new String(bytes);
        }
    }
}