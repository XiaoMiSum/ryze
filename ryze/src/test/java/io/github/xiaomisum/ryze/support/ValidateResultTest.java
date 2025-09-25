/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2025.  Lorem XiaoMiSum (mi_xiao@qq.com)
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

import org.testng.Assert;
import org.testng.annotations.Test;

public class ValidateResultTest {

    @Test
    public void testDefaultValidateResult() {
        ValidateResult result = new ValidateResult();

        // 默认情况下验证结果应该是有效的
        Assert.assertTrue(result.isValid());

        // 默认情况下原因应该是空的
        Assert.assertEquals(result.getReason(), "");
    }

    @Test
    public void testAppendDescription() {
        ValidateResult result = new ValidateResult();

        // 添加描述信息
        result.appendDescription("This is a test description");

        // 验证结果仍然有效
        Assert.assertTrue(result.isValid());

        // 验证原因包含描述信息
        Assert.assertEquals(result.getReason(), "This is a test description");

        // 添加更多描述信息
        result.appendDescription(" and more information");

        // 验证原因包含所有描述信息
        Assert.assertEquals(result.getReason(), "This is a test description and more information");

        // 添加空描述信息不应该改变结果
        result.appendDescription(null);
        result.appendDescription("");
        result.appendDescription("   ");

        // 验证结果不变
        Assert.assertEquals(result.getReason(), "This is a test description and more information");
    }

    @Test
    public void testAppendWithValidatable() {
        ValidateResult result = new ValidateResult();

        // 创建一个有效的可验证对象
        Validatable validValidatable = new Validatable() {
        };

        // 添加有效的可验证对象
        result.append(validValidatable);

        // 验证结果仍然有效
        Assert.assertTrue(result.isValid());
        Assert.assertEquals(result.getReason(), "");

        // 创建一个无效的可验证对象
        Validatable invalidValidatable = new Validatable() {
            @Override
            public ValidateResult validate() {
                return new ValidateResult().append("Invalid object");
            }
        };

        // 添加无效的可验证对象
        result.append(invalidValidatable);

        // 验证结果变为无效
        Assert.assertFalse(result.isValid());
        Assert.assertEquals(result.getReason(), "Invalid object");

        // 添加null对象不应该改变结果
        result.append((Validatable) null);
        Assert.assertFalse(result.isValid());
        Assert.assertEquals(result.getReason(), "Invalid object");
    }

    @Test
    public void testAppendWithReason() {
        ValidateResult result = new ValidateResult();

        // 添加失败原因
        result.append("This is a failure reason");

        // 验证结果变为无效
        Assert.assertFalse(result.isValid());

        // 验证原因包含失败原因
        Assert.assertEquals(result.getReason(), "This is a failure reason");

        // 添加更多失败原因
        result.append(" and more details");

        // 验证原因包含所有失败原因
        Assert.assertEquals(result.getReason(), "This is a failure reason and more details");

        // 添加空原因不应该改变结果
        result.append((String) null);
        result.append("");
        result.append("   ");

        // 验证结果不变
        Assert.assertEquals(result.getReason(), "This is a failure reason and more details");
    }

    @Test
    public void testAppendWithTemplate() {
        ValidateResult result = new ValidateResult();

        // 添加带模板的失败原因
        result.append("Error code: %d, message: %s", 404, "Not Found");

        // 验证结果变为无效
        Assert.assertFalse(result.isValid());

        // 验证原因包含格式化后的失败原因
        Assert.assertEquals(result.getReason(), "Error code: 404, message: Not Found");

        // 添加空模板不应该改变结果
        result.append(null, "arg1", "arg2");
        result.append("", "arg1", "arg2");
        result.append("   ", "arg1", "arg2");

        // 验证结果不变
        Assert.assertEquals(result.getReason(), "Error code: 404, message: Not Found");
    }

    @Test
    public void testChainCalls() {
        ValidateResult result = new ValidateResult();

        // 测试链式调用
        result.appendDescription("Starting validation")
                .append("Validation failed")
                .append("Error code: %d", 500);

        // 验证最终结果
        Assert.assertFalse(result.isValid());
        Assert.assertEquals(result.getReason(), "Starting validationValidation failedError code: 500");
    }
}