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

package io.github.xiaomisum.ryze.context.variables;

import com.alibaba.fastjson2.JSON;
import io.github.xiaomisum.ryze.config.RyzeVariables;
import io.github.xiaomisum.ryze.context.Context;
import io.github.xiaomisum.ryze.context.TestRunContext;
import io.github.xiaomisum.ryze.testelement.TestElementConfigureGroup;
import io.github.xiaomisum.ryze.testelement.TestElementConstantsInterface;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 变量包装类基础实现
 * <p>
 * 该类提供了变量管理的核心功能，包括变量的获取、设置、删除以及跨上下文的变量合并。
 * 它维护了一个上下文链，允许在不同层级的上下文之间管理变量，支持变量的继承和覆盖机制。
 * </p>
 * <p>
 * 变量管理遵循以下规则：
 * <ul>
 * <li>变量存储在每个上下文的配置组中</li>
 * <li>当获取变量时，优先从最后一个上下文中获取</li>
 * <li>合并变量时，会按照上下文链的顺序合并，后加入的上下文变量会覆盖前面的同名变量</li>
 * </ul>
 * </p>
 *
 * @author xiaomi
 */
public abstract class AbstractVariablesWrapper implements VariablesWrapper, TestElementConstantsInterface {

    /**
     * 上下文链，表示变量作用域的层次结构
     * <p>
     * 上下文链表示了变量的层级关系，通常包括全局上下文、测试套件上下文、会话上下文和测试上下文等。
     * 链中的每个上下文都可以包含自己的变量配置，后面的上下文变量会覆盖前面的同名变量。
     * </p>
     */
    protected List<Context> contextChain;

    /**
     * 最后一个上下文，通常是当前正在执行的测试、会话或测试套件的上下文
     * <p>
     * 这个上下文用于存储当前作用域的变量。当设置或获取变量时，通常操作的是这个上下文中的变量配置。
     * </p>
     */
    protected Context lastContext;

    // -------- 扁平化缓存层 --------

    /**
     * 合并变量的快照缓存，避免每次调用 mergeVariables() 时重复遍历上下文链
     */
    private volatile Map<String, Object> flattenedCache;

    /**
     * 缓存版本号，与 modificationCount 比较判断缓存是否有效
     */
    private volatile long cacheVersion = 0;

    /**
     * 修改计数器，每次 put/remove 操作时递增，用于缓存失效判断
     */
    private volatile long modificationCount = 0;

    /**
     * 记录自上次缓存构建以来变更的 key 集合
     */
    private volatile Set<String> dirtyKeys = ConcurrentHashMap.newKeySet();

    /**
     * 增量更新阈值：变更 key 数量超过此值时执行全量重建
     */
    private static final int INCREMENTAL_THRESHOLD = 20;

    /**
     * 构造一个新的变量包装器实例
     *
     * @param contextChain 上下文链，不能为空
     */
    public AbstractVariablesWrapper(List<Context> contextChain) {
        this.contextChain = contextChain;
        this.lastContext = contextChain.getLast();
    }

    /**
     * 根据变量名称读取变量值
     * <p>
     * 该方法会从最后一个上下文的变量配置中获取指定名称的变量值。如果最后一个上下文没有变量配置，
     * 或者变量配置中不存在指定名称的变量，则返回null。
     * </p>
     *
     * @param name 变量名称，不能为空
     * @return 变量值，如果变量不存在则返回null
     * @see #getLastVariables()
     */
    public Object get(String name) {
        if (hasLastVariables()) {
            return getLastVariables().get(name);
        }
        return null;
    }

    /**
     * 设置指定名称变量的值
     * <p>
     * 该方法会将指定名称和值的变量存储到最后一个上下文的变量配置中。如果该变量已存在，则会被新值替换，
     * 并返回旧值；如果不存在，则会创建新的变量，并返回null。
     * </p>
     *
     * @param name  变量名称，不能为空
     * @param value 变量值，可以为空
     * @return 变量之前的值，如果有，否则返回null
     * @see #getLastVariables()
     */
    public Object put(String name, Object value) {
        modificationCount++;
        dirtyKeys.add(name);
        return getLastVariables().put(name, value);
    }

    /**
     * 移除指定变量
     * <p>
     * 该方法会从最后一个上下文的变量配置中移除指定名称的变量，并返回被移除的变量值。如果变量不存在，则返回null。
     * </p>
     *
     * @param name 变量名称，不能为空
     * @return 变量之前的值，如果有，否则返回null
     * @see #getLastVariables()
     */
    public Object remove(String name) {
        if (hasLastVariables()) {
            modificationCount++;
            dirtyKeys.add(name);
            return getLastVariables().remove(name);
        }
        return null;
    }

