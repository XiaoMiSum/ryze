package io.github.xiaomisum.ryze.support;

import io.github.xiaomisum.ryze.context.ContextWrapper;

public interface Computable<T> {

    /**
     * 计算并替换对象中的动态数据
     *
     * @param context 上下文对象
     * @return 计算后的对象（原地更新）
     */
    T evaluate(ContextWrapper context);
}
