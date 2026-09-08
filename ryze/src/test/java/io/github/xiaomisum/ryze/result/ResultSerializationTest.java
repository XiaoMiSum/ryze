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

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import io.github.xiaomisum.ryze.TestStatus;
import io.github.xiaomisum.ryze.config.RyzeVariables;
import io.github.xiaomisum.ryze.protocol.http.RealHTTPResponse;
import io.github.xiaomisum.ryze.protocol.redis.RealRedisRequest;
import io.github.xiaomisum.ryze.testelement.sampler.DefaultSampleResult;
import io.github.xiaomisum.ryze.testelement.sampler.SampleResult;
import org.apache.hc.core5.http.message.BasicHeader;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class ResultSerializationTest {

    @Test
    public void testVariableRecordCollect() {
        var own = new RyzeVariables(Map.of("a", "${b}", "b", "base", "c", 1));
        var postView = new java.util.LinkedHashMap<String, Object>();
        postView.put("a", "parent-b");
        postView.put("b", "base");
        postView.put("c", 1);
        postView.put("d", "new");
        // 提取器产出的新变量（不是自身定义）
        List<VariableRecord> records = VariableRecord.collect(own, postView, Arrays.asList("token", "d", null));

        Assert.assertEquals(records.size(), 4);
        VariableRecord a = recordByName(records, "a");
        Assert.assertEquals(a.getRaw(), "${b}");
        Assert.assertEquals(a.getValue(), "parent-b");
        VariableRecord d = recordByName(records, "d");
        Assert.assertNull(d.getRaw());
        Assert.assertEquals(d.getValue(), "new");
    }

    private static VariableRecord recordByName(List<VariableRecord> records, String name) {
        return records.stream().filter(record -> name.equals(record.getName())).findFirst().orElseThrow();
    }

    private static String bodyTextOf(com.alibaba.fastjson2.JSONArray array) {
        byte[] bytes = new byte[array.size()];
        for (int i = 0; i < array.size(); i++) {
            bytes[i] = ((Number) array.get(i)).byteValue();
        }
        return new String(bytes);
    }

    @Test
    public void testSampleResultSerialization() {
        var result = new DefaultSampleResult("sample-result-title");
        result.setMetadata(Map.of("env", "test", "node", "job-1"));

        result.setRequest(new RealRedisRequest("redis://localhost:6379/0", "SET", List.of("k", "v")));
        result.setResponse(new RealHTTPResponse(
                "{\"token\":\"abc\"}".getBytes(), 200, "HTTP/1.1", "OK",
                new BasicHeader("Content-Type", "application/json")));

        var assertionRecord = new AssertionResult();
        assertionRecord.setField("status");
        assertionRecord.setRule("==");
        assertionRecord.setExpected(200);
        assertionRecord.setActual(200);
        assertionRecord.setStatus(TestStatus.passed);
        result.addAssertion(assertionRecord);

        var extractorRecord = new ExtractorResult();
        extractorRecord.setRefName("token");
        extractorRecord.setField("$.token");
        extractorRecord.setValue("abc");
        result.addExtractor(extractorRecord);

        result.setVariables(List.of(new VariableRecord("token", null, "abc")));

        JSONObject json = JSON.parseObject(JSON.toJSONString(result));
        Assert.assertEquals(json.getString("title"), "sample-result-title");
        JSONObject metadata = json.getJSONObject("metadata");
        Assert.assertEquals(metadata.getString("env"), "test");
        Assert.assertEquals(metadata.getString("node"), "job-1");

        // 请求/响应按 JavaBean getter 输出结构化快照
        JSONObject request = json.getJSONObject("request");
        Assert.assertNotNull(request);
        Assert.assertEquals(request.getString("command"), "SET");
        Assert.assertEquals(request.getString("url"), "redis://localhost:6379/0");
        Assert.assertEquals(request.getJSONArray("args").getString(1), "v");
        Assert.assertNotNull(request.getString("format"));

        JSONObject response = json.getJSONObject("response");
        Assert.assertNotNull(response);
        Assert.assertEquals(response.getIntValue("status"), 200);
        Assert.assertEquals(response.getString("message"), "OK");
        Assert.assertEquals(response.getString("version"), "HTTP/1.1");
        Assert.assertEquals(bodyTextOf(response.getJSONArray("body")), "{\"token\":\"abc\"}");

        // 断言/提取器/变量记录
        Assert.assertEquals(json.getJSONArray("assertions").getJSONObject(0).getString("status"), "passed");
        Assert.assertEquals(json.getJSONArray("extractors").getJSONObject(0).getString("refName"), "token");
        Assert.assertEquals(json.getJSONArray("variables").getJSONObject(0).getString("value"), "abc");
    }

    @Test
    public void testRealRequestBytes() {
        var request = new RealRedisRequest("redis://localhost:6379/0", "SET", List.of("k", "v"));
        Assert.assertEquals(new String(request.bytes()), "SET k v");
    }

    @Test
    public void testResultTreeSerialization() {
        var host = new io.github.xiaomisum.ryze.testelement.TestSuiteResult("host");

        var pre = new DefaultSampleResult("preprocessor-1");
        pre.setVariables(List.of(new VariableRecord("created", null, "user-1")));
        host.addPreprocessor(pre);

        var post = new DefaultSampleResult("postprocessor-1");
        post.setVariables(List.of(new VariableRecord("cleaned", null, "yes")));
        host.addPostprocessor(post);

        var child = new DefaultSampleResult("child");
        child.setResponse(DefaultRealResponseBuilder.ok());
        host.addChild(child);

        JSONObject json = JSON.parseObject(JSON.toJSONString(host));
        Assert.assertEquals(json.getJSONArray("preprocessors").getJSONObject(0).getString("title"), "preprocessor-1");
        Assert.assertEquals(
                json.getJSONArray("preprocessors").getJSONObject(0).getJSONArray("variables")
                        .getJSONObject(0).getString("name"), "created");
        Assert.assertEquals(json.getJSONArray("postprocessors").getJSONObject(0).getString("title"), "postprocessor-1");
        Assert.assertEquals(json.getJSONArray("children").getJSONObject(0).getString("title"), "child");
    }

    @Test
    public void testDefaultRealRequestResponseSnapshot() {
        var result = new DefaultSampleResult("default");
        result.setRequest(SampleResult.DefaultRealRequest.build("hello".getBytes()));
        result.setResponse(SampleResult.DefaultRealResponse.build("world".getBytes()));

        JSONObject json = JSON.parseObject(JSON.toJSONString(result));
        Assert.assertEquals(json.getJSONObject("request").getString("format"), "hello");
        JSONObject response = json.getJSONObject("response");
        Assert.assertEquals(response.getIntValue("status"), 200);
        Assert.assertEquals(response.getString("format"), "world");
    }

    private static final class DefaultRealResponseBuilder {
        private static SampleResult.RealResponse ok() {
            return SampleResult.DefaultRealResponse.build("ok".getBytes());
        }
    }
}