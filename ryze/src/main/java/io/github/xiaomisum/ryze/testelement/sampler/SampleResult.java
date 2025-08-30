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

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneId;

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
    private Real request;
    
    /**
     * 响应数据
     */
    private Real response;

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
        if (sampleStartTime == null) {
            setSampleEndTime(LocalDateTime.now(ZoneId.systemDefault()));
        }
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
        var duration = Duration.between(sampleStartTime, sampleEndTime).toMillis() / 1000.00;
        return new BigDecimal(duration).setScale(2, RoundingMode.HALF_UP).doubleValue() + " s";
    }

    /**
     * 获取请求数据
     *
     * @return 请求数据
     */
    public Real getRequest() {
        return request;
    }

    /**
     * 设置请求数据
     *
     * @param request 请求数据
     */
    public void setRequest(Real request) {
        this.request = request;
    }

    /**
     * 获取响应数据
     *
     * @return 响应数据
     */
    public Real getResponse() {
        return response;
    }

    /**
     * 设置响应数据
     *
     * @param response 响应数据
     */
    public void setResponse(Real response) {
        this.response = response;
    }


    /**
     * 数据实体抽象类，用于表示请求或响应的数据内容
     * 
     * <p>该类提供了数据的字节表示和字符串表示之间的转换功能，
     * 并定义了数据格式化的抽象方法供子类实现。</p>
     */
    public static abstract class Real {

        /**
         * 数据的字节表示
         */
        private byte[] bytes;
        
        /**
         * 数据的字符串表示（缓存）
         */
        private String bytesAsString;

        /**
         * 构造函数
         *
         * @param bytes 数据的字节表示
         */
        public Real(byte[] bytes) {
            this.bytes = bytes;
        }

        /**
         * 格式化数据
         *
         * @return 格式化后的数据字符串
         */
        public abstract String format();

        /**
         * 获取数据的字节表示
         *
         * @return 数据的字节表示
         */
        public byte[] bytes() {
            return bytes;
        }

        /**
         * 设置数据的字节表示
         *
         * @param bytes 数据的字节表示
         */
        public void bytes(byte[] bytes) {
            this.bytes = bytes;
            this.bytesAsString = null;
        }

        /**
         * 获取数据的字符串表示
         * 
         * <p>如果字符串表示尚未缓存，则从字节表示转换得到。</p>
         *
         * @return 数据的字符串表示
         */
        public String bytesAsString() {
            if (bytesAsString == null) {
                bytesAsString = (bytes == null || bytes.length == 0) ? "" : new String(bytes);
            }
            return bytesAsString;
        }
    }

    /**
     * 默认数据实体实现类
     * 
     * <p>该类提供了数据格式化的默认实现，直接返回数据的字符串表示。</p>
     */
    public static class DefaultReal extends Real {

        /**
         * 私有构造函数
         *
         * @param bytes 数据的字节表示
         */
        private DefaultReal(byte[] bytes) {
            super(bytes);
        }

        /**
         * 构建默认数据实体实例
         *
         * @param bytes 数据的字节表示
         * @return 默认数据实体实例
         */
        public static DefaultReal build(byte[] bytes) {
            return new DefaultReal(bytes);
        }

        /**
         * 格式化数据，直接返回数据的字符串表示
         *
         * @return 数据的字符串表示
         */
        @Override
        public String format() {
            return bytesAsString();
        }
    }
}