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

package io.github.xiaomisum.ryze.extractor.builtin;

import com.alibaba.fastjson2.annotation.JSONField;
import io.github.xiaomisum.ryze.TestStatus;
import io.github.xiaomisum.ryze.extractor.AbstractExtractor;
import io.github.xiaomisum.ryze.extractor.ExtractResult;
import io.github.xiaomisum.ryze.testelement.KW;
import io.github.xiaomisum.ryze.testelement.sampler.SampleResult;
import org.apache.commons.lang3.StringUtils;

import java.util.regex.Pattern;

/**
 * 正则表达式提取器，用于从响应数据中通过正则表达式提取指定内容
 *
 * <p>该提取器使用Java正则表达式从响应体中提取数据。
 * 支持提取第一个匹配组的内容，并可以指定匹配序号来选择第N个匹配项。</p>
 *
 * <p>使用示例：
 * <pre>
 * 假设响应体为："姓名：张三，年龄：25岁"
 *
 * 提取姓名：
 * field = "姓名：(\\S+)"
 * refName = "name"
 *
 * 提取年龄：
 * field = "年龄：(\\d+)"
 * refName = "age"
 * </pre></p>
 *
 * <p>注意事项：
 * <ul>
 *   <li>正则表达式必须包含捕获组（用括号括起来的部分）才能提取数据</li>
 *   <li>默认提取第一个捕获组的内容</li>
 *   <li>可以通过matchNum指定要提取第几个匹配项</li>
 * </ul></p>
 *
 * @author xiaomi
 * @see Pattern
 * @see java.util.regex.Matcher
 */
@KW({"RegexExtractor", "regex_extractor", "regex"})
public class RegexExtractor extends AbstractExtractor {

    /**
     * 匹配序号，用于指定要提取第几个匹配项
     *
     * <p>当目标字符串中存在多个匹配项时，通过该字段指定要提取第几个匹配结果。
     * 序号从0开始计数，即0表示第一个匹配项，1表示第二个匹配项，以此类推。
     * 默认值为0，表示提取第一个匹配项。</p>
     */
    @JSONField(name = MATCH_NUM)
    protected int matchNum;

    /**
     * 创建一个新的正则表达式提取器构建器
     *
     * @return 正则表达式提取器构建器实例
     */
    public static Builder builder() {
        return new Builder();
    }

    /**
     * 执行正则表达式数据提取
     *
     * <p>提取流程包括：
     * <ol>
     *   <li>获取响应体的字符串表示</li>
     *   <li>编译正则表达式</li>
     *   <li>查找匹配项</li>
     *   <li>根据matchNum选择匹配项</li>
     *   <li>提取第一个捕获组的内容</li>
     *   <li>封装提取结果</li>
     *   <li>处理提取失败情况</li>
     * </ol></p>
     *
     * @param result 取样结果，包含待提取的响应数据
     * @return 提取结果对象
     */
    @Override
    protected ExtractResult extract(SampleResult result) {
        var res = new ExtractResult("正则表达式 提取: %s ，匹配第 %s 个".formatted(field, matchNum + 1));
        var target = result.getResponse().bytesAsString();
        var value = "";

        var pattern = Pattern.compile(field);
        var matcher = pattern.matcher(target);
        matchNum = Math.max(0, matchNum);
        int state = 0;
        while (state > -1 && matcher.find()) {
            state = matcher.groupCount() > 0 ? matchNum : state;
            value = matcher.group(1);
            state--;
        }

        if (StringUtils.isBlank(value)) {
            res.setStatus(TestStatus.failed);
            res.setMessage(String.format("目标字符串没有匹配的数据 %s，目标字符串：\n%s", field, target));
        }
        return res;
    }

    /**
     * 获取匹配序号
     *
     * @return 匹配序号，从0开始计数
     */
    public int getMatchNum() {
        return matchNum;
    }

    /**
     * 设置匹配序号
     *
     * @param matchNum 匹配序号，从0开始计数
     */
    public void setMatchNum(int matchNum) {
        this.matchNum = matchNum;
    }

    /**
     * 正则表达式提取器构建器，提供链式调用方式创建正则表达式提取器实例
     */
    public static class Builder extends AbstractExtractor.Builder<Builder, RegexExtractor> {

        /**
         * 构造一个新的正则表达式提取器构建器实例
         */
        public Builder() {
            super(new RegexExtractor());
        }

        /**
         * 设置匹配序号
         *
         * @param matchNum 匹配序号，从0开始计数
         * @return 构建器自身实例，用于链式调用
         */
        public Builder matchNum(int matchNum) {
            extractor.matchNum = matchNum;
            return self;
        }
    }
}