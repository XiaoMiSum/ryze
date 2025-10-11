/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2025.  Lorem XiaoMiSum (mi_xiao@qq.com)
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

package io.github.xiaomisum.ryze.protocol.http.config;

import io.github.xiaomisum.ryze.SessionRunner;
import io.github.xiaomisum.ryze.context.ContextWrapper;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.HashMap;
import java.util.Map;

import static io.github.xiaomisum.ryze.protocol.http.HTTPConstantsInterface.DEF_REF_NAME_KEY;


public class HTTPConfigureItemTest {

    @Test
    public void testBuilder() {
        HTTPConfigureItem item = HTTPConfigureItem.builder()
                .ref("testRef")
                .protocol("https")
                .host("example.com")
                .port("8080")
                .path("/api/test")
                .method("POST")
                .http2()
                .build();

        Assert.assertEquals(item.getRef(), "testRef");
        Assert.assertEquals(item.getProtocol(), "https");
        Assert.assertEquals(item.getHost(), "example.com");
        Assert.assertEquals(item.getPort(), "8080");
        Assert.assertEquals(item.getPath(), "/api/test");
        Assert.assertEquals(item.getMethod(), "POST");
        Assert.assertTrue(item.isHttp2());
    }

    @Test
    public void testBuilderMethods() {
        HTTPConfigureItem item = HTTPConfigureItem.builder()
                .http()
                .get()
                .build();

        Assert.assertEquals(item.getProtocol(), "http");
        Assert.assertEquals(item.getMethod(), "GET");
    }

    @Test
    public void testBuilderMethodVariants() {
        HTTPConfigureItem item1 = HTTPConfigureItem.builder().post().build();
        HTTPConfigureItem item2 = HTTPConfigureItem.builder().put().build();
        HTTPConfigureItem item3 = HTTPConfigureItem.builder().delete().build();
        HTTPConfigureItem item4 = HTTPConfigureItem.builder().patch().build();
        HTTPConfigureItem item5 = HTTPConfigureItem.builder().options().build();
        HTTPConfigureItem item6 = HTTPConfigureItem.builder().trace().build();

        Assert.assertEquals(item1.getMethod(), "POST");
        Assert.assertEquals(item2.getMethod(), "PUT");
        Assert.assertEquals(item3.getMethod(), "DELETE");
        Assert.assertEquals(item4.getMethod(), "PATCH");
        Assert.assertEquals(item5.getMethod(), "OPTIONS");
        Assert.assertEquals(item6.getMethod(), "TRACE");
    }

    @Test
    public void testBuilderWithMaps() {
        Map<String, String> headers = new HashMap<>();
        headers.put("Content-Type", "application/json");
        headers.put("Accept", "application/json");

        Map<String, String> cookies = new HashMap<>();
        cookies.put("sessionId", "abc123");
        cookies.put("userId", "user123");

        Map<String, Object> query = new HashMap<>();
        query.put("param1", "value1");
        query.put("param2", 123);

        Map<String, Object> data = new HashMap<>();
        data.put("field1", "value1");
        data.put("field2", 456);

        HTTPConfigureItem item = HTTPConfigureItem.builder()
                .headers(headers)
                .cookie(cookies)
                .query(query)
                .data(data)
                .build();

        Assert.assertEquals(item.getHeaders(), headers);
        Assert.assertEquals(item.getCookie(), cookies);
        Assert.assertEquals(item.getQuery(), query);
        Assert.assertEquals(item.getData(), data);
    }

