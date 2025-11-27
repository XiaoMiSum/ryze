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

package io.github.xiaomisum.ryze;

import io.github.xiaomisum.ryze.assertion.Assertion;
import io.github.xiaomisum.ryze.assertion.builtin.JSONAssertion;
import io.github.xiaomisum.ryze.assertion.builtin.ResultAssertion;
import io.github.xiaomisum.ryze.assertion.builtin.StatusAssertion;
import io.github.xiaomisum.ryze.assertion.builtin.matcher.*;
import io.github.xiaomisum.ryze.extractor.Extractor;
import io.github.xiaomisum.ryze.extractor.builtin.JSONExtractor;
import io.github.xiaomisum.ryze.extractor.builtin.RegexExtractor;
import io.github.xiaomisum.ryze.extractor.builtin.ResultExtractor;
import io.github.xiaomisum.ryze.function.Function;
import io.github.xiaomisum.ryze.function.builtin.*;
import io.github.xiaomisum.ryze.interceptor.report.*;
import io.github.xiaomisum.ryze.protocol.debug.config.DebugDefaults;
import io.github.xiaomisum.ryze.protocol.debug.processer.DebugPostprocessor;
import io.github.xiaomisum.ryze.protocol.debug.processer.DebugPreprocessor;
import io.github.xiaomisum.ryze.protocol.debug.sampler.DebugSampler;
import io.github.xiaomisum.ryze.protocol.http.assertion.HTTPResponseAssertion;
import io.github.xiaomisum.ryze.protocol.http.config.HTTPDefaults;
import io.github.xiaomisum.ryze.protocol.http.extractor.HTTPHeaderExtractor;
import io.github.xiaomisum.ryze.protocol.http.processor.HTTPPostprocessor;
import io.github.xiaomisum.ryze.protocol.http.processor.HTTPPreprocessor;
import io.github.xiaomisum.ryze.protocol.http.sampler.HTTPSampler;
import io.github.xiaomisum.ryze.protocol.jdbc.config.JDBCDatasource;
import io.github.xiaomisum.ryze.protocol.jdbc.config.JDBCJSONInterceptor;
import io.github.xiaomisum.ryze.protocol.jdbc.processor.JDBCPostprocessor;
import io.github.xiaomisum.ryze.protocol.jdbc.processor.JDBCPreprocessor;
import io.github.xiaomisum.ryze.protocol.jdbc.sampler.JDBCSampler;
import io.github.xiaomisum.ryze.protocol.redis.config.RedisDatasource;
import io.github.xiaomisum.ryze.protocol.redis.config.RedisJSONInterceptor;
import io.github.xiaomisum.ryze.protocol.redis.processor.RedisPostprocessor;
import io.github.xiaomisum.ryze.protocol.redis.processor.RedisPreprocessor;
import io.github.xiaomisum.ryze.protocol.redis.sampler.RedisSampler;
import io.github.xiaomisum.ryze.support.fastjson.interceptor.JSONInterceptor;
import io.github.xiaomisum.ryze.testelement.TestElement;
import io.github.xiaomisum.ryze.testelement.TestSuite;
import io.github.xiaomisum.ryze.testelement.configure.ConfigureElement;
import io.github.xiaomisum.ryze.testelement.processor.Postprocessor;
import io.github.xiaomisum.ryze.testelement.processor.Preprocessor;
import io.github.xiaomisum.ryze.testelement.processor.builtin.SyncTimer;
import org.hamcrest.Matcher;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static io.github.xiaomisum.ryze.testelement.TestElementConstantsInterface.TESTSUITE;

@SuppressWarnings("all")
public class ApplicationConfigTest {

