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

package io.github.xiaomisum.ryze.support.testng;

import io.github.xiaomisum.ryze.support.testng.annotation.AnnotationUtils;
import io.github.xiaomisum.ryze.support.testng.annotation.Datasource;
import io.github.xiaomisum.ryze.support.testng.dataprovider.SeqParser;
import org.testng.IDataProviderInterceptor;
import org.testng.IDataProviderMethod;
import org.testng.ITestContext;
import org.testng.ITestNGMethod;
import org.testng.util.Strings;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Objects;

/**
 * 数据过滤拦截器，用于根据数据源注解中的slice配置过滤测试数据
 * <p>
 * 该类实现了TestNG的 {@link IDataProviderInterceptor}接口，
 * 在数据提供者返回测试数据后、测试方法执行前对数据进行过滤处理。
 * 主要功能是根据 {@link Datasource}注解中的slice属性对测试数据进行切片，
 * 只执行指定范围或指定索引的测试数据。
 * </p>
 *
 * @author xiaomi
 */
public class RyzeDatasourceFilterInterceptor implements IDataProviderInterceptor {

    /**
     * 拦截并过滤测试数据提供者返回的数据
     * <p>
     * 该方法会检查测试方法上的数据源注解，如果存在slice配置，则根据配置对数据进行过滤，
     * 只保留指定索引的数据项用于测试执行。
     * </p>
     *
     * @param original           原始数据迭代器
     * @param dataProviderMethod 数据提供者方法
     * @param method             测试方法
     * @param iTestContext       测试上下文
     * @return 过滤后的数据迭代器
     */
    @Override
    public Iterator<Object[]> intercept(Iterator<Object[]> original, IDataProviderMethod dataProviderMethod,
                                        ITestNGMethod method, ITestContext iTestContext) {
        Datasource datasource = AnnotationUtils.getDatasource(method.getConstructorOrMethod().getMethod());
        if (Objects.isNull(datasource) || Strings.isNullOrEmpty(datasource.slice())) {
            return original;
        }
        // 获取所有数据
        var dataList = new ArrayList<Object[]>();
        while (original.hasNext()) {
            Object[] data = original.next();
            dataList.add(data);
        }
        // 过滤数据
        var result = new ArrayList<Object[]>();
        var seqList = SeqParser.parseSeq(datasource.slice(), dataList.size());
        seqList.forEach(index -> result.add(dataList.get(index - 1)));
        return result.iterator();
    }
}