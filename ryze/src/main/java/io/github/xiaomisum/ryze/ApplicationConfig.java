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

package io.github.xiaomisum.ryze;

import io.github.xiaomisum.ryze.assertion.Assertion;
import io.github.xiaomisum.ryze.extractor.Extractor;
import io.github.xiaomisum.ryze.function.Function;
import io.github.xiaomisum.ryze.interceptor.RyzeInterceptor;
import io.github.xiaomisum.ryze.interceptor.report.ReporterListener;
import io.github.xiaomisum.ryze.support.RyzeServiceLoader;
import io.github.xiaomisum.ryze.support.fastjson.interceptor.JSONInterceptor;
import io.github.xiaomisum.ryze.testelement.TestElement;
import io.github.xiaomisum.ryze.testelement.configure.ConfigureElement;
import io.github.xiaomisum.ryze.testelement.processor.Postprocessor;
import io.github.xiaomisum.ryze.testelement.processor.Preprocessor;
import org.hamcrest.Matcher;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.function.Supplier;

/**
 * 应用程序配置类，负责管理Ryze框架的核心配置和组件注册
 *
 * <p>该类是Ryze框架的核心配置管理器，通过SPI（Service Provider Interface）机制加载和管理各种组件，
 * 包括测试元件、配置元件、处理器、断言、提取器、函数、规则、拦截器和报告监听器等。</p>
 *
 * <p>主要功能包括：
 * <ul>
 *   <li>通过SPI机制动态加载框架组件</li>
 *   <li>提供各类组件的访问接口</li>
 *   <li>使用读写锁保证线程安全</li>
 *   <li>实现延迟加载和缓存机制</li>
 * </ul></p>
 *
 * <p>该类采用单例模式设计，所有方法均为静态方法，通过懒加载方式初始化各类组件映射表。</p>
 *
 * @author xiaomi
 */
@SuppressWarnings({"rawtypes"})
public class ApplicationConfig {


    // 关键字字典相关读写锁
    private static final ReadWriteLock TEST_ELEMENT_KEY_MAP_LOCK = new ReentrantReadWriteLock(false);
    private static final ReadWriteLock CONFIG_ELEMENT_KEY_MAP_LOCK = new ReentrantReadWriteLock(false);
    private static final ReadWriteLock PREPROCESSOR_KEY_MAP_LOCK = new ReentrantReadWriteLock(false);
    private static final ReadWriteLock POSTPROCESSOR_KEY_MAP_LOCK = new ReentrantReadWriteLock(false);
    private static final ReadWriteLock ASSERTION_KEY_MAP_LOCK = new ReentrantReadWriteLock(false);
    private static final ReadWriteLock EXTRACTOR_KEY_MAP_LOCK = new ReentrantReadWriteLock(false);
    private static final ReadWriteLock FUNCTIONS_LOCK = new ReentrantReadWriteLock(false);
    private static final ReadWriteLock MATCHER_KEY_MAP_LOCK = new ReentrantReadWriteLock(false);
    private static final ReadWriteLock JSON_INTERCEPTOR_KEY_MAP_LOCK = new ReentrantReadWriteLock(false);
    private static final ReadWriteLock REPORT_LISTENER_LOCK = new ReentrantReadWriteLock(false);
    private static final ReadWriteLock TEST_INTERCEPTOR_KEY_MAP_LOCK = new ReentrantReadWriteLock(false);

    /**
     * 测试元件类型映射表，键为元件标识符，值为对应的Class类型
     */
    private static Map<String, Class<? extends TestElement>> TEST_ELEMENT_KEY_MAP;

    /**
     * 配置元件类型映射表，键为元件标识符，值为对应的Class类型
     */
    private static Map<String, Class<? extends ConfigureElement>> CONFIG_ELEMENT_KEY_MAP;

    /**
     * 前置处理器类型映射表，键为处理器标识符，值为对应的Class类型
     */
    private static Map<String, Class<? extends Preprocessor>> PREPROCESSOR_KEY_MAP;

    /**
     * 后置处理器类型映射表，键为处理器标识符，值为对应的Class类型
     */
    private static Map<String, Class<? extends Postprocessor>> POSTPROCESSOR_KEY_MAP;

    /**
     * 断言类型映射表，键为断言标识符，值为对应的Class类型
     */
    private static Map<String, Class<? extends Assertion>> ASSERTION_KEY_MAP;

    /**
     * 提取器类型映射表，键为提取器标识符，值为对应的Class类型
     */
    private static Map<String, Class<? extends Extractor>> EXTRACTOR_KEY_MAP;

    /**
     * 函数列表，包含所有注册的函数实现
     */
    private static List<Function> FUNCTIONS;

    /**
     * 规则映射表，键为规则标识符，值为对应的Rule实例
     */
    private static Map<String, Class<? extends Matcher>> MATCHER_KEY_MAP;

    /**
     * 报告监听器列表，包含所有注册的报告监听器实现
     */
    private static List<? extends ReporterListener> REPORT_LISTENERS;