    @Test
    public void testGetTestElementKeyMap() {
        Map<String, Class<? extends TestElement>> map = ApplicationConfig.getTestElementKeyMap();
        Assert.assertNotNull(map);
        Assert.assertTrue(map.get(TESTSUITE) == TestSuite.class);
        Assert.assertTrue(map.get("TestSuite".toLowerCase()) == TestSuite.class);
        Assert.assertTrue(map.get("debug".toLowerCase()) == DebugSampler.class);
        Assert.assertTrue(map.get("debug_sampler".toLowerCase()) == DebugSampler.class);
        Assert.assertTrue(map.get("http".toLowerCase()) == HTTPSampler.class);
        Assert.assertTrue(map.get("https".toLowerCase()) == HTTPSampler.class);
        Assert.assertTrue(map.get("http_sampler".toLowerCase()) == HTTPSampler.class);
        Assert.assertTrue(map.get("HTTPSampler".toLowerCase()) == HTTPSampler.class);
        Assert.assertTrue(map.get("jdbc".toLowerCase()) == JDBCSampler.class);
        Assert.assertTrue(map.get("jdbc_sampler".toLowerCase()) == JDBCSampler.class);
        Assert.assertTrue(map.get("JDBCSampler".toLowerCase()) == JDBCSampler.class);
        Assert.assertTrue(map.get("redis".toLowerCase()) == RedisSampler.class);
        Assert.assertTrue(map.get("redis_sampler".toLowerCase()) == RedisSampler.class);
        Assert.assertTrue(map.get("RedisSampler".toLowerCase()) == RedisSampler.class);
    }

    @Test
    public void testGetConfigureElementKeyMap() {
        Map<String, Class<? extends ConfigureElement>> map = ApplicationConfig.getConfigureElementKeyMap();
        Assert.assertNotNull(map);
        Assert.assertTrue(map.get("debugconfig".toLowerCase()) == DebugDefaults.class);
        Assert.assertTrue(map.get("debug_config".toLowerCase()) == DebugDefaults.class);
        Assert.assertTrue(map.get("debug".toLowerCase()) == DebugDefaults.class);
        Assert.assertTrue(map.get("DebugDefaults".toLowerCase()) == DebugDefaults.class);
        Assert.assertTrue(map.get("http".toLowerCase()) == HTTPDefaults.class);
        Assert.assertTrue(map.get("https".toLowerCase()) == HTTPDefaults.class);
        Assert.assertTrue(map.get("http_defaults".toLowerCase()) == HTTPDefaults.class);
        Assert.assertTrue(map.get("HTTPDefaults".toLowerCase()) == HTTPDefaults.class);
        Assert.assertTrue(map.get("jdbc".toLowerCase()) == JDBCDatasource.class);
        Assert.assertTrue(map.get("jdbc_datasource".toLowerCase()) == JDBCDatasource.class);
        Assert.assertTrue(map.get("jdbc_data_source".toLowerCase()) == JDBCDatasource.class);
        Assert.assertTrue(map.get("JDBCDatasource".toLowerCase()) == JDBCDatasource.class);
        Assert.assertTrue(map.get("redis".toLowerCase()) == RedisDatasource.class);
        Assert.assertTrue(map.get("redis_datasource".toLowerCase()) == RedisDatasource.class);
        Assert.assertTrue(map.get("redis_data_source".toLowerCase()) == RedisDatasource.class);
        Assert.assertTrue(map.get("RedisDatasource".toLowerCase()) == RedisDatasource.class);
    }

