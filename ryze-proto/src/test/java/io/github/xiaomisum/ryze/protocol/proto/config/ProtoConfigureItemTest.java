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

package io.github.xiaomisum.ryze.protocol.proto.config;

import io.github.xiaomisum.ryze.SessionRunner;
import io.github.xiaomisum.ryze.context.ContextWrapper;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.HashMap;
import java.util.Map;

import static io.github.xiaomisum.ryze.protocol.proto.ProtoConstantsInterface.DEF_REF_NAME_KEY;


public class ProtoConfigureItemTest {

    @Test
    public void testBuilder() {
        ProtoConfigureItem item = ProtoConfigureItem.builder()
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
        ProtoConfigureItem item = ProtoConfigureItem.builder()
                .http()
                .get()
                .build();

        Assert.assertEquals(item.getProtocol(), "http");
        Assert.assertEquals(item.getMethod(), "GET");
    }

    @Test
    public void testBuilderMethodVariants() {
        ProtoConfigureItem item1 = ProtoConfigureItem.builder().post().build();
        ProtoConfigureItem item2 = ProtoConfigureItem.builder().put().build();
        ProtoConfigureItem item3 = ProtoConfigureItem.builder().delete().build();
        ProtoConfigureItem item4 = ProtoConfigureItem.builder().patch().build();
        ProtoConfigureItem item5 = ProtoConfigureItem.builder().options().build();
        ProtoConfigureItem item6 = ProtoConfigureItem.builder().trace().build();

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

        Map<String, Object> query = new HashMap<>();
        query.put("param1", "value1");
        query.put("param2", 123);

        Map<String, Object> body = new HashMap<>();
        body.put("field1", "value1");
        body.put("field2", 456);

        ProtoConfigureItem item = ProtoConfigureItem.builder()
                .headers(headers)
                .query(query)
                .body(body)
                .build();

        Assert.assertEquals(item.getHeaders(), headers);
        Assert.assertEquals(item.getQuery(), query);
        Assert.assertEquals(item.getBody(), body);
    }

    @Test
    public void testBuilderWithCustomizers() {
        ProtoConfigureItem item = ProtoConfigureItem.builder()
                .headers(builder -> {
                    builder.put("Content-Type", "application/json");
                    builder.put("Accept", "application/json");
                })
                .query(builder -> {
                    builder.put("param1", "value1");
                    builder.put("param2", 123);
                })
                .body(builder -> {
                    builder.put("field1", "value1");
                    builder.put("field2", 456);
                })
                .build();

        Assert.assertEquals(item.getHeaders().size(), 2);
        Assert.assertEquals(item.getHeaders().get("Content-Type"), "application/json");
        Assert.assertEquals(item.getHeaders().get("Accept"), "application/json");

        Assert.assertEquals(item.getQuery().size(), 2);
        Assert.assertEquals(item.getQuery().get("param1"), "value1");
        Assert.assertEquals(item.getQuery().get("param2"), 123);

        Map<String, Object> body = (Map<String, Object>) item.getBody();

        Assert.assertEquals(body.size(), 2);
        Assert.assertEquals(body.get("field1"), "value1");
        Assert.assertEquals(body.get("field2"), 456);
    }

    @Test
    public void testBuilderBody() {
        // Test with Map body
        Map<String, Object> bodyMap = new HashMap<>();
        bodyMap.put("field1", "value1");
        bodyMap.put("field2", 123);

        ProtoConfigureItem item1 = ProtoConfigureItem.builder()
                .body(bodyMap)
                .build();

        Assert.assertEquals(item1.getBody(), bodyMap);

        // Test with Object body
        String bodyString = "{\"field1\":\"value1\"}";
        ProtoConfigureItem item2 = ProtoConfigureItem.builder()
                .body(bodyString)
                .build();

        Assert.assertEquals(item2.getBody(), bodyString);
    }

