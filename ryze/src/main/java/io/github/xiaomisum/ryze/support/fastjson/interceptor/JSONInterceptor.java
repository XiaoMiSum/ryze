package io.github.xiaomisum.ryze.support.fastjson.interceptor;

import io.github.xiaomisum.ryze.config.ConfigureItem;
import io.github.xiaomisum.ryze.testelement.TestElement;
import io.github.xiaomisum.ryze.testelement.processor.Postprocessor;
import io.github.xiaomisum.ryze.testelement.processor.Preprocessor;

import java.util.List;
import java.util.Map;

/**
 * JSON拦截器接口
 * <p>
 * 该接口定义了JSON反序列化过程中的拦截方法，允许在反序列化过程中对配置项进行自定义处理。
 * 主要用于处理配置项的版本兼容性问题，将旧版本的配置项转换为新版本格式。
 * </p>
 * <p>
 * 支持的拦截点：
 * <ul>
 *   <li>配置项反序列化{@link #deserializeConfigureItem(Object)}</li>
 *   <li>测试元素反序列化{@link #deserializeTestElement(Class, Map)}</li>
 *   <li>前置处理器反序列化{@link #deserializePreprocessor(Class, Object)}</li>
 *   <li>后置处理器反序列化<{@link #deserializePostprocessor(Class, Object)}/li>
 * </ul>
 * </p>
 *
 * @author xiaomi
 */
public interface JSONInterceptor {

    /**
     * 获取支持的类列表
     * <p>
     * 返回该拦截器支持处理的类列表，用于匹配需要拦截的类型。
     * </p>
     *
     * @return 支持的类列表
     */
    List<Class<?>> getSupportedClasses();


    /**
     * 反序列化测试元件的 config 属性，同时将使用当前已过期的配置项转化为最新配置
     * <p>
     * 如果不需要反序列化，则返回 null，调用方应当自行对 null 进行处理
     * </p>
     *
     * @param value 数据
     * @return 标准化数据，如果不需要处理则返回null
     */
    default ConfigureItem<?> deserializeConfigureItem(Object value) {
        return null;
    }

    /**
     * 反序列化测试元件的属性，同时将使用当前已过期的配置项转化为最新配置
     * <p>
     * 如果不需要反序列化，则返回 null，调用方应当自行对 null 进行处理
     * </p>
     *
     * @param clazz 反序列化目标类
     * @param value TestElement JSON 表示
     * @param <T>   测试元素类型参数
     * @return 标准化数据，如果不需要处理则返回null
     */
    default <T extends TestElement<?>> Map<String, Object> deserializeTestElement(Class<T> clazz, Map<String, Object> value) {
        return null;
    }

    /**
     * 反序列化前置处理器的属性，同时将使用当前已过期的配置项转化为最新配置
     * <p>
     * 如果不需要反序列化，则返回 null，调用方应当自行对 null 进行处理
     * </p>
     *
     * @param clazz 反序列化目标类
     * @param value 数据
     * @param <T>   前置处理器类型参数
     * @return 标准化数据，如果不需要处理则返回null
     */
    default <T extends Preprocessor> Map<String, Object> deserializePreprocessor(Class<T> clazz, Object value) {
        return null;
    }

    /**
     * 反序列化后置处理器的属性，同时将使用当前已过期的配置项转化为最新配置
     * <p>
     * 如果不需要反序列化，则返回 null，调用方应当自行对 null 进行处理
     * </p>
     *
     * @param clazz 反序列化目标类
     * @param value 数据
     * @param <T>   后置处理器类型参数
     * @return 标准化数据，如果不需要处理则返回null
     */
    default <T extends Postprocessor> Map<String, Object> deserializePostprocessor(Class<T> clazz, Object value) {
        return null;
    }
}