    @Test
    public void testGetPreprocessorKeyMap() {
        Map<String, Class<? extends Preprocessor>> map = ApplicationConfig.getPreprocessorKeyMap();
        Assert.assertNotNull(map);
        Assert.assertTrue(map.get("debug_preprocessor".toLowerCase()) == DebugPreprocessor.class);
        Assert.assertTrue(map.get("debug_pre_processor".toLowerCase()) == DebugPreprocessor.class);
        Assert.assertTrue(map.get("debug".toLowerCase()) == DebugPreprocessor.class);
        Assert.assertTrue(map.get("DebugPreprocessor".toLowerCase()) == DebugPreprocessor.class);
        Assert.assertTrue(map.get("http".toLowerCase()) == HTTPPreprocessor.class);
        Assert.assertTrue(map.get("https".toLowerCase()) == HTTPPreprocessor.class);
        Assert.assertTrue(map.get("http_preprocessor".toLowerCase()) == HTTPPreprocessor.class);
        Assert.assertTrue(map.get("http_preprocessor".toLowerCase()) == HTTPPreprocessor.class);
        Assert.assertTrue(map.get("HTTPPreprocessor".toLowerCase()) == HTTPPreprocessor.class);
        Assert.assertTrue(map.get("jdbc".toLowerCase()) == JDBCPreprocessor.class);
        Assert.assertTrue(map.get("jdbc_preprocessor".toLowerCase()) == JDBCPreprocessor.class);
        Assert.assertTrue(map.get("jdbc_pre_processor".toLowerCase()) == JDBCPreprocessor.class);
        Assert.assertTrue(map.get("JDBCPreprocessor".toLowerCase()) == JDBCPreprocessor.class);
        Assert.assertTrue(map.get("redis".toLowerCase()) == RedisPreprocessor.class);
        Assert.assertTrue(map.get("redis_preprocessor".toLowerCase()) == RedisPreprocessor.class);
        Assert.assertTrue(map.get("redis_pre_processor".toLowerCase()) == RedisPreprocessor.class);
        Assert.assertTrue(map.get("RedisPreprocessor".toLowerCase()) == RedisPreprocessor.class);
    }

    @Test
    public void testGetPostprocessorKeyMap() {
        Map<String, Class<? extends Postprocessor>> map = ApplicationConfig.getPostprocessorKeyMap();
        Assert.assertNotNull(map);
        Assert.assertTrue(map.get("debug_postprocessor".toLowerCase()) == DebugPostprocessor.class);
        Assert.assertTrue(map.get("debug_post_processor".toLowerCase()) == DebugPostprocessor.class);
        Assert.assertTrue(map.get("debug".toLowerCase()) == DebugPostprocessor.class);
        Assert.assertTrue(map.get("DebugPostprocessor".toLowerCase()) == DebugPostprocessor.class);
        Assert.assertTrue(map.get("http".toLowerCase()) == HTTPPostprocessor.class);
        Assert.assertTrue(map.get("https".toLowerCase()) == HTTPPostprocessor.class);
        Assert.assertTrue(map.get("http_postprocessor".toLowerCase()) == HTTPPostprocessor.class);
        Assert.assertTrue(map.get("http_post_processor".toLowerCase()) == HTTPPostprocessor.class);
        Assert.assertTrue(map.get("HTTPPostprocessor".toLowerCase()) == HTTPPostprocessor.class);
        Assert.assertTrue(map.get("jdbc".toLowerCase()) == JDBCPostprocessor.class);
        Assert.assertTrue(map.get("jdbc_postprocessor".toLowerCase()) == JDBCPostprocessor.class);
        Assert.assertTrue(map.get("jdbc_post_processor".toLowerCase()) == JDBCPostprocessor.class);
        Assert.assertTrue(map.get("JDBCPostprocessor".toLowerCase()) == JDBCPostprocessor.class);
        Assert.assertTrue(map.get("redis".toLowerCase()) == RedisPostprocessor.class);
        Assert.assertTrue(map.get("redis_postprocessor".toLowerCase()) == RedisPostprocessor.class);
        Assert.assertTrue(map.get("redis_post_processor".toLowerCase()) == RedisPostprocessor.class);
        Assert.assertTrue(map.get("RedisPostprocessor".toLowerCase()) == RedisPostprocessor.class);
        Assert.assertTrue(map.get("Timer".toLowerCase()) == SyncTimer.class);
        Assert.assertTrue(map.get("sync_timer".toLowerCase()) == SyncTimer.class);
        Assert.assertTrue(map.get("def_timer".toLowerCase()) == SyncTimer.class);
        Assert.assertTrue(map.get("defTimer".toLowerCase()) == SyncTimer.class);
        Assert.assertTrue(map.get("SyncTimer".toLowerCase()) == SyncTimer.class);
    }

