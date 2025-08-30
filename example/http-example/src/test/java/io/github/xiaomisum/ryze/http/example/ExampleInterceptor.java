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

package io.github.xiaomisum.ryze.http.example;

import io.github.xiaomisum.ryze.context.ContextWrapper;
import io.github.xiaomisum.ryze.interceptor.RyzeInterceptor;
import io.github.xiaomisum.ryze.protocol.http.sampler.HTTPSampler;

/**
 * @author xiaomi
 * Created at 2025/8/17 11:27
 */
public class ExampleInterceptor implements RyzeInterceptor<HTTPSampler> {
    @Override
    public int getOrder() {
        return 0;
    }

    @Override
    public boolean supports(ContextWrapper context) {
        return context.getTestElement() instanceof HTTPSampler;
    }

    @Override
    public boolean preHandle(ContextWrapper context, HTTPSampler runtime) {
        System.out.println("ExampleInterceptor preHandle");
        return true;
    }

    @Override
    public void postHandle(ContextWrapper context, HTTPSampler runtime) {
        System.out.println("ExampleInterceptor postHandle");
    }

    @Override
    public void afterCompletion(ContextWrapper context) {
        System.out.println("ExampleInterceptor afterCompletion");
    }
}
