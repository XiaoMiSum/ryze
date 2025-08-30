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

package io.github.xiaomisum.ryze.support.fastjson.deserializer;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.alibaba.fastjson2.JSONReader;
import com.alibaba.fastjson2.reader.ObjectReader;
import io.github.xiaomisum.ryze.testelement.TestElementConfigureGroup;

import java.lang.reflect.Type;

/**
 * 测试元素配置组对象反序列化器
 * <p>
 * 该类实现了FastJSON的ObjectReader接口，用于将JSON数据反序列化为TestElementConfigureGroup对象。
 * TestElementConfigureGroup用于存储测试元素的配置信息组。
 * </p>
 *
 * @author xiaomi
 * Created at 2025/7/19 22:16
 */
public class TestElementConfigureObjectReader implements ObjectReader<TestElementConfigureGroup> {

    /**
     * 从JSON读取器中读取并构建测试元素配置组对象
     * <p>
     * 该方法会解析JSON数据并直接创建TestElementConfigureGroup实例。
     * </p>
     *
     * @param jsonReader JSON读取器
     * @param fieldType  字段类型
     * @param fieldName  字段名称
     * @param features   特性标志
     * @return 解析后的测试元素配置组对象
     */
    @Override
    public TestElementConfigureGroup readObject(JSONReader jsonReader, Type fieldType, Object fieldName, long features) {
        var value = jsonReader.readObject();
        return JSONObject.parseObject(JSON.toJSONString(value), TestElementConfigureGroup.class);
    }
}