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

package io.github.xiaomisum.ryze.function.builtin;

import io.github.xiaomisum.ryze.context.ContextWrapper;
import io.github.xiaomisum.ryze.function.Args;
import io.github.xiaomisum.ryze.function.Function;
import org.apache.commons.lang3.StringUtils;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.temporal.ChronoField;
import java.util.Locale;

/**
 * 时间偏移函数实现类
 *
 * <p>该类用于对当前时间进行偏移操作，支持增加或减少天、小时、分钟等时间单位。
 * 常用于生成相对时间、测试时间相关功能等场景。</p>
 *
 * <p>在测试用例中可以通过 ${time_shift()} 的方式调用该函数。</p>
 *
 * @author mi.xiao
 */
public class TimeShift implements Function {

    private final ZoneId systemDefaultZoneId = ZoneId.systemDefault();

    @Override
    public String key() {
        return "time_shift";
    }

    /**
     * 对时间进行操作，支持两个参数，且两个参数都允许为空
     *
     * <p>参数说明：
     * <ol>
     *   <li>format: 指定日期时间格式，如果该参数未传递，则返回时间戳</li>
     *   <li>amount: 表示要从当前时间中添加或减去的时间量</li>
     * </ol>
     * </p>
     *
     * <p>amount参数格式遵循ISO-8601标准的Duration格式：
     * <ul>
     *   <li>PT20.345S: 解析为 20.345秒</li>
     *   <li>PT15M: 解析为 15分钟</li>
     *   <li>PT10H: 解析为 10小时</li>
     *   <li>P2D: 解析为 2天</li>
     *   <li>P2DT3H4M: 解析为 2天3小时4分钟</li>
     *   <li>P-6H3M: 解析为 -6小时+3分钟</li>
     *   <li>-P6H3M: 解析为 -6小时-3分钟</li>
     *   <li>-P-6H+3M: 解析为 +6小时-3分钟</li>
     * </ul>
     * </p>
     *
     * <p>当两个参数都未传递时，则返回当前时间戳</p>
     *
     * <p>使用示例：
     * <pre>
     * ${time_shift()}                          // 返回当前时间戳
     * ${time_shift("yyyy-MM-dd")}              // 返回当前日期，格式为yyyy-MM-dd
     * ${time_shift("yyyy-MM-dd HH:mm:ss", "P1D")}     // 返回明天此刻的时间，格式为yyyy-MM-dd HH:mm:ss
     * ${time_shift("", "-P7D")}                // 返回7天前的时间戳
     * </pre>
     * </p>
     *
     * @param context 上下文对象
     * @param args    参数列表，包含日期时间格式和时间偏移量
     * @return 格式化后的时间字符串或时间戳
     */
    public String execute(ContextWrapper context, Args args) {
        return execute(args.getString(0), args.getString(1));
    }

    /**
     * 执行时间偏移操作
     *
     * @param format 日期时间格式
     * @param amount 时间偏移量
     * @return 格式化后的时间字符串或时间戳
     */
    private String execute(String format, String amount) {
        var localDateTimeToShift = LocalDateTime.now(systemDefaultZoneId);
        DateTimeFormatter dateTimeFormatter = null;
        if (!StringUtils.isBlank(format)) {
            dateTimeFormatter = createFormatter(format, Locale.getDefault());
        }
        if (!StringUtils.isBlank(amount)) {
            localDateTimeToShift = localDateTimeToShift.plus(Duration.parse(amount));
        }
        if (dateTimeFormatter != null) {
            return localDateTimeToShift.format(dateTimeFormatter);
        }
        var offset = ZoneOffset.systemDefault().getRules().getOffset(localDateTimeToShift);
        return String.valueOf(localDateTimeToShift.toInstant(offset).toEpochMilli());
    }

    /**
     * 创建日期时间格式化器
     *
     * @param format 格式字符串
     * @param locale 区域设置
     * @return 日期时间格式化器
     */
    private DateTimeFormatter createFormatter(String format, Locale locale) {
        return new DateTimeFormatterBuilder().appendPattern(format)
                .parseDefaulting(ChronoField.NANO_OF_SECOND, 0)
                .parseDefaulting(ChronoField.MILLI_OF_SECOND, 0)
                .parseDefaulting(ChronoField.SECOND_OF_MINUTE, 0)
                .parseDefaulting(ChronoField.MINUTE_OF_HOUR, 0)
                .parseDefaulting(ChronoField.HOUR_OF_DAY, 0)
                .parseDefaulting(ChronoField.DAY_OF_MONTH, 1)
                .parseDefaulting(ChronoField.MONTH_OF_YEAR, 1)
                .parseDefaulting(ChronoField.YEAR_OF_ERA, Year.now().getValue())
                .toFormatter(locale);

    }
}