    @Test
    public void testBuilderWithCustomizers() {
        HTTPConfigureItem item = HTTPConfigureItem.builder()
                .headers(builder -> {
                    builder.put("Content-Type", "application/json");
                    builder.put("Accept", "application/json");
                })
                .cookie(builder -> {
                    builder.put("sessionId", "abc123");
                    builder.put("userId", "user123");
                })
                .query(builder -> {
                    builder.put("param1", "value1");
                    builder.put("param2", 123);
                })
                .data(builder -> {
                    builder.put("field1", "value1");
                    builder.put("field2", 456);
                })
                .build();

        Assert.assertEquals(item.getHeaders().size(), 2);
        Assert.assertEquals(item.getHeaders().get("Content-Type"), "application/json");
        Assert.assertEquals(item.getHeaders().get("Accept"), "application/json");

        Assert.assertEquals(item.getCookie().size(), 2);
        Assert.assertEquals(item.getCookie().get("sessionId"), "abc123");
        Assert.assertEquals(item.getCookie().get("userId"), "user123");

        Assert.assertEquals(item.getQuery().size(), 2);
        Assert.assertEquals(item.getQuery().get("param1"), "value1");
        Assert.assertEquals(item.getQuery().get("param2"), 123);

        Assert.assertEquals(item.getData().size(), 2);
        Assert.assertEquals(item.getData().get("field1"), "value1");
        Assert.assertEquals(item.getData().get("field2"), 456);
    }

    @Test
    public void testBuilderBody() {
        // Test with Map body
        Map<String, Object> bodyMap = new HashMap<>();
        bodyMap.put("field1", "value1");
        bodyMap.put("field2", 123);

        HTTPConfigureItem item1 = HTTPConfigureItem.builder()
                .body(bodyMap)
                .build();

        Assert.assertEquals(item1.getBody(), bodyMap);

        // Test with Object body
        String bodyString = "{\"field1\":\"value1\"}";
        HTTPConfigureItem item2 = HTTPConfigureItem.builder()
                .body(bodyString)
                .build();

        Assert.assertEquals(item2.getBody(), bodyString);
    }

    @Test
    public void testBuilderBodyCustomizer() {
        HTTPConfigureItem item = HTTPConfigureItem.builder()
                .body(builder -> {
                    builder.put("field1", "value1");
                    builder.put("field2", 123);
                })
                .build();

        Map<String, Object> body = (Map<String, Object>) item.getBody();
        Assert.assertEquals(body.size(), 2);
        Assert.assertEquals(body.get("field1"), "value1");
        Assert.assertEquals(body.get("field2"), 123);
    }

    @Test
    public void testBuilderBytesAndBinary() {
        byte[] bytes = "test bytes".getBytes();
        Object binary = new Object();

        HTTPConfigureItem item = HTTPConfigureItem.builder()
                .bytes(bytes)
                .binary(binary)
                .build();

        Assert.assertEquals(item.getBytes(), bytes);
        Assert.assertEquals(item.getBinary(), binary);
    }

    @Test
    public void testMerge() {
        HTTPConfigureItem baseItem = HTTPConfigureItem.builder()
                .ref("baseRef")
                .protocol("http")
                .host("base.example.com")
                .port("80")
                .path("/base")
                .method("GET")
                .build();

        HTTPConfigureItem otherItem = HTTPConfigureItem.builder()
                .ref("otherRef")
                .protocol("https")
                .host("other.example.com")
                .build();

        HTTPConfigureItem mergedItem = baseItem.merge(otherItem);

        // Check that properties from baseItem take precedence
        Assert.assertEquals(mergedItem.getRef(), "baseRef");
        Assert.assertEquals(mergedItem.getProtocol(), "http");
        Assert.assertEquals(mergedItem.getHost(), "base.example.com");
        Assert.assertEquals(mergedItem.getPort(), "80");
        Assert.assertEquals(mergedItem.getPath(), "/base");
        Assert.assertEquals(mergedItem.getMethod(), "GET");
    }

    @Test
    public void testMergeWithNull() {
        HTTPConfigureItem baseItem = HTTPConfigureItem.builder()
                .ref("baseRef")
                .protocol("http")
                .host("base.example.com")
                .build();

        HTTPConfigureItem mergedItem = baseItem.merge(null);

        // Should be a copy of the base item
        Assert.assertEquals(mergedItem.getRef(), "baseRef");
        Assert.assertEquals(mergedItem.getProtocol(), "http");
        Assert.assertEquals(mergedItem.getHost(), "base.example.com");
    }

