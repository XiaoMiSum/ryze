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

import io.github.xiaomisum.ryze.context.ContextWrapper;
import io.github.xiaomisum.ryze.function.Args;
import io.github.xiaomisum.ryze.function.Function;
import org.apache.commons.lang3.StringUtils;

import java.lang.reflect.Method;
import java.util.Locale;

/**
 * Faker数据生成函数实现类
 *
 * <p>该类基于com.github.javafaker.Faker库实现，用于生成各种类型的模拟数据，
 * 如姓名、地址、电话号码、邮箱等。常用于测试场景中需要大量模拟数据的情况。</p>
 *
 * <p>在测试用例中可以通过 ${faker()} 的方式调用该函数。</p>
 *
 * @author xiaomi
 * @see <a href="https://github.com/DiUS/java-faker">Java Faker库</a>
 */
public class Faker implements Function {

    @Override
    public String key() {
        return "faker";
    }

    /**
     * 通过Faker库生成假数据，支持2个参数
     *
     * <p>参数说明：
     * <ol>
     *   <li>key: 假数据类型，格式为Faker库的ClassName.Method，如"name.fullName"、"address.city"</li>
     *   <li>locale: 语言区域设置，可选，默认为"zh-CN"（中文）</li>
     * </ol>
     * </p>
     *
     * <p>使用示例：
     * <pre>
     * ${faker("name.fullName")}              // 生成中文姓名
     * ${faker("address.city", "en-US")}      // 生成英文城市名
     * ${faker("phoneNumber.cellPhone")}      // 生成手机号码
     * </pre>
     * </p>
     *
     * @param context 上下文对象
     * @param args    参数列表，包含数据类型和语言区域设置
     * @return 生成的模拟数据
     * @throws RuntimeException 当参数格式错误或反射调用失败时抛出异常
     */
    @Override
    public Object execute(ContextWrapper context, Args args) {
        checkMethodArgCount(args, 1, 2);
        var key = args.getString(0);
        if (StringUtils.isBlank(key)) {
            throw new IllegalArgumentException("第一个参数不能为空，参考格式： Faker库的ClassName.Method");
        }
        var locale = args.size() < 2 ? "zh-CN" : args.getLast().toString();

        try {
            var faker = new com.github.javafaker.Faker(Locale.of(locale));
            var keys = key.split("\\.");
            var current = faker.getClass().getDeclaredMethod(StringUtils.uncapitalize(keys[0])).invoke(faker);
            Method method = current.getClass().getDeclaredMethod(StringUtils.uncapitalize(keys[1]));
            return method.invoke(current);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}