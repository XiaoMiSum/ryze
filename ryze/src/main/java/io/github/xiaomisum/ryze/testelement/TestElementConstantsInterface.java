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

package io.github.xiaomisum.ryze.testelement;

/**
 * 测试元素常量接口
 * <p>
 * 该接口定义了测试元素中使用的各种常量，主要用于JSON序列化/反序列化时的字段名称映射，
 * 以及在构建测试元素时的配置项标识。通过集中管理这些常量，确保在整个框架中字段名称的一致性。
 * </p>
 *
 * @author xiaomi
 */
public interface TestElementConstantsInterface {

    /**
     * 测试元素ID字段标识符
     * <p>用于唯一标识一个测试元素实例</p>
     */
    String ID = "id";

    /**
     * 测试元素标题字段标识符
     * <p>用于描述测试元素的标题或名称</p>
     */
    String TITLE = "title";

    /**
     * 测试元素元数据字段标识符
     * <p>用于存储测试元素的附加元数据信息</p>
     */
    String METADATA = "metadata";

    /**
     * 测试元素禁用状态字段标识符
     * <p>用于标识测试元素是否被禁用</p>
     */
    String DISABLED = "disabled";

    /**
     * 测试元素配置字段标识符
     * <p>用于存储测试元素的配置信息</p>
     */
    String CONFIG = "config";

    /**
     * 容器类测试元素子元素字段标识符
     * <p>用于存储容器类测试元素的子元素列表</p>
     */
    String CHILDREN = "children";

    /**
     * 单个子元素字段标识符
     * <p>用于引用单个子元素</p>
     */
    String CHILD = "child";

    /**
     * 验证器字段标识符
     * <p>用于存储验证器列表</p>
     */
    String VALIDATORS = "validators";

    /**
     * 提取器字段标识符
     * <p>用于存储提取器列表</p>
     */
    String EXTRACTORS = "extractors";

    /**
     * 前置处理器字段标识符
     * <p>用于存储前置处理器列表</p>
     */
    String PREPROCESSORS = "preprocessors";

    /**
     * 后置处理器字段标识符
     * <p>用于存储后置处理器列表</p>
     */
    String POSTPROCESSORS = "postprocessors";

    /**
     * 配置元素字段标识符
     * <p>用于存储配置元素列表</p>
     */
    String CONFIG_ELEMENTS = "configelements";

    /**
     * 变量字段标识符
     * <p>用于存储变量配置</p>
     */
    String VARIABLES = "variables";

    /**
     * 测试类字段标识符
     * <p>用于标识测试元素的实现类</p>
     */
    String TEST_CLASS = "testclass";

    /**
     * 拦截器字段标识符
     * <p>用于存储拦截器列表</p>
     */
    String INTERCEPTORS = "interceptors";

    /**
     * 引用字段标识符
     * <p>用于标识引用关系</p>
     */
    String REF = "ref";

}