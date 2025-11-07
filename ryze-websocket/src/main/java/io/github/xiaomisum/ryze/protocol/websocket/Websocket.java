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

package io.github.xiaomisum.ryze.protocol.websocket;

import com.alibaba.fastjson2.JSON;
import io.github.xiaomisum.ryze.protocol.websocket.config.WebsocketConfigureItem;
import io.github.xiaomisum.ryze.testelement.sampler.DefaultSampleResult;
import io.github.xiaomisum.simplewebsocket.Request;
import io.github.xiaomisum.simplewebsocket.Response;
import org.apache.commons.lang3.StringUtils;

import java.util.regex.Pattern;

public class Websocket {
    public static Request build(WebsocketConfigureItem config) {
        return new Request(config.getProtocol() + "://" + config.getHost() + ":" + config.getPort() + config.getPath())
                .headers(config.getHeaders())
                .query(config.getQuery())
                .body(config.getBody() == null ? null : config.getBody() instanceof String s ? s : JSON.toJSONString(config.getBody()))
                .bytes(config.getBytes());
    }

    public static Response execute(Request request, WebsocketConfigureItem config, DefaultSampleResult result) {
        result.sampleStart();
        try {
            return request.execute(StringUtils.isBlank(config.getResponsePattern()) ? null :
                            res -> Pattern.compile(config.getResponsePattern()).matcher(res).find(),
                    config.getBytesToStringConverter());
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            result.sampleEnd();
        }

    }
}
