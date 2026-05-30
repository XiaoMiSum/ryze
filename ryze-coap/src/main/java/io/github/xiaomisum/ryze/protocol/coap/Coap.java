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

package io.github.xiaomisum.ryze.protocol.coap;

import groovy.lang.Closure;
import groovy.lang.DelegatesTo;
import io.github.xiaomisum.ryze.Result;
import io.github.xiaomisum.ryze.Ryze;
import io.github.xiaomisum.ryze.protocol.coap.sampler.CoapSampler;
import io.github.xiaomisum.ryze.support.Customizer;
import io.github.xiaomisum.ryze.support.groovy.Groovy;

/**
 * CoAP协议魔法盒子类
 * <p>
 * 提供函数式编程方式执行CoAP取样器的便捷入口，支持多种配置方式：
 * 1. 通过Groovy闭包方式配置取样器
 * 2. 通过Customizer函数式接口配置取样器
 * 支持GET/POST/PUT/DELETE四种方法的便捷入口。
 * </p>
 *
 * @author xiaomi
 */
public class Coap {

    /**
     * 通过Groovy闭包执行CoAP取样器测试（无标题）
     */
    public static Result coap(@DelegatesTo(strategy = Closure.DELEGATE_ONLY, value = CoapSampler.Builder.class) Closure<?> closure) {
        return coap("", closure);
    }

    /**
     * 通过Groovy闭包执行CoAP取样器测试（带标题）
     */
    public static Result coap(String title,
                              @DelegatesTo(strategy = Closure.DELEGATE_ONLY, value = CoapSampler.Builder.class) Closure<?> closure) {
        var builder = CoapSampler.builder();
        Groovy.call(closure, builder);
        return Ryze.runTest(title, builder.build());
    }

    /**
     * 通过Customizer函数式接口执行CoAP取样器测试（无标题）
     */
    public static Result coap(Customizer<CoapSampler.Builder> customizer) {
        return coap("", customizer);
    }

    /**
     * 通过Customizer函数式接口执行CoAP取样器测试（带标题）
     */
    public static Result coap(String title, Customizer<CoapSampler.Builder> customizer) {
        var builder = CoapSampler.builder();
        customizer.customize(builder);
        return Ryze.runTest(title, builder.build());
    }

    /**
     * CoAP GET请求（无标题）
     */
    public static Result get(Customizer<CoapSampler.Builder> customizer) {
        return get("", customizer);
    }

    /**
     * CoAP GET请求（带标题）
     */
    public static Result get(String title, Customizer<CoapSampler.Builder> customizer) {
        var builder = CoapSampler.builder();
        customizer.customize(builder);
        builder.config(c -> c.get());
        return Ryze.runTest(title, builder.build());
    }

    /**
     * CoAP POST请求（无标题）
     */
    public static Result post(Customizer<CoapSampler.Builder> customizer) {
        return post("", customizer);
    }

    /**
     * CoAP POST请求（带标题）
     */
    public static Result post(String title, Customizer<CoapSampler.Builder> customizer) {
        var builder = CoapSampler.builder();
        customizer.customize(builder);
        builder.config(c -> c.post());
        return Ryze.runTest(title, builder.build());
    }

    /**
     * CoAP PUT请求（无标题）
     */
    public static Result put(Customizer<CoapSampler.Builder> customizer) {
        return put("", customizer);
    }

    /**
     * CoAP PUT请求（带标题）
     */
    public static Result put(String title, Customizer<CoapSampler.Builder> customizer) {
        var builder = CoapSampler.builder();
        customizer.customize(builder);
        builder.config(c -> c.put());
        return Ryze.runTest(title, builder.build());
    }

    /**
     * CoAP DELETE请求（无标题）
     */
    public static Result delete(Customizer<CoapSampler.Builder> customizer) {
        return delete("", customizer);
    }

    /**
     * CoAP DELETE请求（带标题）
     */
    public static Result delete(String title, Customizer<CoapSampler.Builder> customizer) {
        var builder = CoapSampler.builder();
        customizer.customize(builder);
        builder.config(c -> c.delete());
        return Ryze.runTest(title, builder.build());
    }
}
