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

package io.github.xiaomisum.ryze.support;

import org.apache.commons.lang3.StringUtils;

import java.util.Objects;
import java.util.StringJoiner;

/**
 * {@link Validatable#validate()} 验证结果实体类
 *
 * @author xiaomi
 */
public class ValidateResult {

    private final StringJoiner reason = new StringJoiner("\n");
    private boolean valid = true;

    /**
     * 仅追加描述信息，不改变验证结果
     *
     * @param description 描述信息
     * @return 当前对象
     */
    public ValidateResult appendDescription(String description) {
        if (StringUtils.isNotBlank(description)) {
            reason.add(description);
        }
        return this;
    }

    /**
     * 追加指定 Validatable 的验证结果
     *
     * @param validatable 要验证的对象
     */
    public ValidateResult append(Validatable validatable) {
        if (!Objects.isNull(validatable)) {
            var result = validatable.validate();
            // 任意一个失败，即为失败
            if (!result.isValid()) {
                valid = false;
                reason.add(result.getReason());
            }
        }
        return this;
    }

    /**
     * 追加失败原因，并暗示验证失败
     *
     * @param extraReason 额外的失败原因
     * @return 当前对象
     * @see #appendDescription(String)
     */
    public ValidateResult append(String extraReason) {
        if (StringUtils.isNotBlank(extraReason)) {
            valid = false;
            reason.add(extraReason);
        }
        return this;
    }

    /**
     * 追加失败原因，并暗示验证失败
     *
     * @param extraReasonTemplate 额外的失败原因（模板，基于 String.format）
     * @param args                模板参数
     * @return 更新后的验证结果对象
     * @see #appendDescription(String)
     */
    public ValidateResult append(String extraReasonTemplate, Object... args) {
        if (StringUtils.isNotBlank(extraReasonTemplate)) {
            return append(String.format(extraReasonTemplate, args));
        }
        return this;
    }

    public boolean isValid() {
        return valid;
    }

    public String getReason() {
        return reason.toString();
    }

    public void valid() {
        if (!valid) {
            throw new IllegalArgumentException(reason.toString());
        }
    }

}