    @Test
    public void testGetAssertionKeyMap() {
        Map<String, Class<? extends Assertion>> map = ApplicationConfig.getAssertionKeyMap();
        Assert.assertNotNull(map);
        Assert.assertTrue(map.get("json_assertion".toLowerCase()) == JSONAssertion.class);
        Assert.assertTrue(map.get("json".toLowerCase()) == JSONAssertion.class);
        Assert.assertTrue(map.get("JSONAssertion".toLowerCase()) == JSONAssertion.class);
        Assert.assertTrue(map.get("Result_Assertion".toLowerCase()) == ResultAssertion.class);
        Assert.assertTrue(map.get("result".toLowerCase()) == ResultAssertion.class);
        Assert.assertTrue(map.get("ResultAssertion".toLowerCase()) == ResultAssertion.class);
        Assert.assertTrue(map.get("HTTPAssertion".toLowerCase()) == HTTPResponseAssertion.class);
        Assert.assertTrue(map.get("HTTP_Assertion".toLowerCase()) == HTTPResponseAssertion.class);
        Assert.assertTrue(map.get("http".toLowerCase()) == HTTPResponseAssertion.class);
        Assert.assertTrue(map.get("https".toLowerCase()) == HTTPResponseAssertion.class);
        Assert.assertTrue(map.get("HTTPResponseAssertion".toLowerCase()) == HTTPResponseAssertion.class);
        Assert.assertTrue(map.get("status".toLowerCase()) == StatusAssertion.class);
        Assert.assertTrue(map.get("status_code".toLowerCase()) == StatusAssertion.class);
        Assert.assertTrue(map.get("status_assertion".toLowerCase()) == StatusAssertion.class);
        Assert.assertTrue(map.get("StatusAssertion".toLowerCase()) == StatusAssertion.class);
    }

    @Test
    public void testGetExtractorKeyMap() {
        Map<String, Class<? extends Extractor>> map = ApplicationConfig.getExtractorKeyMap();
        Assert.assertNotNull(map);
        Assert.assertTrue(map.get("json_extractor".toLowerCase()) == JSONExtractor.class);
        Assert.assertTrue(map.get("json".toLowerCase()) == JSONExtractor.class);
        Assert.assertTrue(map.get("JSONExtractor".toLowerCase()) == JSONExtractor.class);
        Assert.assertTrue(map.get("regex_extractor".toLowerCase()) == RegexExtractor.class);
        Assert.assertTrue(map.get("regex".toLowerCase()) == RegexExtractor.class);
        Assert.assertTrue(map.get("RegexExtractor".toLowerCase()) == RegexExtractor.class);
        Assert.assertTrue(map.get("ResultExtractor".toLowerCase()) == ResultExtractor.class);
        Assert.assertTrue(map.get("result_extractor".toLowerCase()) == ResultExtractor.class);
        Assert.assertTrue(map.get("result".toLowerCase()) == ResultExtractor.class);
        Assert.assertTrue(map.get("http_header_extractor".toLowerCase()) == HTTPHeaderExtractor.class);
        Assert.assertTrue(map.get("http_header".toLowerCase()) == HTTPHeaderExtractor.class);
        Assert.assertTrue(map.get("HTTPHeader".toLowerCase()) == HTTPHeaderExtractor.class);
        Assert.assertTrue(map.get("http".toLowerCase()) == HTTPHeaderExtractor.class);
        Assert.assertTrue(map.get("https".toLowerCase()) == HTTPHeaderExtractor.class);
        Assert.assertTrue(map.get("HTTPHeaderExtractor".toLowerCase()) == HTTPHeaderExtractor.class);
    }

