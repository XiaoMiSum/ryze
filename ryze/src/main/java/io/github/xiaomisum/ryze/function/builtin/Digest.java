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
import org.apache.commons.codec.binary.Hex;
import org.apache.commons.lang3.StringUtils;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * 消息摘要函数实现类
 *
 * <p>该类实现了消息摘要算法功能，支持MD5、SHA等常见的摘要算法。
 * 可用于生成数据的摘要值，常用于密码加密、数据完整性校验等场景。</p>
 *
 * <p>在测试用例中可以通过 ${digest()} 的方式调用该函数。</p>
 *
 * @author xiaomi
 */
public class Digest implements Function {

    @Override
    public String key() {
        return "digest";
    }

    /**
     * 获取信息摘要，通常为MD5，支持四个参数
     *
     * <p>参数说明：
     * <ol>
     *   <li>algorithm: 算法名称，如"md5"、"sha1"等，允许为空，默认为"md5"</li>
     *   <li>content: 待编码的原始内容，非空</li>
     *   <li>salt：盐值，用于增加摘要的复杂度，允许为空</li>
     *   <li>upper: 是否将结果转为大写，允许为空，默认false</li>
     * </ol>
     * </p>
     *
     * <p>使用示例：
     * <pre>
     * ${digest("md5", "password")}                   // 返回password的md5摘要
     * ${digest("sha1", "password", "salt", true)}    // 返回password+salt的sha1大写摘要
     * </pre>
     * </p>
     *
     * @param context 上下文对象
     * @param args    参数列表，包含算法、内容、盐值和大小写选项
     * @return 生成的信息摘要字符串
     * @throws RuntimeException 当content为空或算法不支持时抛出异常
     */
    @Override
    public Object execute(ContextWrapper context, Args args) {
        checkMethodArgCount(args, 1, 4);
        if (args.size() == 1) {
            args.addFirst("md5");
        }
        var algorithm = (String) args.getFirst();
        var content = (String) args.get(1);
        var salt = args.getString(2);
        var upper = args.getBooleanValue(3);
        return hex(algorithm, content, salt, upper);
    }


    /**
     * 执行摘要算法并生成十六进制字符串
     *
     * @param algorithm 算法名称，如"md5"、"sha1"等
     * @param content   待处理的内容
     * @param salt      盐值
     * @param upper     是否转换为大写
     * @return 生成的十六进制摘要字符串
     * @throws IllegalArgumentException 当content为空时抛出
     * @throws RuntimeException         当算法不支持时抛出
     */
    private String hex(String algorithm, String content, String salt, boolean upper) {
        if (StringUtils.isBlank(content)) {
            throw new IllegalArgumentException("content is null or empty");
        }
        algorithm = StringUtils.isBlank(algorithm) ? "md5" : algorithm.trim();
        try {
            var md = MessageDigest.getInstance(algorithm);
            md.update(content.getBytes(StandardCharsets.UTF_8));
            if (StringUtils.isNotBlank(salt)) {
                md.update(salt.getBytes(StandardCharsets.UTF_8));
            }
            byte[] bytes = md.digest();
            return upper ? new String(Hex.encodeHex(bytes)).toUpperCase() : new String(Hex.encodeHex(bytes));
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalArgumentException(e.getMessage(), e);
        }
    }

}