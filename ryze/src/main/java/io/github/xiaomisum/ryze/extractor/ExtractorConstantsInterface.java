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

/**
 * 提取器常量接口，定义了提取器组件中使用的标准字段名称
 * 
 * <p>该接口集中管理提取器相关的标准字段名常量，确保在整个系统中字段名称的一致性。
 * 所有提取器实现类都应该实现此接口以使用标准字段名称。</p>
 * 
 * @author xiaomi
 */
public interface ExtractorConstantsInterface {

    /**
     * 提取表达式字段名，用于指定提取数据的规则或路径
     * 
     * <p>根据不同类型的提取器，该字段的含义会有所不同：
     * <ul>
     *   <li>对于JSON提取器：表示JsonPath表达式</li>
     *   <li>对于正则表达式提取器：表示正则表达式</li>
     *   <li>对于HTTP头提取器：表示要提取的头字段名称</li>
     * </ul></p>
     */
    String FIELD = "field";

    /**
     * 默认值字段名，当提取失败时使用的备选值
     * 
     * <p>当提取器无法从目标数据中提取到期望值时，如果配置了默认值，
     * 系统将使用该默认值作为提取结果，而不是报告错误。</p>
     */
    String DEFAULT_VALUE = "default_value";

    /**
     * 变量名称字段名，已废弃，使用REF_NAME替代
     * 
     * <p>该字段名用于历史兼容性，新代码应使用REF_NAME。</p>
     * 
     * @deprecated 使用REF_NAME替代
     */
    String VARIABLE_NAME = "variable_name";

    /**
     * 引用名称字段名，用于指定提取结果存储的变量名
     * 
     * <p>提取器成功提取数据后，会将结果以该名称作为键存储到测试上下文的变量映射中，
     * 后续的测试步骤可以通过该名称引用提取的值。</p>
     */
    String REF_NAME = "ref_name";

    /**
     * 匹配序号字段名，用于指定要提取第几个匹配项
     * 
     * <p>当目标数据中存在多个匹配项时，通过该字段指定要提取第几个匹配结果。
     * 序号从0开始计数，即0表示第一个匹配项，1表示第二个匹配项，以此类推。</p>
     */
    String MATCH_NUM = "match_num";
}