    @Test
    public void testGetFunctions() {
        List<Function> functions = ApplicationConfig.getFunctions();
        Assert.assertNotNull(functions);
        var map = functions.stream().collect(Collectors.toMap(Function::key, Function::getClass));
        Assert.assertTrue(map.get("Digest".toLowerCase()) == Digest.class);
        Assert.assertTrue(map.get("google2fa".toLowerCase()) == GoogleAuthCode.class);
        Assert.assertTrue(map.get("Json".toLowerCase()) == Json.class);
        Assert.assertTrue(map.get("json_read".toLowerCase()) == JsonRead.class);
        Assert.assertTrue(map.get("random".toLowerCase()) == Random.class);
        Assert.assertTrue(map.get("random_string".toLowerCase()) == RandomString.class);
        Assert.assertTrue(map.get("time_shift".toLowerCase()) == TimeShift.class);
        Assert.assertTrue(map.get("timestamp".toLowerCase()) == Timestamp.class);
        Assert.assertTrue(map.get("url_decode".toLowerCase()) == UrlDecode.class);
        Assert.assertTrue(map.get("url_encode".toLowerCase()) == UrlEncode.class);
        Assert.assertTrue(map.get("Uuid".toLowerCase()) == Uuid.class);
        Assert.assertTrue(map.get("Faker".toLowerCase()) == Faker.class);
    }