    @Test
    public void testMergeMapProperties() {
        Map<String, String> baseHeaders = new HashMap<>();
        baseHeaders.put("Content-Type", "application/json");

        Map<String, String> otherHeaders = new HashMap<>();
        otherHeaders.put("Accept", "application/json");

        HTTPConfigureItem baseItem = HTTPConfigureItem.builder()
                .headers(baseHeaders)
                .build();

        HTTPConfigureItem otherItem = HTTPConfigureItem.builder()
                .headers(otherHeaders)
                .build();

        HTTPConfigureItem mergedItem = baseItem.merge(otherItem);

        // Should merge headers, with base taking precedence
        Assert.assertEquals(mergedItem.getHeaders().size(), 2);
        Assert.assertEquals(mergedItem.getHeaders().get("Content-Type"), "application/json");
        Assert.assertEquals(mergedItem.getHeaders().get("Accept"), "application/json");
    }

    @Test
    public void testEvaluate() {
        ContextWrapper context = new ContextWrapper(SessionRunner.getSessionIfNoneCreateNew());
        context.getLocalVariablesWrapper().put("ref", "testRef");
        context.getLocalVariablesWrapper().put("protocol", "testProtocol");
        context.getLocalVariablesWrapper().put("host", "testHost");
        context.getLocalVariablesWrapper().put("port", "testPort");
        context.getLocalVariablesWrapper().put("path", "testPath");
        context.getLocalVariablesWrapper().put("method", "testMethod");

        HTTPConfigureItem item = HTTPConfigureItem.builder()
                .ref("${ref}")
                .protocol("${protocol}")
                .host("${host}")
                .port("${port}")
                .path("${path}")
                .method("${method}")
                .build();

        HTTPConfigureItem evaluatedItem = item.evaluate(context);

        Assert.assertEquals(evaluatedItem.getRef(), "testRef");
        Assert.assertEquals(evaluatedItem.getProtocol(), "testProtocol");
        Assert.assertEquals(evaluatedItem.getHost(), "testHost");
        Assert.assertEquals(evaluatedItem.getPort(), "testPort");
        Assert.assertEquals(evaluatedItem.getFullPort(), ":testPort");
        Assert.assertEquals(evaluatedItem.getPath(), "/testPath");
        Assert.assertEquals(evaluatedItem.getMethod(), "TESTMETHOD");
    }

    @Test
    public void testGettersWithDefaults() {
        HTTPConfigureItem item = new HTTPConfigureItem();

        Assert.assertEquals(item.getProtocol(), "http");
        Assert.assertEquals(item.getPath(), "/");
        Assert.assertEquals(item.getMethod(), "GET");
        Assert.assertEquals(item.getRef(), DEF_REF_NAME_KEY);
    }

    @Test
    public void testGetFullPort() {
        HTTPConfigureItem item1 = HTTPConfigureItem.builder().port("8080").build();
        HTTPConfigureItem item2 = new HTTPConfigureItem();

        Assert.assertEquals(item1.getFullPort(), ":8080");
        Assert.assertEquals(item2.getFullPort(), "");
    }

    @Test
    public void testGetPath() {
        HTTPConfigureItem item1 = HTTPConfigureItem.builder().path("api/test").build();
        HTTPConfigureItem item2 = HTTPConfigureItem.builder().path("/api/test").build();

        Assert.assertEquals(item1.getPath(), "/api/test");
        Assert.assertEquals(item2.getPath(), "/api/test");
    }

    @Test
    public void testGetMethod() {
        HTTPConfigureItem item = HTTPConfigureItem.builder().method("post").build();
        Assert.assertEquals(item.getMethod(), "POST");
    }
}