    /**
     * 合并所有上下文的变量
     * <p>
     * 该方法会按照上下文链的顺序，依次合并所有上下文中的变量配置。后加入上下文的变量会覆盖前面上下文的同名变量，
     * 实现了变量的继承和覆盖机制。
     * </p>
     * <p>
     * 内部使用缓存机制优化性能：当变量未发生修改时，直接返回缓存的快照。
     * </p>
     *
     * @return 合并后的变量映射（不可变视图），键为变量名称，值为变量值
     */
    @Override
    public Map<String, Object> mergeVariables() {
        long currentMod = this.modificationCount;

        // 缓存命中：版本号未变
        if (flattenedCache != null && cacheVersion == currentMod) {
            return Collections.unmodifiableMap(flattenedCache);
        }

        // 判断：增量更新还是全量重建
        if (flattenedCache != null && !dirtyKeys.isEmpty() && dirtyKeys.size() <= INCREMENTAL_THRESHOLD) {
            // 增量更新：基于现有缓存，只更新变化的 key
            var snapshot = new HashMap<>(flattenedCache);
            for (String dirtyKey : dirtyKeys) {
                // 从上下文链中按优先级查找该 key 的最新值
                Object latestValue = resolveKeyFromChain(dirtyKey);
                if (latestValue != null) {
                    snapshot.put(dirtyKey, latestValue);
                } else {
                    snapshot.remove(dirtyKey);
                }
            }
            this.flattenedCache = snapshot;
            this.cacheVersion = currentMod;
            this.dirtyKeys = ConcurrentHashMap.newKeySet();
            return Collections.unmodifiableMap(snapshot);
        }

        // 全量重建
        var snapshot = new HashMap<String, Object>();
        for (var context : contextChain) {
            var configureGroup = context.getConfigGroup();
            if (configureGroup != null) {
                var ctxVars = configureGroup.getVariables();
                if (ctxVars != null) {
                    snapshot.putAll(ctxVars);
                }
            }
        }
        this.flattenedCache = snapshot;
        this.cacheVersion = currentMod;
        this.dirtyKeys = ConcurrentHashMap.newKeySet();
        return Collections.unmodifiableMap(snapshot);
    }

    /**
     * 从上下文链按优先级解析指定 key 的值（后面覆盖前面）
     *
     * @param key 变量名称
     * @return 变量的最新值，如果所有上下文中都不存在则返回 null
     */
    private Object resolveKeyFromChain(String key) {
        Object result = null;
        for (var context : contextChain) {
            var configureGroup = context.getConfigGroup();
            if (configureGroup != null) {
                var ctxVars = configureGroup.getVariables();
                if (ctxVars != null && ctxVars.containsKey(key)) {
                    result = ctxVars.get(key);
                }
            }
        }
        return result;
    }

    /**
     * 主动失效缓存
     * <p>
     * 当上下文链发生变更（如添加/移除 Context）时调用此方法使缓存失效。
     * </p>
     */
    protected void invalidateCache() {
        this.modificationCount++;
        this.dirtyKeys = ConcurrentHashMap.newKeySet();
    }

    /**
     * 检查最后一个上下文是否包含变量配置
     *
     * @return 如果最后一个上下文包含变量配置则返回true，否则返回false
     */
    private boolean hasLastVariables() {
        if (lastContext.getConfigGroup() == null) {
            return false;
        }
        return lastContext.getConfigGroup().getVariables() != null;
    }

    /**
     * 获取最后一个上下文的变量配置
     * <p>
     * 如果最后一个上下文没有配置组，则会创建一个新的配置组；如果配置组中没有变量配置，
     * 则会创建一个新的变量配置并添加到配置组中。这样确保总是能获取到一个有效的变量配置对象。
     * </p>
     *
     * @return 最后一个上下文的变量配置对象
     */
    public RyzeVariables getLastVariables() {
        var lastConfigureGroup = lastContext.getConfigGroup();
        if (Objects.isNull(lastConfigureGroup)) {
            lastConfigureGroup = new TestElementConfigureGroup();
            ((TestRunContext) lastContext).setConfigGroup(lastConfigureGroup);
        }
        var lastContextVariables = lastConfigureGroup.getVariables();
        if (!Objects.isNull(lastContextVariables)) {
            return lastContextVariables;
        }
        lastContextVariables = new RyzeVariables();
        lastConfigureGroup.put(VARIABLES, lastContextVariables);
        return lastContextVariables;
    }

    @Override
    public String toString() {
        return JSON.toJSONString(this);
    }

}