    @Test
    public void testGetMatcherKeyMap() {
        Map<String, Class<? extends Matcher>> map = ApplicationConfig.getMatcherKeyMap();
        Assert.assertNotNull(map);
        Assert.assertTrue(map.get("any_contains".toLowerCase()) == AnyContainsMatcher.class);
        Assert.assertTrue(map.get("contains_any".toLowerCase()) == AnyContainsMatcher.class);
        Assert.assertTrue(map.get("any_contains".toLowerCase()) == AnyContainsMatcher.class);
        Assert.assertTrue(map.get("contain_any".toLowerCase()) == AnyContainsMatcher.class);
        Assert.assertTrue(map.get("AnyContainsMatcher".toLowerCase()) == AnyContainsMatcher.class);
        Assert.assertTrue(map.get("equals_any".toLowerCase()) == AnyEqualsMatcher.class);
        Assert.assertTrue(map.get("any_equals".toLowerCase()) == AnyEqualsMatcher.class);
        Assert.assertTrue(map.get("equal_any".toLowerCase()) == AnyEqualsMatcher.class);
        Assert.assertTrue(map.get("any_equal".toLowerCase()) == AnyEqualsMatcher.class);
        Assert.assertTrue(map.get("eq_any".toLowerCase()) == AnyEqualsMatcher.class);
        Assert.assertTrue(map.get("any_eq".toLowerCase()) == AnyEqualsMatcher.class);
        Assert.assertTrue(map.get("AnyEqualsMatcher".toLowerCase()) == AnyEqualsMatcher.class);
        Assert.assertTrue(map.get("contains".toLowerCase()) == ContainsMatcher.class);
        Assert.assertTrue(map.get("ct".toLowerCase()) == ContainsMatcher.class);
        Assert.assertTrue(map.get("包含".toLowerCase()) == ContainsMatcher.class);
        Assert.assertTrue(map.get("⊆".toLowerCase()) == ContainsMatcher.class);
        Assert.assertTrue(map.get("contain".toLowerCase()) == ContainsMatcher.class);
        Assert.assertTrue(map.get("ContainsMatcher".toLowerCase()) == ContainsMatcher.class);
        Assert.assertTrue(map.get("equals".toLowerCase()) == EqualsMatcher.class);
        Assert.assertTrue(map.get("equal".toLowerCase()) == EqualsMatcher.class);
        Assert.assertTrue(map.get("qe".toLowerCase()) == EqualsMatcher.class);
        Assert.assertTrue(map.get("is".toLowerCase()) == EqualsMatcher.class);
        Assert.assertTrue(map.get("=".toLowerCase()) == EqualsMatcher.class);
        Assert.assertTrue(map.get("==".toLowerCase()) == EqualsMatcher.class);
        Assert.assertTrue(map.get("===".toLowerCase()) == EqualsMatcher.class);
        Assert.assertTrue(map.get("等于".toLowerCase()) == EqualsMatcher.class);
        Assert.assertTrue(map.get("相等".toLowerCase()) == EqualsMatcher.class);
        Assert.assertTrue(map.get("EqualsMatcher".toLowerCase()) == EqualsMatcher.class);
        Assert.assertTrue(map.get(">".toLowerCase()) == GreaterMatcher.class);
        Assert.assertTrue(map.get("greater".toLowerCase()) == GreaterMatcher.class);
        Assert.assertTrue(map.get("greater_than".toLowerCase()) == GreaterMatcher.class);
        Assert.assertTrue(map.get("gt".toLowerCase()) == GreaterMatcher.class);
        Assert.assertTrue(map.get("GreaterMatcher".toLowerCase()) == GreaterMatcher.class);
        Assert.assertTrue(map.get(">=".toLowerCase()) == GreaterOrEqualsMatcher.class);
        Assert.assertTrue(map.get("大于等于".toLowerCase()) == GreaterOrEqualsMatcher.class);
        Assert.assertTrue(map.get("greater_or_equals".toLowerCase()) == GreaterOrEqualsMatcher.class);
        Assert.assertTrue(map.get("greater_than_or_equals".toLowerCase()) == GreaterOrEqualsMatcher.class);
        Assert.assertTrue(map.get("gte".toLowerCase()) == GreaterOrEqualsMatcher.class);
        Assert.assertTrue(map.get("GreaterOrEqualsMatcher".toLowerCase()) == GreaterOrEqualsMatcher.class);
        Assert.assertTrue(map.get("<".toLowerCase()) == LessMatcher.class);
        Assert.assertTrue(map.get("小于".toLowerCase()) == LessMatcher.class);
        Assert.assertTrue(map.get("less".toLowerCase()) == LessMatcher.class);
        Assert.assertTrue(map.get("less_than".toLowerCase()) == LessMatcher.class);
        Assert.assertTrue(map.get("lt".toLowerCase()) == LessMatcher.class);
        Assert.assertTrue(map.get("LessMatcher".toLowerCase()) == LessMatcher.class);
        Assert.assertTrue(map.get("<=".toLowerCase()) == LessOrEqualsMatcher.class);
        Assert.assertTrue(map.get("小于等于".toLowerCase()) == LessOrEqualsMatcher.class);
        Assert.assertTrue(map.get("less_or_equals".toLowerCase()) == LessOrEqualsMatcher.class);
        Assert.assertTrue(map.get("less_than_or_equals".toLowerCase()) == LessOrEqualsMatcher.class);
        Assert.assertTrue(map.get("lte".toLowerCase()) == LessOrEqualsMatcher.class);
        Assert.assertTrue(map.get("LessOrEqualsMatcher".toLowerCase()) == LessOrEqualsMatcher.class);
        Assert.assertTrue(map.get("not_contains".toLowerCase()) == NotContainsMatcher.class);
        Assert.assertTrue(map.get("not_contain".toLowerCase()) == NotContainsMatcher.class);
        Assert.assertTrue(map.get("nct".toLowerCase()) == NotContainsMatcher.class);
        Assert.assertTrue(map.get("不包含".toLowerCase()) == NotContainsMatcher.class);
        Assert.assertTrue(map.get("⊈".toLowerCase()) == NotContainsMatcher.class);
        Assert.assertTrue(map.get("not_equals".toLowerCase()) == NotEqualsMatcher.class);
        Assert.assertTrue(map.get("not_equal".toLowerCase()) == NotEqualsMatcher.class);
        Assert.assertTrue(map.get("neq".toLowerCase()) == NotEqualsMatcher.class);
        Assert.assertTrue(map.get("不等于".toLowerCase()) == NotEqualsMatcher.class);
        Assert.assertTrue(map.get("!=".toLowerCase()) == NotEqualsMatcher.class);
        Assert.assertTrue(map.get("<>".toLowerCase()) == NotEqualsMatcher.class);
        Assert.assertTrue(map.get("not".toLowerCase()) == NotEqualsMatcher.class);
        Assert.assertTrue(map.get("is_not".toLowerCase()) == NotEqualsMatcher.class);
        Assert.assertTrue(map.get("NotEqualsMatcher".toLowerCase()) == NotEqualsMatcher.class);
        Assert.assertTrue(map.get("regex".toLowerCase()) == RegexMatcher.class);
        Assert.assertTrue(map.get("rx".toLowerCase()) == RegexMatcher.class);
        Assert.assertTrue(map.get("正则".toLowerCase()) == RegexMatcher.class);
        Assert.assertTrue(map.get("正则表达式".toLowerCase()) == RegexMatcher.class);
        Assert.assertTrue(map.get("RegexMatcher".toLowerCase()) == RegexMatcher.class);
        Assert.assertTrue(map.get("same".toLowerCase()) == SameObjectMatcher.class);
        Assert.assertTrue(map.get("object".toLowerCase()) == SameObjectMatcher.class);
        Assert.assertTrue(map.get("same_object".toLowerCase()) == SameObjectMatcher.class);
        Assert.assertTrue(map.get("SameObjectMatcher".toLowerCase()) == SameObjectMatcher.class);
    }