    /**
     * 测试拦截器类型映射表，键为拦截器标识符，值为对应的Class类型
     */
    private static Map<String, Class<? extends RyzeInterceptor>> TEST_INTERCEPTOR_KEY_MAP;

    /**
     * JSON拦截器映射表，键为支持的类类型，值为对应的JSONInterceptor实例
     */
    private static Map<Class<?>, JSONInterceptor> JSON_INTERCEPTOR_KEY_MAP;

    /**
     * 获取测试元件类型映射表
     * <p>通过SPI机制加载所有TestElement实现类，并以注解中的key作为键构建映射表。</p>
     *
     * @return 测试元件类型映射表
     */
    public static Map<String, Class<? extends TestElement>> getTestElementKeyMap() {
        return getDataMap(TEST_ELEMENT_KEY_MAP_LOCK,
                () -> ApplicationConfig.TEST_ELEMENT_KEY_MAP,
                () -> {
                    ApplicationConfig.TEST_ELEMENT_KEY_MAP = RyzeServiceLoader.loadAsMapBySPI(TestElement.class);
                    return ApplicationConfig.TEST_ELEMENT_KEY_MAP;
                }
        );
    }

    /**
     * 获取配置元件类型映射表
     * <p>通过SPI机制加载所有ConfigureElement实现类，并以注解中的key作为键构建映射表。</p>
     *
     * @return 配置元件类型映射表
     */
    public static Map<String, Class<? extends ConfigureElement>> getConfigureElementKeyMap() {
        return getDataMap(CONFIG_ELEMENT_KEY_MAP_LOCK,
                () -> ApplicationConfig.CONFIG_ELEMENT_KEY_MAP,
                () -> {
                    ApplicationConfig.CONFIG_ELEMENT_KEY_MAP = RyzeServiceLoader.loadAsMapBySPI(ConfigureElement.class);
                    return ApplicationConfig.CONFIG_ELEMENT_KEY_MAP;
                }
        );
    }

    /**
     * 获取前置处理器类型映射表
     * <p>通过SPI机制加载所有Preprocessor实现类，并以注解中的key作为键构建映射表。</p>
     *
     * @return 前置处理器类型映射表
     */
    public static Map<String, Class<? extends Preprocessor>> getPreprocessorKeyMap() {
        return getDataMap(PREPROCESSOR_KEY_MAP_LOCK,
                () -> ApplicationConfig.PREPROCESSOR_KEY_MAP,
                () -> {
                    ApplicationConfig.PREPROCESSOR_KEY_MAP = RyzeServiceLoader.loadAsMapBySPI(Preprocessor.class);
                    return ApplicationConfig.PREPROCESSOR_KEY_MAP;
                }
        );
    }

    /**
     * 获取后置处理器类型映射表
     * <p>通过SPI机制加载所有Postprocessor实现类，并以注解中的key作为键构建映射表。</p>
     *
     * @return 后置处理器类型映射表
     */
    public static Map<String, Class<? extends Postprocessor>> getPostprocessorKeyMap() {
        return getDataMap(POSTPROCESSOR_KEY_MAP_LOCK,
                () -> ApplicationConfig.POSTPROCESSOR_KEY_MAP,
                () -> {
                    ApplicationConfig.POSTPROCESSOR_KEY_MAP = RyzeServiceLoader.loadAsMapBySPI(Postprocessor.class);
                    return ApplicationConfig.POSTPROCESSOR_KEY_MAP;
                }
        );
    }

    /**
     * 获取断言类型映射表
     * <p>通过SPI机制加载所有Assertion实现类，并以注解中的key作为键构建映射表。</p>
     *
     * @return 断言类型映射表
     */
    public static Map<String, Class<? extends Assertion>> getAssertionKeyMap() {
        return getDataMap(ASSERTION_KEY_MAP_LOCK,
                () -> ApplicationConfig.ASSERTION_KEY_MAP,
                () -> {
                    ApplicationConfig.ASSERTION_KEY_MAP = RyzeServiceLoader.loadAsMapBySPI(Assertion.class);
                    return ApplicationConfig.ASSERTION_KEY_MAP;
                }
        );
    }

    /**
     * 获取提取器类型映射表
     * <p>通过SPI机制加载所有Extractor实现类，并以注解中的key作为键构建映射表。</p>
     *
     * @return 提取器类型映射表
     */
    public static Map<String, Class<? extends Extractor>> getExtractorKeyMap() {
        return getDataMap(EXTRACTOR_KEY_MAP_LOCK,
                () -> ApplicationConfig.EXTRACTOR_KEY_MAP,
                () -> {
                    ApplicationConfig.EXTRACTOR_KEY_MAP = RyzeServiceLoader.loadAsMapBySPI(Extractor.class);
                    return ApplicationConfig.EXTRACTOR_KEY_MAP;
                }
        );
    }

