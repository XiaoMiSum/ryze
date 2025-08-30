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

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONPath;
import io.github.xiaomisum.ryze.context.ContextWrapper;
import io.github.xiaomisum.ryze.function.Args;
import io.github.xiaomisum.ryze.function.Function;
import org.apache.commons.lang3.StringUtils;

import java.util.Objects;

/**
 * JSON路径读取函数实现类
 *
 * <p>该类用于通过JSONPath表达式从JSON对象中提取数据。
 * 常用于从响应数据中提取特定字段值进行后续处理或验证。</p>
 *
 * <p>在测试用例中可以通过 ${json_read()} 的方式调用该函数。</p>
 *
 * @author mi.xiao
 * @since 2021/5/31 23:32
 */
public class JsonRead implements Function {

    @Override
    public String key() {
        return "json_read";
    }

    /**
     * 通过json path读取数据，需要两个参数
     *
     * <p>参数说明：
     * <ol>
     *   <li>json: JSON对象</li>
     *   <li>path: JSONPath表达式，用于定位需要提取的数据</li>
     * </ol>
     * </p>
     *
     * <p>使用示例：
     * <pre>
     * ${json_read(response, "$.data.id")}                    // 从response中提取data.id字段
     * </pre>
     * </p>
     *
     * @param context 上下文对象
     * @param args    参数列表，包含JSON对象和JSONPath表达式
     * @return JSONPath对应的数据
     * @throws RuntimeException 当参数数量不正确或参数为空时抛出异常
     */
    @Override
    public Object execute(ContextWrapper context, Args args) {
        checkMethodArgCount(args, 2, 2);
        return execute(args.getFirst(), args.getString(1));
    }

    /**
     * 执行JSONPath数据提取
     *
     * @param json JSON对象或JSON字符串
     * @param path JSONPath表达式
     * @return 提取到的数据
     * @throws RuntimeException 当json或path为空时抛出异常
     */
    private Object execute(Object json, String path) {
        if (Objects.isNull(json) || StringUtils.isBlank(path)) {
            throw new IllegalArgumentException("json or jsonpath con not be null");
        }
        if (json instanceof String str) {
            if (!JSON.isValid(str)) {
                throw new IllegalArgumentException("thr first arg require json");
            }
            return JSONPath.extract(str, path);
        }
        return JSONPath.extract(JSON.toJSONString(json), path);
    }
}