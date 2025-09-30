package io.github.xiaomisum.ryze.support.groovy;

import groovy.lang.Closure;
import groovy.lang.DelegatesTo;

/**
 * Groovy 工具类，提供 Groovy 闭包和构建器的便捷操作方法
 * <p>
 * 该类主要用于简化 Groovy 代码在 Java 环境中的使用，提供闭包调用和对象构建的封装方法。
 * 主要功能包括：
 * 1. 闭包调用：设置委托对象并执行闭包
 * 2. 对象构建：通过闭包配置创建并返回对象实例
 * </p>
 *
 * @author xiaomi
 */
public class Groovy {

    /**
     * 调用指定的闭包，并设置委托对象
     * <p>
     * 该方法会克隆传入的闭包，设置其委托对象和解析策略，然后执行闭包。
     * 使用 Closure.DELEGATE_ONLY 策略确保闭包中的方法调用会直接委托给指定对象。
     * </p>
     *
     * @param delegate 闭包的委托对象，不能为 null
     * @param closure  要执行的闭包，不能为 null
     */
    public static void call(Object delegate, Closure<?> closure) {
        Closure<?> code = (Closure<?>) closure.clone();
        code.setDelegate(delegate);
        code.setResolveStrategy(Closure.DELEGATE_ONLY);
        code.call();
    }

    /**
     * 调用指定的闭包，并设置委托对象
     * <p>
     * 该方法会克隆传入的闭包，设置其委托对象和解析策略，然后执行闭包。
     * 使用 Closure.DELEGATE_ONLY 策略确保闭包中的方法调用会直接委托给指定对象。
     * </p>
     *
     * @param closure  要执行的闭包，不能为 null
     * @param delegate 闭包的委托对象，不能为 null
     */
    public static void call(Closure<?> closure, Object delegate) {
        Closure<?> code = (Closure<?>) closure.clone();
        code.setDelegate(delegate);
        code.setResolveStrategy(Closure.DELEGATE_ONLY);
        code.call();
    }

    /**
     * 使用闭包构建指定类型的对象实例
     * <p>
     * 该方法会创建指定类型的实例，然后通过闭包对其进行配置，最后返回配置好的实例。
     * 闭包中的方法调用会直接作用于新创建的对象实例上。
     * </p>
     *
     * @param type    要构建的对象类型，不能为 null
     * @param closure 用于配置对象的闭包，不能为 null
     * @param <T>     对象类型参数
     * @return 配置完成的对象实例
     * @throws RuntimeException 当对象实例化或闭包执行过程中发生异常时抛出
     */
    public static <T> T builder(Class<T> type, @DelegatesTo(strategy = Closure.DELEGATE_ONLY, type = "T") Closure<T> closure) {
        try {
            var builder = type.getConstructor().newInstance();
            call(builder, closure);
            return builder;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}