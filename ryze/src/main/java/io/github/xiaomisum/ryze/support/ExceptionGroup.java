package io.github.xiaomisum.ryze.support;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 异常组，用于收集多个独立组件的异常
 * <p>
 * 语义：多个独立操作同时失败（如多个 extractor 各自失败），
 * 每个异常之间没有因果关系。与 addSuppressed 的区别是：
 * addSuppressed 表示"主异常 + 清理异常"的因果链，
 * ExceptionGroup 表示"多个平等异常"的集合。
 * </p>
 * <p>
 * 参考：Python PEP 654 ExceptionGroup、Reactor Exceptions.multiple()、
 * C# AggregateException、JS AggregateError
 * </p>
 *
 * @author xiaomi
 */
public class ExceptionGroup extends RuntimeException {

    private final List<Throwable> exceptions;

    /**
     * 构造异常组
     *
     * @param message    汇总消息
     * @param exceptions 组内的异常列表（会被拷贝为不可变列表）
     */
    public ExceptionGroup(String message, List<? extends Throwable> exceptions) {
        super(message, exceptions.isEmpty() ? null : exceptions.get(0));
        this.exceptions = Collections.unmodifiableList(new ArrayList<>(exceptions));
    }

    /**
     * 获取组内所有异常
     *
     * @return 不可变的异常列表
     */
    public List<Throwable> getExceptions() {
        return exceptions;
    }

    /**
     * 获取组内异常数量
     */
    public int size() {
        return exceptions.size();
    }

    @Override
    public String toString() {
        var sb = new StringBuilder(getClass().getSimpleName());
        sb.append(": ").append(getMessage());
        sb.append(" (").append(exceptions.size()).append(" sub-exceptions)");
        for (int i = 0; i < exceptions.size(); i++) {
            sb.append("\n  ").append(i + 1).append(". ").append(exceptions.get(i));
        }
        return sb.toString();
    }
}
