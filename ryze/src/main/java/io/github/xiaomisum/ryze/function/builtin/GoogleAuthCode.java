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
import org.apache.commons.codec.binary.Base32;
import org.apache.commons.lang3.StringUtils;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

/**
 * 谷歌验证码生成函数实现类
 *
 * <p>该类实现了Google Authenticator两步验证算法，用于生成6位数字验证码。
 * 常用于需要双因素认证的测试场景，如登录验证、交易确认等。</p>
 *
 * <p>在测试用例中可以通过 ${google2fa()} 的方式调用该函数。</p>
 *
 * @author xiaomi
 */
public class GoogleAuthCode implements Function {

    private static final int LENGTH = 5;
    private static final int SCRATCH_CODE_LENGTH = 8;
    private static final int BYTES_PER_SCRATCH_CODE = 4;

    @Override
    public String key() {
        return "google2fa";
    }

    /**
     * 获取谷歌验证码，支持一个参数，且不允许为空
     *
     * <p>参数说明：
     * <ol>
     *   <li>secret: 谷歌验证器安全码，非空，通过该安全码生成谷歌验证码</li>
     * </ol>
     * </p>
     *
     * <p>使用示例：
     * <pre>
     * ${google2fa("SECRETKEY")}     // 根据安全码生成当前的6位验证码
     * </pre>
     * </p>
     *
     * @param context 上下文对象
     * @param args    参数列表，包含谷歌验证器安全码
     * @return 6位数字的谷歌验证码
     * @throws RuntimeException 当安全码为空时抛出异常
     */
    @Override
    public Object execute(ContextWrapper context, Args args) {
        checkMethodArgCount(args, 1, 1);
        if (StringUtils.isBlank(args.getFirstString())) {
            throw new IllegalArgumentException("Google 2FA SecretKey 不能为空");
        }
        return generateVerifyCode(args.getFirstString());
    }


    /**
     * 根据安全码生成谷歌验证码
     *
     * @param secretKey 安全码
     * @return 6位数字的谷歌验证码
     */
    private String generateVerifyCode(String secretKey) {
        var t = (System.currentTimeMillis() / 1000L) / 30L;
        byte[] decodedKey = new Base32().decode(secretKey);
        try {
            var code = String.valueOf(generateVerifyCode(decodedKey, t));
            return code.length() == LENGTH ? "0" + code : code;
        } catch (Exception e) {
            throw new RuntimeException("generateVerifyCode exception", e);
        }
    }

    /**
     * 基于HmacSHA1算法生成验证码
     *
     * @param key 安全码字节数组
     * @param t   时间窗口
     * @return 验证码数字
     * @throws NoSuchAlgorithmException 当算法不支持时抛出
     * @throws InvalidKeyException      当密钥无效时抛出
     */
    private int generateVerifyCode(byte[] key, long t) throws NoSuchAlgorithmException, InvalidKeyException {
        byte[] data = new byte[8];
        var value = t;
        for (var i = SCRATCH_CODE_LENGTH; i-- > 0; value >>>= SCRATCH_CODE_LENGTH) {
            data[i] = (byte) value;
        }
        var signKey = new SecretKeySpec(key, "HmacSHA1");
        var mac = Mac.getInstance("HmacSHA1");
        mac.init(signKey);
        byte[] hash = mac.doFinal(data);
        var offset = hash[20 - 1] & 0xF;
        // We're using a long because Java hasn't got unsigned int.
        var truncatedHash = 0;
        for (var i = 0; i < BYTES_PER_SCRATCH_CODE; ++i) {
            truncatedHash <<= 8;
            // We are dealing with signed bytes:
            // we just keep the first byte.
            truncatedHash |= (hash[offset + i] & 0xFF);
        }
        truncatedHash &= 0x7FFFFFFF;
        truncatedHash %= 1000000;
        return truncatedHash;
    }

}