    @Test
    public void testGetJsonInterceptorKeyMap() {
        Map<Class<?>, JSONInterceptor> map = ApplicationConfig.getJsonInterceptorKeyMap();
        Assert.assertNotNull(map);
        // jdbc 相关反序列测试
        Assert.assertTrue(map.containsKey(JDBCSampler.class));
        Assert.assertTrue(map.containsKey(JDBCDatasource.class));
        Assert.assertTrue(map.containsKey(JDBCPreprocessor.class));
        Assert.assertTrue(map.containsKey(JDBCPostprocessor.class));
        Assert.assertTrue(map.get(JDBCSampler.class) instanceof JDBCJSONInterceptor);
        Assert.assertTrue(map.get(JDBCDatasource.class) instanceof JDBCJSONInterceptor);
        Assert.assertTrue(map.get(JDBCPreprocessor.class) instanceof JDBCJSONInterceptor);
        Assert.assertTrue(map.get(JDBCSampler.class) instanceof JDBCJSONInterceptor);

        // redis 相关反序列测试
        Assert.assertTrue(map.containsKey(RedisSampler.class));
        Assert.assertTrue(map.containsKey(RedisDatasource.class));
        Assert.assertTrue(map.containsKey(RedisPreprocessor.class));
        Assert.assertTrue(map.containsKey(RedisPostprocessor.class));
        Assert.assertTrue(map.get(RedisSampler.class) instanceof RedisJSONInterceptor);
        Assert.assertTrue(map.get(RedisDatasource.class) instanceof RedisJSONInterceptor);
        Assert.assertTrue(map.get(RedisPreprocessor.class) instanceof RedisJSONInterceptor);
        Assert.assertTrue(map.get(RedisPostprocessor.class) instanceof RedisJSONInterceptor);
    }

    @Test
    public void testGetReporterListeners() {
        List<? extends ReporterListener> listeners = ApplicationConfig.getReporterListeners();
        Assert.assertNotNull(listeners);
        Assert.assertFalse(listeners.stream().filter(listener -> listener instanceof TestContainerLogListener).toList().isEmpty());
        Assert.assertFalse(listeners.stream().filter(listener -> listener instanceof ProcessorLogListener).toList().isEmpty());
        Assert.assertFalse(listeners.stream().filter(listener -> listener instanceof SamplerLogListener).toList().isEmpty());
        Assert.assertFalse(listeners.stream().filter(listener -> listener instanceof TestContainerAllureReportListener).toList().isEmpty());
        Assert.assertFalse(listeners.stream().filter(listener -> listener instanceof ProcessorAllureReportListener).toList().isEmpty());
        Assert.assertFalse(listeners.stream().filter(listener -> listener instanceof SamplerAllureReportListener).toList().isEmpty());
    }

    @Test
    public void testGetDataMapCaching() {
        // 测试缓存机制 - 多次调用应该返回相同的实例
        Map<String, Class<? extends TestElement>> map1 = ApplicationConfig.getTestElementKeyMap();
        Map<String, Class<? extends TestElement>> map2 = ApplicationConfig.getTestElementKeyMap();
        Assert.assertSame(map1, map2);

        // 测试其他方法的缓存机制
        List<Function> functions1 = ApplicationConfig.getFunctions();
        List<Function> functions2 = ApplicationConfig.getFunctions();
        Assert.assertSame(functions1, functions2);
    }
}