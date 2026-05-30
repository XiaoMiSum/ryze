/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2025.  Lorem XiaoMiSum (mi_xiao@qq.com)
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

package io.github.xiaomisum.ryze.template.freemarker;

import java.util.Set;

/**
 * FreeMarker 安全配置，定义白名单和黑名单规则。
 *
 * <p>
 * 该类用于防止模板注入攻击，通过表达式预检查和方法白名单机制，
 * 确保用户模板仅能访问安全的数据和操作。
 * </p>
 *
 * <p>
 * 安全策略包括：
 * <ul>
 * <li>禁止使用 ?new、?api 等危险内建函数</li>
 * <li>禁止访问 Runtime、ProcessBuilder 等系统类</li>
 * <li>禁止调用 getClass()、forName() 等反射方法</li>
 * <li>仅允许白名单中的安全方法调用</li>
 * </ul>
 * </p>
 *
 * @author xiaomi
 */
public final class RyzeSecurityConfiguration {

    private RyzeSecurityConfiguration() {
    }

    /**
     * 表达式中被禁止的危险模式
     */
    private static final Set<String> BLOCKED_PATTERNS = Set.of(
            "?new",
            "?api",
            ".getclass()",
            ".class",
            "freemarker.template.utility.execute",
            "freemarker.template.utility.objectconstructor",
            "freemarker.template.utility.jythonruntime",
            "runtime",
            "processbuilder",
            "exec(");

    /**
     * 允许调用的方法白名单
     */
    private static final Set<String> ALLOWED_METHODS = Set.of(
            // Collection/Map 方法
            "size", "length", "get", "put", "contains", "containsKey",
            "isEmpty", "toString", "hashCode", "equals",
            "keySet", "values", "entrySet", "toList",
            // String 方法
            "substring", "replace", "trim", "toLowerCase", "toUpperCase",
            "startsWith", "endsWith", "indexOf", "split", "matches",
            "charAt", "concat", "format",
            // Ryze 框架方法
            "getLastVariables", "getVariable", "setVariable");

    /**
     * 被禁止的方法名黑名单（优先级高于白名单）
     */
    private static final Set<String> BLOCKED_METHODS = Set.of(
            "getClass",
            "forName",
            "newInstance",
            "getMethod",
            "getDeclaredMethod",
            "invoke",
            "exec",
            "exit",
            "load",
            "loadLibrary",
            "getRuntime",
            "start",
            "getClassLoader",
            "defineClass");

    /**
     * 检查表达式是否包含危险模式
     *
     * @param expression 待检查的表达式
     * @return true 如果表达式安全，false 如果包含危险模式
     */
    public static boolean isExpressionSafe(String expression) {
        if (expression == null) {
            return true;
        }
        String lower = expression.toLowerCase();
        for (String pattern : BLOCKED_PATTERNS) {
            if (lower.contains(pattern)) {
                return false;
            }
        }
        return true;
    }

    /**
     * 检查方法名是否在白名单中
     *
     * @param methodName 方法名
     * @return true 如果方法允许调用
     */
    public static boolean isMethodAllowed(String methodName) {
        return ALLOWED_METHODS.contains(methodName);
    }

    /**
     * 检查方法名是否在黑名单中
     *
     * @param methodName 方法名
     * @return true 如果方法被禁止调用
     */
    public static boolean isMethodBlocked(String methodName) {
        return BLOCKED_METHODS.contains(methodName);
    }
}
