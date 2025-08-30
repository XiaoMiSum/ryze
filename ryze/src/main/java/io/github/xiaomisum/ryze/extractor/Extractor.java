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

package io.github.xiaomisum.ryze.extractor;

import com.alibaba.fastjson2.annotation.JSONType;
import io.github.xiaomisum.ryze.context.ContextWrapper;
import io.github.xiaomisum.ryze.support.Validatable;
import io.github.xiaomisum.ryze.support.fastjson.deserializer.ExtractorObjectReader;

/**
 * 提取器接口，定义了从测试结果中提取数据的功能规范
 * 
 * <p>提取器用于从测试执行结果（如HTTP响应、数据库查询结果等）中按照指定规则提取数据，
 * 并将提取到的数据保存到测试上下文的变量中供后续步骤使用。</p>
 * 
 * <p>该接口的实现类需要具备以下能力：
 * <ul>
 *   <li>处理测试上下文中的结果数据</li>
 *   <li>根据预定义规则提取所需数据</li>
 *   <li>将提取结果存储到上下文变量中</li>
 *   <li>处理提取失败的情况（如使用默认值）</li>
 * </ul>
 * </p>
 * 
 * <p>提取器通常与测试取样器配合使用，在取样器执行完成后，提取器会处理取样器的结果，
 * 提取需要的数据并存储为变量，供后续的验证器或其他组件使用。</p>
 * 
 * @author xiaomi
 * @see Validatable
 * @see ContextWrapper
 */
@JSONType(deserializer = ExtractorObjectReader.class)
public interface Extractor extends Validatable {

    /**
     * 提取器执行方法，从取样器结果中按指定规格提取数据，并保存到变量中
     *
     * <p>该方法是提取器的核心处理逻辑，负责：
     * <ol>
     *   <li>检查测试执行状态，只有在测试通过的情况下才进行提取</li>
     *   <li>从测试上下文获取取样结果</li>
     *   <li>根据具体提取器的实现逻辑提取数据</li>
     *   <li>将提取结果保存到上下文的本地变量中</li>
     *   <li>处理提取失败或异常情况</li>
     * </ol></p>
     *
     * @param context 测试上下文，包含测试结果和变量信息
     * @throws RuntimeException 当提取过程中发生无法处理的错误时抛出
     */
    void process(ContextWrapper context);
}