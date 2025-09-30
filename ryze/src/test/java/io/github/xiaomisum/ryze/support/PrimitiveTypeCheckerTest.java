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

@SuppressWarnings("all")
public class PrimitiveTypeCheckerTest {

    @Test
    public void testIsPrimitiveType() {
        // 测试基本类型
        Assert.assertTrue(PrimitiveTypeChecker.isPrimitiveType(true));
        Assert.assertTrue(PrimitiveTypeChecker.isPrimitiveType((byte) 1));
        Assert.assertTrue(PrimitiveTypeChecker.isPrimitiveType('a'));
        Assert.assertTrue(PrimitiveTypeChecker.isPrimitiveType((short) 1));
        Assert.assertTrue(PrimitiveTypeChecker.isPrimitiveType(1));
        Assert.assertTrue(PrimitiveTypeChecker.isPrimitiveType(1L));
        Assert.assertTrue(PrimitiveTypeChecker.isPrimitiveType(1.0f));
        Assert.assertTrue(PrimitiveTypeChecker.isPrimitiveType(1.0d));
        Assert.assertTrue(PrimitiveTypeChecker.isPrimitiveType("test"));

        // 测试包装类型（应该返回false）
        Assert.assertFalse(PrimitiveTypeChecker.isPrimitiveType(Boolean.TRUE));
        Assert.assertFalse(PrimitiveTypeChecker.isPrimitiveType((byte) 1));
        Assert.assertFalse(PrimitiveTypeChecker.isPrimitiveType('a'));
        Assert.assertFalse(PrimitiveTypeChecker.isPrimitiveType((short) 1));
        Assert.assertFalse(PrimitiveTypeChecker.isPrimitiveType(1));
        Assert.assertFalse(PrimitiveTypeChecker.isPrimitiveType(1L));
        Assert.assertFalse(PrimitiveTypeChecker.isPrimitiveType(1.0f));
        Assert.assertFalse(PrimitiveTypeChecker.isPrimitiveType(1.0d));

        // 测试非基本类型
        Assert.assertFalse(PrimitiveTypeChecker.isPrimitiveType(new Object()));
        Assert.assertFalse(PrimitiveTypeChecker.isPrimitiveType(new int[]{1, 2, 3}));

        // 测试null
        Assert.assertFalse(PrimitiveTypeChecker.isPrimitiveType(null));
    }

    @Test
    public void testIsWrapperType() {
        // 测试包装类型
        Assert.assertTrue(PrimitiveTypeChecker.isWrapperType(Boolean.TRUE));
        Assert.assertTrue(PrimitiveTypeChecker.isWrapperType((byte) 1));
        Assert.assertTrue(PrimitiveTypeChecker.isWrapperType('a'));
        Assert.assertTrue(PrimitiveTypeChecker.isWrapperType((short) 1));
        Assert.assertTrue(PrimitiveTypeChecker.isWrapperType(1));
        Assert.assertTrue(PrimitiveTypeChecker.isWrapperType(1L));
        Assert.assertTrue(PrimitiveTypeChecker.isWrapperType(1.0f));
        Assert.assertTrue(PrimitiveTypeChecker.isWrapperType(1.0d));
        Assert.assertTrue(PrimitiveTypeChecker.isWrapperType("test"));

        // 测试基本类型
        Assert.assertTrue(PrimitiveTypeChecker.isWrapperType(true));
        Assert.assertTrue(PrimitiveTypeChecker.isWrapperType((byte) 1));
        Assert.assertTrue(PrimitiveTypeChecker.isWrapperType('a'));
        Assert.assertTrue(PrimitiveTypeChecker.isWrapperType((short) 1));
        Assert.assertTrue(PrimitiveTypeChecker.isWrapperType(1));
        Assert.assertTrue(PrimitiveTypeChecker.isWrapperType(1L));
        Assert.assertTrue(PrimitiveTypeChecker.isWrapperType(1.0f));
        Assert.assertTrue(PrimitiveTypeChecker.isWrapperType(1.0d));

        // 测试非包装类型
        Assert.assertFalse(PrimitiveTypeChecker.isWrapperType(new Object()));
        Assert.assertFalse(PrimitiveTypeChecker.isWrapperType(new int[]{1, 2, 3}));

        // 测试null
        Assert.assertFalse(PrimitiveTypeChecker.isWrapperType(null));
    }

