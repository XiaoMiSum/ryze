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

package io.github.xiaomisum.ryze.function.builtin;

import com.alibaba.fastjson2.JSONObject;
import io.github.xiaomisum.ryze.context.ContextWrapper;
import io.github.xiaomisum.ryze.function.Args;
import io.github.xiaomisum.ryze.function.Function;

/**
 * JSON对象生成函数实现类
 *
 * <p>该类用于将传入的键值对参数转换为JSONObject对象。
 * 常用于构造JSON格式的请求体、配置参数等场景。</p>
 *
 * <p>在测试用例中可以通过 ${json()} 的方式调用该函数。</p>
 *
 * @author xiaomi
 */
public class Json implements Function {

    @Override
    public String key() {
        return "json";
    }

    /**
     * 将传入的参数转换为JSONObject
     *
     * <p>参数为多个键值对字符串，格式为"key=value"，每个键值对生成JSONObject的一个属性。</p>
     *
     * <p>使用示例：
     * <pre>
     * ${json("name=张三", "age=25")}     // 生成 {"name":"张三", "age":"25"} 的JSON对象
     * ${json("id=1", "status=active")}   // 生成 {"id":"1", "status":"active"} 的JSON对象
     * </pre>
     * </p>
     *
     * @param context 上下文对象
     * @param args    参数列表，包含多个"key=value"格式的键值对
     * @return 生成的JSONObject对象
     * @throws RuntimeException 当参数为空时抛出异常
     */
    @Override
    public Object execute(ContextWrapper context, Args args) {
        if (args.isEmpty()) {
            throw new IllegalArgumentException("parameters con not be null");
        }
        JSONObject json = new JSONObject();
        args.forEach(item -> {
            String[] arr = ((String) item).split("=");
            json.put(arr[0], arr[1]);
        });
        return json;
    }
}