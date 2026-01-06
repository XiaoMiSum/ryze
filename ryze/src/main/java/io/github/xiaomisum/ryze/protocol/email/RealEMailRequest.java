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

package io.github.xiaomisum.ryze.protocol.email;

import io.github.xiaomisum.ryze.protocol.email.config.EMailConfigureItem;
import io.github.xiaomisum.ryze.testelement.sampler.SampleResult;

/**
 * @author xiaomi
 */
public class RealEMailRequest extends SampleResult.RealRequest {

    protected String host;

    protected String port;

    protected Boolean useSSL;

    protected String username;

    protected String password;

    protected String to;

    protected String title;

    protected String content;


    public RealEMailRequest(EMailConfigureItem config) {
        this.host = config.getHost();
        this.port = config.getPort(EMailConstantsInterface.DEFAULT_PORT);
        this.useSSL = config.getUseSSL();
        this.username = config.getUsername();
        this.password = config.getPassword();
        this.to = config.getTo();
        this.title = config.getTitle();
        this.content = config.getContent();
    }


    /**
     * 获取请求字节数组
     * <p>优先返回请求体，如果没有请求体则返回查询参数</p>
     *
     * @return 请求字节数组
     */
    @Override
    public byte[] bytes() {
        return content != null ? content.getBytes() : new byte[0];
    }

    /**
     * 格式化 请求信息
     *
     * @return 格式化后的请求信息字符串
     */
    @Override
    public String format() {
        return "To" + ": " + to +
                "\n" +
                "Title" + ": " + title + "\n\n" +
                content;
    }
}