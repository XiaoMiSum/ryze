package io.github.xiaomisum.ryze.template;

/**
 * 表达式中使用的变量名常量
 */
public enum Vkw {

    /**
     * 上下文包装器 ContextWrapper 对象
     *
     * <p><pre>
     *   String uuid = ctx.evalAsString("${uuid()}")
     * </pre>
     */
    ctx,
    context,
    vars
}
