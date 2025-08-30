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

package io.github.xiaomisum.ryze.assertion.builtin.matcher;

import io.github.xiaomisum.ryze.assertion.Matchers;
import org.testng.Assert;
import org.testng.annotations.Test;

public class NumberMatcherTest {

    @Test
    public void testLessThan() {
        NumberMatcher matcher = new NumberMatcher(10, Matchers.CompareOperator.LESS_THAN) {
            @Override
            public boolean matches(Object actual) {
                return super.matches(actual);
            }
        };
        
        Assert.assertTrue(matcher.matches(5));
        Assert.assertFalse(matcher.matches(15));
        Assert.assertFalse(matcher.matches(10));
    }

    @Test
    public void testLessThanOrEqual() {
        NumberMatcher matcher = new NumberMatcher(10, Matchers.CompareOperator.LESS_THAN_OR_EQUAL) {
            @Override
            public boolean matches(Object actual) {
                return super.matches(actual);
            }
        };
        
        Assert.assertTrue(matcher.matches(5));
        Assert.assertFalse(matcher.matches(15));
        Assert.assertTrue(matcher.matches(10));
    }

    @Test
    public void testGreaterThan() {
        NumberMatcher matcher = new NumberMatcher(10, Matchers.CompareOperator.GREATER_THAN) {
            @Override
            public boolean matches(Object actual) {
                return super.matches(actual);
            }
        };
        
        Assert.assertFalse(matcher.matches(5));
        Assert.assertTrue(matcher.matches(15));
        Assert.assertFalse(matcher.matches(10));
    }

    @Test
    public void testGreaterThanOrEqual() {
        NumberMatcher matcher = new NumberMatcher(10, Matchers.CompareOperator.GREATER_THAN_OR_EQUAL) {
            @Override
            public boolean matches(Object actual) {
                return super.matches(actual);
            }
        };
        
        Assert.assertFalse(matcher.matches(5));
        Assert.assertTrue(matcher.matches(15));
        Assert.assertTrue(matcher.matches(10));
    }

    @Test
    public void testConvertToDouble() {
        NumberMatcher matcher = new NumberMatcher("10.5", Matchers.CompareOperator.GREATER_THAN) {
            @Override
            public boolean matches(Object actual) {
                return super.matches(actual);
            }
        };
        
        Assert.assertFalse(matcher.matches(5.5));
        Assert.assertTrue(matcher.matches(15.5));
    }

    @Test
    public void testDescribeTo() {
        NumberMatcher matcher = new NumberMatcher(10, Matchers.CompareOperator.GREATER_THAN) {
            @Override
            public boolean matches(Object actual) {
                return super.matches(actual);
            }
        };
        String description = matcher.toString();
        Assert.assertNotNull(description);
    }
}