    @Test
    public void testIsPrimitiveOrWrapper() {
        // 测试基本类型
        Assert.assertTrue(PrimitiveTypeChecker.isPrimitiveOrWrapper(true));
        Assert.assertTrue(PrimitiveTypeChecker.isPrimitiveOrWrapper((byte) 1));
        Assert.assertTrue(PrimitiveTypeChecker.isPrimitiveOrWrapper('a'));
        Assert.assertTrue(PrimitiveTypeChecker.isPrimitiveOrWrapper((short) 1));
        Assert.assertTrue(PrimitiveTypeChecker.isPrimitiveOrWrapper(1));
        Assert.assertTrue(PrimitiveTypeChecker.isPrimitiveOrWrapper(1L));
        Assert.assertTrue(PrimitiveTypeChecker.isPrimitiveOrWrapper(1.0f));
        Assert.assertTrue(PrimitiveTypeChecker.isPrimitiveOrWrapper(1.0d));
        Assert.assertTrue(PrimitiveTypeChecker.isPrimitiveOrWrapper("test"));

        // 测试包装类型
        Assert.assertTrue(PrimitiveTypeChecker.isPrimitiveOrWrapper(Boolean.TRUE));
        Assert.assertTrue(PrimitiveTypeChecker.isPrimitiveOrWrapper((byte) 1));
        Assert.assertTrue(PrimitiveTypeChecker.isPrimitiveOrWrapper('a'));
        Assert.assertTrue(PrimitiveTypeChecker.isPrimitiveOrWrapper((short) 1));
        Assert.assertTrue(PrimitiveTypeChecker.isPrimitiveOrWrapper(1));
        Assert.assertTrue(PrimitiveTypeChecker.isPrimitiveOrWrapper(1L));
        Assert.assertTrue(PrimitiveTypeChecker.isPrimitiveOrWrapper(1.0f));
        Assert.assertTrue(PrimitiveTypeChecker.isPrimitiveOrWrapper(1.0d));
        Assert.assertTrue(PrimitiveTypeChecker.isPrimitiveOrWrapper("test"));

        // 测试非基本类型
        Assert.assertFalse(PrimitiveTypeChecker.isPrimitiveOrWrapper(new Object()));
        Assert.assertFalse(PrimitiveTypeChecker.isPrimitiveOrWrapper(new int[]{1, 2, 3}));

        // 测试null
        Assert.assertFalse(PrimitiveTypeChecker.isPrimitiveOrWrapper(null));
    }

    @Test
    public void testIsPrimitiveClass() {
        // 测试基本类型class
        Assert.assertTrue(PrimitiveTypeChecker.isPrimitiveClass(boolean.class));
        Assert.assertTrue(PrimitiveTypeChecker.isPrimitiveClass(byte.class));
        Assert.assertTrue(PrimitiveTypeChecker.isPrimitiveClass(char.class));
        Assert.assertTrue(PrimitiveTypeChecker.isPrimitiveClass(short.class));
        Assert.assertTrue(PrimitiveTypeChecker.isPrimitiveClass(int.class));
        Assert.assertTrue(PrimitiveTypeChecker.isPrimitiveClass(long.class));
        Assert.assertTrue(PrimitiveTypeChecker.isPrimitiveClass(float.class));
        Assert.assertTrue(PrimitiveTypeChecker.isPrimitiveClass(double.class));
        Assert.assertTrue(PrimitiveTypeChecker.isPrimitiveClass(void.class));
        Assert.assertTrue(PrimitiveTypeChecker.isPrimitiveClass(String.class));

        // 测试包装类型class（应该返回false）
        Assert.assertFalse(PrimitiveTypeChecker.isPrimitiveClass(Boolean.class));
        Assert.assertFalse(PrimitiveTypeChecker.isPrimitiveClass(Byte.class));
        Assert.assertFalse(PrimitiveTypeChecker.isPrimitiveClass(Character.class));
        Assert.assertFalse(PrimitiveTypeChecker.isPrimitiveClass(Short.class));
        Assert.assertFalse(PrimitiveTypeChecker.isPrimitiveClass(Integer.class));
        Assert.assertFalse(PrimitiveTypeChecker.isPrimitiveClass(Long.class));
        Assert.assertFalse(PrimitiveTypeChecker.isPrimitiveClass(Float.class));
        Assert.assertFalse(PrimitiveTypeChecker.isPrimitiveClass(Double.class));
        Assert.assertFalse(PrimitiveTypeChecker.isPrimitiveClass(Void.class));

        // 测试非基本类型class
        Assert.assertFalse(PrimitiveTypeChecker.isPrimitiveClass(Object.class));
        Assert.assertFalse(PrimitiveTypeChecker.isPrimitiveClass(int[].class));

        // 测试null
        Assert.assertFalse(PrimitiveTypeChecker.isPrimitiveClass(null));
    }

