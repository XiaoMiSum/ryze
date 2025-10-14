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

package io.github.xiaomisum.ryze.support.testng.dataprovider;

import org.testng.util.Strings;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * 序列解析器
 * <p>
 * 该类用于解析序列字符串，将其转换为整数列表。
 * 支持多种序列格式，包括范围、列表和混合格式。
 * 主要用于数据切片功能，确定需要执行的测试数据索引。
 * </p>
 *
 * @author xiaomi
 * Created at 2025/8/2 10:51
 */
public class SeqParser {

    // [1:3]
    private static final Pattern SEQ_RANGE = Pattern.compile("\\[(-?\\d+):(-?\\d+)]");
    // [1:]
    private static final Pattern SEQ_RANGE_LEFT = Pattern.compile("\\[(-?\\d+):]");
    // [:5]
    private static final Pattern SEQ_RANGE_RIGHT = Pattern.compile("\\[:(-?\\d+)]");
    // [1, 2, 3] [1, 3, -4, -1] [-1]
    private static final Pattern SEQ_LIST = Pattern.compile("\\[(-?\\d+)(,(-?\\d+))*]");
    private static final Pattern SEQ_LIST_EXTRACT = Pattern.compile(",?(-?\\d+)");


    /**
     * 解析序列字符串为序列列表。
     *
     * <p><br>合法的序列字符串：
     * <pre><code>
     *   [1:10]
     *   [1..]
     *   [..5]
     *   [1, 3, 5,7 ,9]
     *   [1, 3, -4, -1]
     * </code></pre>
     * </p>
     * <p>
     * 支持的格式：
     * <ul>
     *   <li>范围格式：[start:end]，如[1:10]表示从第1到第10个元素</li>
     *   <li>左边界格式：[start:]，如[3:]表示从第3个元素到末尾</li>
     *   <li>右边界格式：[:end]，如[:5]表示从开头到第5个元素</li>
     *   <li>列表格式：[index1,index2,...]，如[1,3,5]表示第1、3、5个元素</li>
     * </ul>
     * </p>
     * <p>
     * 索引从1开始计算，支持负数索引（-1表示最后一个元素）。
     * </p>
     *
     * @param seq  序列字符串
     * @param size 数据总大小，用于处理负数索引
     * @return 序列字符串对应的序列列表
     * @throws RuntimeException 当序列字符串格式不合法时抛出
     */
    public static List<Integer> parseSeq(String seq, int size) {
        // 默认返回所有
        if (Strings.isNullOrEmpty(seq)) {
            return size == 0 ? new ArrayList<>() : getSeqListByRange(1, size, size, "[1:-1]");
        }

        // 返回符合序列字符串的序列列表
        seq = seq.replaceAll(" ", "");
        Matcher matcher;
        //[1:10]
        if ((matcher = SEQ_RANGE.matcher(seq)).matches()) {
            // 提取上下边界值
            var left = Integer.parseInt(matcher.group(1));
            var right = Integer.parseInt(matcher.group(2));

            // 计算上下边界值
            left = left < 0 ? left + size + 1 : left;
            right = right < 0 ? right + size + 1 : right;

            return getSeqListByRange(left, right, size, seq);
        }
        // [1:] [-1:]
        if ((matcher = SEQ_RANGE_LEFT.matcher(seq)).matches()) {
            // 提取下边界
            var left = Integer.parseInt(matcher.group(1));

            // 计算上下边界值，若 上边界为负数 则以  left + size （从下边界往后数x位）
            left = left < 0 ? left + size : left;

            return getSeqListByRange(left, size, size, seq);
        }
        // [..5]
        if ((matcher = SEQ_RANGE_RIGHT.matcher(seq)).matches()) {
            // 提取上边界
            int right = Integer.parseInt(matcher.group(1));

            // 计算上下边界值
            int left = 1;
            right = right < 0 ? right + size : right;

            return getSeqListByRange(left, right, size, seq);
        }
        // [1, 3, -4, -1]
        if (SEQ_LIST.matcher(seq).matches()) {
            var seqList = getIntegers(seq, size);
            // 返回 去重、排序的序列
            return seqList.stream().distinct().sorted().collect(Collectors.toList());
        }
        throw new RuntimeException("非法的序列字符串：" + seq);
    }

    /**
     * 从序列字符串中提取整数列表
     * <p>
     * 处理列表格式的序列字符串，如[1,3,5]或[1,3,-4,-1]。
     * 负数索引会被转换为正数索引。
     * </p>
     *
     * @param seq  序列字符串
     * @param size 数据总大小
     * @return 整数列表
     */
    private static ArrayList<Integer> getIntegers(String seq, int size) {
        var seqList = new ArrayList<Integer>();
        // 提取列表值
        Matcher extractMatcher = SEQ_LIST_EXTRACT.matcher(seq);
        int matcher_start = 0;
        while (extractMatcher.find(matcher_start)) {
            seqList.add(Integer.parseInt(extractMatcher.group(1)));
            matcher_start = extractMatcher.end();
        }
        // 计算列表值
        int value;
        for (int i = 0; i < seqList.size(); i++) {
            value = seqList.get(i);
            if (value < 0) {
                seqList.set(i, value + size);
            }
        }
        return seqList;
    }

    /**
     * 根据范围获取序列列表
     * <p>
     * 处理范围格式的序列字符串，如[1:10]或[3:]或[:5]。
     * 会验证边界值的有效性并处理边界情况。
     * </p>
     *
     * @param left   左边界（包含）
     * @param right  右边界（包含）
     * @param size   数据总大小
     * @param rawSeq 原始序列字符串，用于错误信息
     * @return 序列列表
     * @throws RuntimeException 当范围非法时抛出
     */
    private static List<Integer> getSeqListByRange(int left, int right, int size, String rawSeq) {
        // 处理边界值 left < 1的情况
        left = Math.max(left, 1);
        // 处理边界值 right > size的情况
        right = Math.min(right, size);
        // 边界值验证
        if (left > right) {
            throw new RuntimeException(String.format("序列范围非法，左边界应不大于右边界，数据集大小：%s，当前区间：%s", size, rawSeq));
        }
        var seqList = new ArrayList<Integer>();
        // 计算序列列表
        for (int i = left; i <= right; i++) {
            seqList.add(i);
        }
        return seqList;
    }

    public static void main(String[] args) {
        System.out.println(parseSeq("[3:10]", 10));
        System.out.println(parseSeq("[2:-1]", 10));
        System.out.println(parseSeq("[-2:-1]", 10));
        System.out.println(parseSeq("[3:]", 10));
        System.out.println(parseSeq("[-3:]", 10));
        System.out.println(parseSeq("[:5]", 10));
        System.out.println(parseSeq("[:-5]", 10));
        System.out.println(parseSeq("[1,3,5,7,9]", 10));
        System.out.println(parseSeq("[1,3,5,-3]", 10));
    }
}