    @Test
    public void testBuilderBodyCustomizer() {
        ProtoConfigureItem item = ProtoConfigureItem.builder()
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
    public void testBuilderProto() {
        ProtoConfigureItem item = ProtoConfigureItem.builder()
                .protoDesc(builder -> {
                    builder.descPath("/path/to/file.desc")
                            .requestMessageName("RequestMessage")
                            .responseMessageName("ResponseMessage");
                })
                .build();

        Assert.assertNotNull(item.getProtoDesc());
        Assert.assertEquals(item.getProtoDesc().getDescPath(), "/path/to/file.desc");
        Assert.assertEquals(item.getProtoDesc().getRequestMessageName(), "RequestMessage");
        Assert.assertEquals(item.getProtoDesc().getResponseMessageName(), "ResponseMessage");
    }

    @Test
    public void testMerge() {
        ProtoConfigureItem.Proto baseProto = ProtoConfigureItem.Proto.builder()
                .descPath("/path/to/base.desc")
                .requestMessageName("BaseRequest")
                .build();

        ProtoConfigureItem baseItem = ProtoConfigureItem.builder()
                .ref("baseRef")
                .protocol("http")
                .host("base.example.com")
                .port("80")
                .path("/base")
                .method("GET")
                .protoDesc(baseProto)
                .build();

        ProtoConfigureItem.Proto otherProto = ProtoConfigureItem.Proto.builder()
                .descPath("/path/to/other.desc")
                .responseMessageName("OtherResponse")
                .build();

        ProtoConfigureItem otherItem = ProtoConfigureItem.builder()
                .ref("otherRef")
                .protocol("https")
                .host("other.example.com")
                .protoDesc(otherProto)
                .build();

        ProtoConfigureItem mergedItem = baseItem.merge(otherItem);

        // Check that properties from baseItem take precedence
        Assert.assertEquals(mergedItem.getRef(), "baseRef");
        Assert.assertEquals(mergedItem.getProtocol(), "http");
        Assert.assertEquals(mergedItem.getHost(), "base.example.com");
        Assert.assertEquals(mergedItem.getPort(), "80");
        Assert.assertEquals(mergedItem.getPath(), "/base");
        Assert.assertEquals(mergedItem.getMethod(), "GET");

        // Check that proto is merged correctly
        Assert.assertNotNull(mergedItem.getProtoDesc());
        Assert.assertEquals(mergedItem.getProtoDesc().getDescPath(), "/path/to/base.desc");
        Assert.assertEquals(mergedItem.getProtoDesc().getRequestMessageName(), "BaseRequest");
        Assert.assertEquals(mergedItem.getProtoDesc().getResponseMessageName(), "OtherResponse");
    }

    @Test
    public void testMergeWithNull() {
        ProtoConfigureItem baseItem = ProtoConfigureItem.builder()
                .ref("baseRef")
                .protocol("http")
                .host("base.example.com")
                .build();

        ProtoConfigureItem mergedItem = baseItem.merge(null);

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

        ProtoConfigureItem baseItem = ProtoConfigureItem.builder()
                .headers(baseHeaders)
                .build();

        ProtoConfigureItem otherItem = ProtoConfigureItem.builder()
                .headers(otherHeaders)
                .build();

        ProtoConfigureItem mergedItem = baseItem.merge(otherItem);

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
        context.getLocalVariablesWrapper().put("descPath", "/path/to/test.desc");
        context.getLocalVariablesWrapper().put("requestMessage", "TestRequest");
        context.getLocalVariablesWrapper().put("responseMessage", "TestResponse");

        ProtoConfigureItem item = ProtoConfigureItem.builder()
                .ref("${ref}")
                .protocol("${protocol}")
                .host("${host}")
                .port("${port}")
                .path("${path}")
                .method("${method}")
                .protoDesc(builder -> {
                    builder.descPath("${descPath}")
                            .requestMessageName("${requestMessage}")
                            .responseMessageName("${responseMessage}");
                })
                .build();

        ProtoConfigureItem evaluatedItem = item.evaluate(context);

        Assert.assertEquals(evaluatedItem.getRef(), "testRef");
        Assert.assertEquals(evaluatedItem.getProtocol(), "testProtocol");
        Assert.assertEquals(evaluatedItem.getHost(), "testHost");
        Assert.assertEquals(evaluatedItem.getPort(), "testPort");
        Assert.assertEquals(evaluatedItem.getFullPort(), ":testPort");
        Assert.assertEquals(evaluatedItem.getPath(), "/testPath");
        Assert.assertEquals(evaluatedItem.getMethod(), "TESTMETHOD");

        Assert.assertNotNull(evaluatedItem.getProtoDesc());
        Assert.assertEquals(evaluatedItem.getProtoDesc().getDescPath(), "/path/to/test.desc");
        Assert.assertEquals(evaluatedItem.getProtoDesc().getRequestMessageName(), "TestRequest");
        Assert.assertEquals(evaluatedItem.getProtoDesc().getResponseMessageName(), "TestResponse");
    }

    @Test
    public void testGettersWithDefaults() {
        ProtoConfigureItem item = new ProtoConfigureItem();

        Assert.assertEquals(item.getProtocol(), "http");
        Assert.assertEquals(item.getPath(), "/");
        Assert.assertEquals(item.getMethod(), "GET");
        Assert.assertEquals(item.getRef(), DEF_REF_NAME_KEY);
    }

    @Test
    public void testGetFullPort() {
        ProtoConfigureItem item1 = ProtoConfigureItem.builder().port("8080").build();
        ProtoConfigureItem item2 = new ProtoConfigureItem();

        Assert.assertEquals(item1.getFullPort(), ":8080");
        Assert.assertEquals(item2.getFullPort(), "");
    }

    @Test
    public void testGetPath() {
        ProtoConfigureItem item1 = ProtoConfigureItem.builder().path("api/test").build();
        ProtoConfigureItem item2 = ProtoConfigureItem.builder().path("/api/test").build();

        Assert.assertEquals(item1.getPath(), "/api/test");
        Assert.assertEquals(item2.getPath(), "/api/test");
    }

    @Test
    public void testGetMethod() {
        ProtoConfigureItem item = ProtoConfigureItem.builder().method("post").build();
        Assert.assertEquals(item.getMethod(), "POST");
    }

    @Test
    public void testGetStringBody() {
        // Test with null body
        ProtoConfigureItem item1 = new ProtoConfigureItem();
        Assert.assertEquals(item1.getStringBody(), "{}");

        // Test with string body
        ProtoConfigureItem item2 = ProtoConfigureItem.builder()
                .body("{\"key\":\"value\"}")
                .build();
        Assert.assertEquals(item2.getStringBody(), "{\"key\":\"value\"}");

        // Test with map body
        Map<String, Object> bodyMap = new HashMap<>();
        bodyMap.put("key", "value");
        bodyMap.put("number", 123);
        ProtoConfigureItem item3 = ProtoConfigureItem.builder()
                .body(bodyMap)
                .build();
        // Note: The order of keys in JSON string may vary
        String result = item3.getStringBody();
        Assert.assertTrue(result.contains("\"key\":\"value\""));
        Assert.assertTrue(result.contains("\"number\":123"));
    }
}