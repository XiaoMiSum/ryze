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

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

/**
 * URL编码函数实现类
 *
 * <p>该类用于对字符串进行URL编码操作。
 * 常用于构造URL参数、编码特殊字符等场景。</p>
 *
 * <p>在测试用例中可以通过 ${url_encode()} 的方式调用该函数。</p>
 *
 * @author xiaomi
 */
public class UrlEncode implements Function {

    @Override
    public String key() {
        return "url_encode";
    }

    /**
     * 将传入的字符串进行URL编码，支持一个参数
     * <p>
     * W3C标准规定，当Content-Type为application/x-www-form-urlencoded时，
     * URL中查询参数名和参数值中空格要用加号+替代，所以几乎所有使用该规范的浏览器在表单提交后，
     * URL查询参数中空格都会被编成加号+。
     * 而在另一份规范(RFC 2396，定义URI)里, URI里的保留字符都需转义成%HH格式(Section 3.4 Query Component)，
     * 因此空格会被编码成%20，加号+本身也作为保留字而被编成%2B，对于某些遵循RFC 2396标准的应用来说，
     * 它可能不接受查询字符串中出现加号+，认为它是非法字符。
     * 所以一个安全的举措是URL中统一使用%20来编码空格字符。
     * <p>
     * Java中的URLEncoder本意是用来把字符串编码成application/x-www-form-urlencoded MIME格式字符串，
     * 也就是说仅仅适用于URL中的查询字符串部分，
     * 但是URLEncoder经常被用来对URL的其他部分编码，它的encode方法会把空格编成加号+，
     * 与之对应的是，URLDecoder的decode方法会把加号+和%20都解码为空格
     *
     * <p>参数说明：
     * <ol>
     *   <li>content: 待URL编码的字符串，非空</li>
     * </ol>
     * </p>
     *
     * <p>使用示例：
     * <pre>
     * ${url_encode("Hello World")}     // 返回 "Hello%20World"
     * ${url_encode("中文")}            // 返回 "%E4%B8%AD%E6%96%87"
     * </pre>
     * </p>
     *
     * @param context 上下文对象
     * @param args    参数列表，包含待编码的字符串
     * @return 编码后的字符串
     * @throws RuntimeException 当参数数量不正确或内容为空时抛出异常
     */
    @Override
    public String execute(ContextWrapper context, Args args) {
        checkMethodArgCount(args, 1, 1);
        String content = args.getFirstString();
        if (StringUtils.isBlank(content)) {
            throw new IllegalArgumentException("content con not be null");
        }
        String result = URLEncoder.encode(content, StandardCharsets.UTF_8);

        return result.replaceAll("\\+", "%20");
    }
}