/*
 *
 *  * The MIT License (MIT)
 *  *
 *  * Copyright (c) 2025.  Lorem XiaoMiSum (mi_xiao@qq.com)
 *  *
 *  * Permission is hereby granted, free of charge, to any person obtaining
 *  * a copy of this software and associated documentation files (the
 *  * 'Software'), to deal in the Software without restriction, including
 *  * without limitation the rights to use, copy, modify, merge, publish,
 *  * distribute, sublicense, and/or sell copies of the Software, and to
 *  * permit persons to whom the Software is furnished to do so, subject to
 *  * the following conditions:
 *  *
 *  * The above copyright notice and this permission notice shall be
 *  * included in all copies or substantial portions of the Software.
 *  *
 *  * THE SOFTWARE IS PROVIDED 'AS IS', WITHOUT WARRANTY OF ANY KIND,
 *  * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
 *  * MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.
 *  * IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY
 *  * CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT,
 *  * TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE
 *  * SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 *
 *
 */

package io.github.xiaomisum.ryze.function;

import java.util.ArrayList;

/**
 * 函数参数列表类，扩展自ArrayList，提供便捷的参数访问方法
 *
 * <p>该类用于存储函数调用时传入的参数列表，提供了类型安全的参数获取方法，
 * 可以方便地获取指定位置的参数并转换为特定类型。</p>
 *
 * <p>与普通ArrayList不同的是，当访问超出范围的索引时不会抛出异常，
 * 而是返回默认值（null或对应类型的默认值）</p>
 *
 * <p>在Ryze框架中，当测试用例调用函数时，传入的参数会被封装成Args对象，
 * 函数实现可以通过该类提供的便捷方法获取和转换参数值。</p>
 *
 * @author xiaomi
 * Created at 2025/8/6 22:50
 */
public class Args extends ArrayList<Object> {

    /**
     * 获取指定索引位置的参数对象
     *
     * <p>如果索引超出范围，则返回null而不是抛出异常，这提高了函数实现的健壮性，
     * 避免因参数数量不匹配导致的运行时错误。</p>
     *
     * @param index 参数索引位置
     * @return 指定位置的参数对象，如果索引超出范围则返回null
     */
    @Override
    public Object get(int index) {
        return index >= size() ? null : super.get(index);
    }

    /**
     * 获取指定索引位置的参数并转换为字符串
     *
     * <p>如果指定位置的参数为null，则返回空字符串""</p>
     *
     * <p>该方法提供了一种安全的字符串参数获取方式，避免了手动进行null检查和类型转换。</p>
     *
     * @param index 参数索引位置
     * @return 指定位置参数的字符串表示，如果参数为null则返回空字符串
     */
    public String getString(int index) {
        return get(index) == null ? "" : super.get(index).toString();
    }

    /**
     * 获取指定索引位置的参数并转换为布尔值
     *
     * <p>如果参数为null则返回false，否则尝试解析为布尔值</p>
     *
     * <p>该方法使用Boolean.parseBoolean进行转换，该方法对"true"（忽略大小写）返回true，
     * 其他所有值都返回false，包括null。</p>
     *
     * @param index 参数索引位置
     * @return 指定位置参数的布尔值表示，如果参数为null则返回false
     */
    public boolean getBooleanValue(int index) {
        return get(index) != null && Boolean.parseBoolean(getString(index));
    }

    /**
     * 获取第一个参数并转换为字符串
     *
     * <p>等同于调用getString(0)</p>
     *
     * <p>该方法为常用的获取第一个参数的场景提供了便捷方法。</p>
     *
     * @return 第一个参数的字符串表示，如果不存在则返回空字符串
     */
    public String getFirstString() {
        return getString(0);
    }

    /**
     * 获取最后一个参数并转换为字符串
     *
     * <p>该方法为常用的获取最后一个参数的场景提供了便捷方法。</p>
     *
     * @return 最后一个参数的字符串表示，如果列表为空则返回空字符串
     */
    public String getLastString() {
        return getString(size() - 1);
    }

    /**
     * 获取指定索引位置的参数并转换为整数值
     *
     * <p>如果指定位置的参数为null，则返回0</p>
     *
     * <p>该方法使用Integer.parseInt进行转换，如果参数无法转换为整数将抛出NumberFormatException。</p>
     *
     * @param index 参数索引位置
     * @return 指定位置参数的整数值表示，如果参数为null则返回0
     * @throws NumberFormatException 如果参数无法转换为整数时抛出
     */
    public int getIntValue(int index) {
        return get(index) == null ? 0 : Integer.parseInt(getString(index));
    }
}