    /**
     * 获取函数列表
     * <p>通过SPI机制加载所有Function实现类的实例。</p>
     *
     * @return 函数列表
     */
    public static List<Function> getFunctions() {
        return getDataMap(FUNCTIONS_LOCK,
                () -> ApplicationConfig.FUNCTIONS,
                () -> {
                    ApplicationConfig.FUNCTIONS = RyzeServiceLoader.loadAsInstanceListBySPI(Function.class);
                    return ApplicationConfig.FUNCTIONS;
                }
        );
    }

    /**
     * 获取规则映射表
     * <p>通过SPI机制加载所有Rule实现类的实例，并以注解中的key作为键构建映射表。</p>
     *
     * @return 规则映射表
     */
    public static Map<String, Class<? extends Matcher>> getMatcherKeyMap() {
        return getDataMap(MATCHER_KEY_MAP_LOCK,
                () -> ApplicationConfig.MATCHER_KEY_MAP,
                () -> {
                    ApplicationConfig.MATCHER_KEY_MAP = RyzeServiceLoader.loadAsMapBySPI(Matcher.class);
                    return ApplicationConfig.MATCHER_KEY_MAP;
                }
        );
    }

    /**
     * 获取JSON拦截器映射表
     * <p>通过SPI机制加载所有JSONInterceptor实现类的实例，并根据其支持的类类型构建映射表。</p>
     *
     * @return JSON拦截器映射表
     */
    public static Map<Class<?>, JSONInterceptor> getJsonInterceptorKeyMap() {
        return getDataMap(JSON_INTERCEPTOR_KEY_MAP_LOCK,
                () -> ApplicationConfig.JSON_INTERCEPTOR_KEY_MAP,
                () -> {
                    var map = new HashMap<Class<?>, JSONInterceptor>();
                    RyzeServiceLoader.loadAsInstanceListBySPI(JSONInterceptor.class).forEach(item -> item.getSupportedClasses().forEach(clazz -> map.put(clazz, item)));
                    ApplicationConfig.JSON_INTERCEPTOR_KEY_MAP = map;
                    return ApplicationConfig.JSON_INTERCEPTOR_KEY_MAP;
                }
        );
    }

    /**
     * 获取测试拦截器类型映射表
     * <p>通过SPI机制加载所有RyzeInterceptor实现类，并以注解中的key作为键构建映射表。</p>
     *
     * @return 测试拦截器类型映射表
     */
    public static Map<String, Class<? extends RyzeInterceptor>> getTestInterceptorKeyMap() {
        return getDataMap(TEST_INTERCEPTOR_KEY_MAP_LOCK,
                () -> ApplicationConfig.TEST_INTERCEPTOR_KEY_MAP,
                () -> {
                    ApplicationConfig.TEST_INTERCEPTOR_KEY_MAP = RyzeServiceLoader.loadAsMapBySPI(RyzeInterceptor.class);
                    return ApplicationConfig.TEST_INTERCEPTOR_KEY_MAP;
                }
        );
    }

    /**
     * 获取报告监听器列表
     * <p>通过SPI机制加载所有ReporterListener实现类的实例。</p>
     *
     * @return 报告监听器列表
     */
    public static List<? extends ReporterListener> getReporterListeners() {
        return getDataMap(REPORT_LISTENER_LOCK,
                () -> ApplicationConfig.REPORT_LISTENERS,
                () -> {
                    ApplicationConfig.REPORT_LISTENERS = RyzeServiceLoader.loadAsInstanceListBySPI(ReporterListener.class);
                    return ApplicationConfig.REPORT_LISTENERS;
                }
        );
    }

    /**
     * 通过读写锁机制获取数据映射表
     * <p>该方法实现了双重检查锁定模式，确保线程安全的同时避免不必要的写锁开销。</p>
     *
     * <p>执行流程：
     * <ol>
     *   <li>获取读锁并尝试读取数据</li>
     *   <li>如果数据已存在则直接返回</li>
     *   <li>否则释放读锁，获取写锁</li>
     *   <li>再次检查数据是否存在（防止其他线程已初始化）</li>
     *   <li>如果仍不存在则通过writeWorker初始化数据</li>
     *   <li>释放写锁并返回数据</li>
     * </ol></p>
     *
     * @param readWriteLock 读写锁实例
     * @param readWorker    读取数据的Supplier
     * @param writeWorker   写入数据的Supplier
     * @param <T>           数据类型
     * @return 数据映射表
     */
    private static <T> T getDataMap(ReadWriteLock readWriteLock, Supplier<T> readWorker, Supplier<T> writeWorker) {
        var readLock = readWriteLock.readLock();
        var writeLock = readWriteLock.writeLock();
        readLock.lock();
        try {
            var clzMap = readWorker.get();
            if (Objects.nonNull(clzMap)) {
                return clzMap;
            }
        } finally {
            readLock.unlock();
        }
        writeLock.lock();
        try {
            var clzMap = readWorker.get();
            if (Objects.isNull(clzMap)) {
                clzMap = writeWorker.get();
            }
            return clzMap;
        } finally {
            writeLock.unlock();
        }
    }
}