/*
 *
 *  * The MIT License (MIT)
 *  *
 *  * Copyright (c) 2026.  Lorem XiaoMiSum (mi_xiao@qq.com)
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

package io.github.xiaomisum.ryze;

/**
 * Ryze测试框架主执行器
 * <p>
 * 提供一个main方法来执行所有测试场景，便于快速验证框架功能
 * </p>
 *
 * @author xiaomi
 */
public class RyzeTestRunner {

    /**
     * 主方法，执行所有测试场景
     * <p>
     * 当前实现执行以下测试场景：
     * <ul>
     *   <li>HTTP协议测试</li>
     *   <li>JDBC协议测试</li>
     *   <li>Redis协议测试</li>
     *   <li>Debug协议测试</li>
     *   <li>测试套件执行</li>
     * </ul>
     * </p>
     *
     * @param args 命令行参数
     */
    public static void main(String[] args) {
        System.out.println("=== Ryze测试框架所有测试场景执行器 ===");

        Ryze.start("debug-scenarios/scene1-full-nested.yaml");
        Ryze.start("debug-scenarios/scene2-module-nested.yaml");
        Ryze.start("debug-scenarios/scene3-interface-nested.yaml");
        Ryze.start("debug-scenarios/scene4-case-nested.yaml");
        Ryze.start("debug-scenarios/scene5-standalone.yaml");

        System.out.println("\n=== 所有测试场景执行完成 ===");
        System.out.println("如需运行特定测试，请查看文档或修改此主类");
    }
}