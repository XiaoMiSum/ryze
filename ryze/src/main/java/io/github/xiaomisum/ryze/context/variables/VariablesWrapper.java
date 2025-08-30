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

package io.github.xiaomisum.ryze.context.variables;

import java.util.Map;

/**
 * 变量包装类接口
 * <p>
 * 该接口定义了变量管理的基本操作，包括变量的获取、设置、删除以及跨上下文的变量合并功能。
 * 它为不同作用域的变量管理提供了统一的接口，支持在测试框架的不同层级（全局、测试套件、会话、测试）中管理变量。
 * </p>
 *
 * @author xiaomi
 */
public interface VariablesWrapper {

    /**
     * 根据变量名称读取变量值
     * <p>
     * 该方法用于获取指定名称的变量值。变量的查找遵循作用域规则，通常从当前作用域开始查找，
     * 如果未找到则可能向上级作用域查找（具体实现取决于实现类）。
     * </p>
     *
     * @param name 变量名称，不能为空
     * @return 变量值，如果变量不存在则返回null
     */
    Object get(String name);

    /**
     * 设置指定名称变量的值
     * <p>
     * 该方法用于设置指定名称的变量值。如果变量已存在，则会被新值替换；如果变量不存在，则会创建新的变量。
     * 变量的存储位置遵循实现类定义的作用域规则，通常存储在当前作用域中。
     * </p>
     *
     * @param name  变量名称，不能为空
     * @param value 变量值，可以为空
     * @return 变量之前的值，如果有，否则返回null
     */
    Object put(String name, Object value);

    /**
     * 移除指定变量
     * <p>
     * 该方法用于从当前作用域中移除指定名称的变量。如果变量存在，则将其移除并返回其值；如果变量不存在，则返回null。
     * </p>
     *
     * @param name 变量名称，不能为空
     * @return 变量之前的值，如果有，否则返回null
     */
    Object remove(String name);

    /**
     * 合并所有上下文的变量
     * <p>
     * 该方法用于合并来自不同作用域的变量，生成一个统一的变量视图。合并过程遵循变量作用域规则，
     * 通常后面的上下文变量会覆盖前面的同名变量，实现变量的继承和覆盖机制。
     * </p>
     * <p>
     * 合并后返回一个新的Map对象，对该对象的修改不会反映到原始变量存储中，确保了数据的安全性。
     * </p>
     *
     * @return 合并后的变量映射，键为变量名称，值为变量值
     */
    Map<String, Object> mergeVariables();

}