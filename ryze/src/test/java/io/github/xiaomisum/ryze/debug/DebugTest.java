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

package io.github.xiaomisum.ryze.debug;

import io.github.xiaomisum.ryze.MagicBox;
import io.github.xiaomisum.ryze.SessionRunner;
import org.testng.annotations.Test;

/**
 * @author xiaomi
 * Created at 2026/1/12 16:26
 */
public class DebugTest {

    @Test
    public void test1() {
        SessionRunner.getSessionIfNoneCreateNew();
        MagicBox.debug("测试用例- test()", sampler -> sampler
                .postprocessors(post -> post.debug(debug -> debug.title("条件不执行").async().condition("false")))
                .config(config -> config.add("test", "t"))
                .build());
        SessionRunner.removeSession();
    }

    @Test
    public void test2() {
        SessionRunner.getSessionIfNoneCreateNew();
        MagicBox.debug("测试用例- test()", sampler -> sampler
                .postprocessors(post -> post.debug(debug -> debug.title("条件执行").async().condition("true")))
                .config(config -> config.add("test", "t"))
                .build());
        SessionRunner.removeSession();
    }
}