    @Test
    public void testIsWrapperClass() {
        // 测试包装类型class
        Assert.assertTrue(PrimitiveTypeChecker.isWrapperClass(Boolean.class));
        Assert.assertTrue(PrimitiveTypeChecker.isWrapperClass(Byte.class));
        Assert.assertTrue(PrimitiveTypeChecker.isWrapperClass(Character.class));
        Assert.assertTrue(PrimitiveTypeChecker.isWrapperClass(Short.class));
        Assert.assertTrue(PrimitiveTypeChecker.isWrapperClass(Integer.class));
        Assert.assertTrue(PrimitiveTypeChecker.isWrapperClass(Long.class));
        Assert.assertTrue(PrimitiveTypeChecker.isWrapperClass(Float.class));
        Assert.assertTrue(PrimitiveTypeChecker.isWrapperClass(Double.class));
        Assert.assertTrue(PrimitiveTypeChecker.isWrapperClass(Void.class));
        Assert.assertTrue(PrimitiveTypeChecker.isWrapperClass(String.class));

        // 测试基本类型class（应该返回false）
        Assert.assertFalse(PrimitiveTypeChecker.isWrapperClass(boolean.class));
        Assert.assertFalse(PrimitiveTypeChecker.isWrapperClass(byte.class));
        Assert.assertFalse(PrimitiveTypeChecker.isWrapperClass(char.class));
        Assert.assertFalse(PrimitiveTypeChecker.isWrapperClass(short.class));
        Assert.assertFalse(PrimitiveTypeChecker.isWrapperClass(int.class));
        Assert.assertFalse(PrimitiveTypeChecker.isWrapperClass(long.class));
        Assert.assertFalse(PrimitiveTypeChecker.isWrapperClass(float.class));
        Assert.assertFalse(PrimitiveTypeChecker.isWrapperClass(double.class));
        Assert.assertFalse(PrimitiveTypeChecker.isWrapperClass(void.class));

        // 测试非包装类型class
        Assert.assertFalse(PrimitiveTypeChecker.isWrapperClass(Object.class));
        Assert.assertFalse(PrimitiveTypeChecker.isWrapperClass(int[].class));

        // 测试null
        Assert.assertFalse(PrimitiveTypeChecker.isWrapperClass(null));
    }

    @Test
    public void testIsPrimitiveOrWrapperClass() {
        // 测试基本类型class
        Assert.assertTrue(PrimitiveTypeChecker.isPrimitiveOrWrapperClass(boolean.class));
        Assert.assertTrue(PrimitiveTypeChecker.isPrimitiveOrWrapperClass(byte.class));
        Assert.assertTrue(PrimitiveTypeChecker.isPrimitiveOrWrapperClass(char.class));
        Assert.assertTrue(PrimitiveTypeChecker.isPrimitiveOrWrapperClass(short.class));
        Assert.assertTrue(PrimitiveTypeChecker.isPrimitiveOrWrapperClass(int.class));
        Assert.assertTrue(PrimitiveTypeChecker.isPrimitiveOrWrapperClass(long.class));
        Assert.assertTrue(PrimitiveTypeChecker.isPrimitiveOrWrapperClass(float.class));
        Assert.assertTrue(PrimitiveTypeChecker.isPrimitiveOrWrapperClass(double.class));
        Assert.assertTrue(PrimitiveTypeChecker.isPrimitiveOrWrapperClass(void.class));
        Assert.assertTrue(PrimitiveTypeChecker.isPrimitiveOrWrapperClass(String.class));

        // 测试包装类型class
        Assert.assertTrue(PrimitiveTypeChecker.isPrimitiveOrWrapperClass(Boolean.class));
        Assert.assertTrue(PrimitiveTypeChecker.isPrimitiveOrWrapperClass(Byte.class));
        Assert.assertTrue(PrimitiveTypeChecker.isPrimitiveOrWrapperClass(Character.class));
        Assert.assertTrue(PrimitiveTypeChecker.isPrimitiveOrWrapperClass(Short.class));
        Assert.assertTrue(PrimitiveTypeChecker.isPrimitiveOrWrapperClass(Integer.class));
        Assert.assertTrue(PrimitiveTypeChecker.isPrimitiveOrWrapperClass(Long.class));
        Assert.assertTrue(PrimitiveTypeChecker.isPrimitiveOrWrapperClass(Float.class));
        Assert.assertTrue(PrimitiveTypeChecker.isPrimitiveOrWrapperClass(Double.class));
        Assert.assertTrue(PrimitiveTypeChecker.isPrimitiveOrWrapperClass(Void.class));
        Assert.assertTrue(PrimitiveTypeChecker.isPrimitiveOrWrapperClass(String.class));

        // 测试非基本类型class
        Assert.assertFalse(PrimitiveTypeChecker.isPrimitiveOrWrapperClass(Object.class));
        Assert.assertFalse(PrimitiveTypeChecker.isPrimitiveOrWrapperClass(int[].class));

        // 测试null
        Assert.assertFalse(PrimitiveTypeChecker.isPrimitiveOrWrapperClass(null));
    }
}