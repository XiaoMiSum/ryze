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

package io.github.xiaomisum.ryze.result;

import io.github.xiaomisum.ryze.config.RyzeVariables;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 变量增量记录（一条即含 raw / value）
 * <p>
 * 每个元件（suite / sampler / processor）执行完成后，只记录本次产生或变更的变量：
 * {@code raw} 为定义时的原始形态（求值前的模板/定义值），{@code value} 为执行完成后的实际值。
 * </p>
 *
 * @author mi.xiao
 */
public class VariableRecord implements Serializable {

    /**
     * 变量名
     */
    private String name;

    /**
     * 原始值：可能是函数/其他变量引用/常量（求值前的模板/定义值）
     */
    private Object raw;

    /**
     * 本元件执行完后的实际值
     */
    private Object value;

    public VariableRecord() {
    }

    public VariableRecord(String name, Object raw, Object value) {
        this.name = name;
        this.raw = raw;
        this.value = value;
    }

    /**
     * 收集本元件执行后新增/覆盖的变量增量记录
     * <p>
     * 记录范围内为「本元件自身定义的变量」（{@code ownDefinitions}，定义形态 + 执行后值）
     * 以及「提取器等运行时写入、且不是本元件定义的新增变量」（{@code extraNames}，仅执行后值）。
     * 同名变量只记录一条。
     * </p>
     *
     * @param ownDefinitions 本元件自身定义的变量（合并父级/求值之前）
     * @param postView       执行完成后的变量视图
     * @param extraNames     除自身定义外，本元件执行过程中新出现的变量名（如提取器 refName）
     * @return 变量增量记录列表，无增量时返回空列表
     */
    public static List<VariableRecord> collect(RyzeVariables ownDefinitions, Map<String, Object> postView, Collection<String> extraNames) {
        List<VariableRecord> records = new ArrayList<>();
        Set<String> seen = new LinkedHashSet<>();
        if (ownDefinitions != null) {
            for (Map.Entry<String, Object> entry : ownDefinitions.entrySet()) {
                String name = entry.getKey();
                seen.add(name);
                records.add(new VariableRecord(name, entry.getValue(), postView == null ? null : postView.get(name)));
            }
        }
        if (extraNames != null && postView != null) {
            for (String name : extraNames) {
                if (name != null && !seen.contains(name) && postView.containsKey(name)) {
                    seen.add(name);
                    records.add(new VariableRecord(name, null, postView.get(name)));
                }
            }
        }
        return records;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Object getRaw() {
        return raw;
    }

    public void setRaw(Object raw) {
        this.raw = raw;
    }

    public Object getValue() {
        return value;
    }

    public void setValue(Object value) {
        this.value = value;
    }
}