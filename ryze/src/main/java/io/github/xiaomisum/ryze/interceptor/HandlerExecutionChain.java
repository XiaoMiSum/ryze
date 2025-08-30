package io.github.xiaomisum.ryze.interceptor;

import io.github.xiaomisum.ryze.context.ContextWrapper;
import io.github.xiaomisum.ryze.testelement.TestElement;

import java.util.List;

/**
 * 处理器执行链，负责管理拦截器的执行顺序和生命周期
 * <p>
 * HandlerExecutionChain实现了拦截器的链式执行机制，确保拦截器按照预定义的顺序执行，
 * 并正确处理前置处理、后置处理和最终处理三个阶段。
 * </p>
 * <p>
 * 执行流程如下：
 * <ol>
 *   <li>前置处理(preHandle)：按顺序执行所有拦截器的preHandle方法，如果任一拦截器返回false，则中断执行并触发已完成拦截器的afterCompletion</li>
 *   <li>业务处理：执行目标处理器的核心业务逻辑</li>
 *   <li>后置处理(postHandle)：按逆序执行所有拦截器的postHandle方法</li>
 *   <li>最终处理(afterCompletion)：按逆序执行所有已执行过的拦截器的afterCompletion方法</li>
 * </ol>
 * </p>
 * <p>
 * 该类通过executedIndex字段跟踪已成功执行前置处理的拦截器索引，
 * 确保在异常情况下能够正确触发相应拦截器的最终处理方法。
 * </p>
 *
 * @param <T> TestElement的子类型，表示被拦截的测试元件类型
 * @author xiaomi
 */
@SuppressWarnings({"unchecked", "rawtypes"})
public class HandlerExecutionChain<T extends TestElement<?>> {
    /**
     * 拦截器列表，按顺序存储所有注册的拦截器
     */
    private final List<RyzeInterceptor> interceptors;
    
    /**
     * 已成功执行前置处理的拦截器索引，用于异常情况下的清理工作
     * -1表示尚未执行任何拦截器的前置处理
     */
    private int executedIndex = -1;

    /**
     * 构造一个处理器执行链实例
     *
     * @param interceptors 拦截器列表，按执行优先级排序
     */
    public HandlerExecutionChain(List<RyzeInterceptor> interceptors) {
        this.interceptors = interceptors;
    }

    /**
     * 应用前置处理逻辑，按顺序执行所有拦截器的preHandle方法
     * <p>
     * 前置处理阶段按拦截器列表的顺序依次执行每个拦截器的preHandle方法。
     * 如果某个拦截器的preHandle方法返回false，表示中断执行流程，
     * 此时会触发所有已完成前置处理的拦截器的afterCompletion方法，
     * 然后返回false。
     * </p>
     *
     * @param context 测试上下文，包含执行环境信息
     * @param runtime TestElement运行时数据
     * @return 如果所有拦截器都允许继续执行则返回true，否则返回false
     */
    public boolean applyPreHandle(ContextWrapper context, T runtime) {
        for (int i = 0; i < interceptors.size(); i++) {
            RyzeInterceptor interceptor = interceptors.get(i);
            if (!interceptor.preHandle(context, runtime)) {
                // 前置处理失败，触发已完成拦截器的afterCompletion
                triggerAfterCompletion(context, i - 1);
                return false;
            }
            executedIndex = i;
        }
        return true;
    }

    /**
     * 应用后置处理逻辑，按逆序执行所有拦截器的postHandle方法
     * <p>
     * 后置处理阶段在目标处理器的核心业务逻辑执行完成后进行，
     * 按拦截器列表的逆序依次执行每个拦截器的postHandle方法。
     * 这确保了与前置处理阶段相反的执行顺序，符合拦截器的对称性原则。
     * </p>
     *
     * @param context 测试上下文，包含执行环境信息
     * @param runtime TestElement运行时数据
     */
    public void applyPostHandle(ContextWrapper context, T runtime) {
        for (int i = interceptors.size() - 1; i >= 0; i--) {
            interceptors.get(i).postHandle(context, runtime);
        }
    }

    /**
     * 触发最终处理逻辑，按逆序执行所有已执行过前置处理的拦截器的afterCompletion方法
     * <p>
     * 最终处理阶段在请求处理完成后进行，无论处理成功还是失败都会执行。
     * 按照与前置处理相反的顺序执行已成功执行过前置处理的拦截器的afterCompletion方法，
     * 确保资源的正确释放和清理。
     * </p>
     *
     * @param context 测试上下文，包含执行环境信息
     */
    public void triggerAfterCompletion(ContextWrapper context) {
        for (int i = executedIndex; i >= 0; i--) {
            interceptors.get(i).afterCompletion(context);
        }
    }

    /**
     * 在前置处理失败时触发最终处理逻辑
     * <p>
     * 当某个拦截器的前置处理失败时，需要触发之前已成功执行过前置处理的拦截器的最终处理方法。
     * 此方法按逆序执行指定索引之前的所有拦截器的afterCompletion方法。
     * </p>
     *
     * @param context    测试上下文，包含执行环境信息
     * @param untilIndex 需要执行最终处理的最后一个拦截器索引
     */
    private void triggerAfterCompletion(ContextWrapper context, int untilIndex) {
        for (int i = untilIndex; i >= 0; i--) {
            interceptors.get(i).afterCompletion(context);
        }
    }
}