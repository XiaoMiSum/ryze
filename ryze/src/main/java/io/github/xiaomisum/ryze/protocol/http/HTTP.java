package io.github.xiaomisum.ryze.protocol.http;

import io.github.xiaomisum.ryze.testelement.sampler.DefaultSampleResult;
import xyz.migoo.simplehttp.Request;
import xyz.migoo.simplehttp.Response;

/**
 * HTTP协议执行器
 * <p>
 * 该类是HTTP协议的核心执行器，负责发送HTTP请求并收集响应结果。
 * 使用simplehttp库作为底层HTTP客户端实现。
 * </p>
 *
 * <p>主要功能：
 * <ul>
 *   <li>执行HTTP请求</li>
 *   <li>记录请求执行时间</li>
 *   <li>处理请求异常</li>
 * </ul>
 * </p>
 *
 * @author xiaomi
 */
public class HTTP {

    /**
     * 执行HTTP请求并返回响应结果
     * <p>
     * 该方法会执行指定的HTTP请求，并在采样结果中记录执行时间。
     * 如果请求执行过程中发生异常，会将其包装为RuntimeException抛出。
     * </p>
     *
     * @param request 要执行的HTTP请求对象
     * @param result 采样结果对象，用于记录执行时间
     * @return HTTP响应对象
     * @throws RuntimeException 当请求执行过程中发生异常时抛出
     */
    public static Response execute(Request request, DefaultSampleResult result) {
        result.sampleStart();
        try {
            return request.execute();
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            result.sampleEnd();
        }
    }
}