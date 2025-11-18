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

package io.github.xiaomisum.ryze.support.dataloader.parser;

import com.alibaba.fastjson2.JSON;

import java.lang.reflect.Type;

/**
 * JSON解析器
 * <p>
 * 该类专门用于解析JSON格式的数据，支持将JSON字符串转换为指定类型的对象。
 * 使用FastJSON2作为底层解析库，提供高效的JSON解析能力。
 * </p>
 *
 * @author xiaomi
 */
public class JsonParser {

    /**
     * 解析JSON字符串
     * <p>
     * 将JSON字符串解析为指定类型的对象。根据目标类型自动判断是否为集合类型，
     * 并采用相应的解析策略。
     * </p>
     *
     * @param json JSON字符串
     * @param type 目标类型(Type)，用于泛型类型的数据转换
     * @return 解析后的对象
     */
    public static Object parse(String json, Type type) {
        return JSON.parseObject(json, type);
    }
}