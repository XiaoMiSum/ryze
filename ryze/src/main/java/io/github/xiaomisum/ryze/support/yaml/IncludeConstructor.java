/*
 *
 *  * The MIT License (MIT)
 *  *
 *  * Copyright (c) 2025.  Lorem XiaoMiSum (mi_xiao@qq.com)
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

package io.github.xiaomisum.ryze.support.yaml;

import io.github.xiaomisum.ryze.support.dataloader.TestDataLoaderChain;
import org.yaml.snakeyaml.LoaderOptions;
import org.yaml.snakeyaml.constructor.AbstractConstruct;
import org.yaml.snakeyaml.constructor.Constructor;
import org.yaml.snakeyaml.nodes.Node;
import org.yaml.snakeyaml.nodes.ScalarNode;
import org.yaml.snakeyaml.nodes.SequenceNode;
import org.yaml.snakeyaml.nodes.Tag;

import java.io.File;
import java.nio.file.Paths;
import java.util.ArrayList;

/**
 * 支持YAML文件引入的构造器
 * <p>
 * 该构造器扩展了SnakeYAML的默认构造器，增加了对YAML文件包含功能的支持。
 * 允许在YAML文件中使用!include和!import标签引入其他YAML文件的内容。
 * </p>
 * <p>
 * 支持的格式：
 * <ul>
 *   <li>单文件引入：!include filepath 或 !import filepath</li>
 *   <li>多文件引入：!include [filepath1, filepath2] 或 !import [filepath1, filepath2]</li>
 * </ul>
 * </p>
 *
 * @author xiaomi
 */
public class IncludeConstructor extends Constructor {

    private final File basePath;

    /**
     * 默认构造函数
     * <p>
     * 创建一个不指定基础路径的包含构造器。
     * </p>
     */
    public IncludeConstructor() {
        this(null);
    }

    /**
     * 带基础路径的构造函数
     * <p>
     * 创建一个指定基础路径的包含构造器，用于解析相对路径的引入文件。
     * </p>
     *
     * @param basePath 基础路径，用于解析相对路径，可以为null
     */
    public IncludeConstructor(File basePath) {
        super(new LoaderOptions());
        this.basePath = basePath;
        this.yamlConstructors.put(new Tag("!include"), new IncludeConstruct());
        this.yamlConstructors.put(new Tag("!import"), new IncludeConstruct());
    }

    /**
     * 包含构造器内部类
     * <p>
     * 处理!include和!import标签的构造逻辑，支持单文件和多文件引入。
     * </p>
     */
    private class IncludeConstruct extends AbstractConstruct {
        /**
         * 构造节点内容
         * <p>
         * 根据节点类型处理文件引入：
         * 1. 标量节点：处理单文件引入
         * 2. 序列节点：处理多文件引入
         * </p>
         *
         * @param node YAML节点
         * @return 引入文件的内容或内容列表
         * @throws RuntimeException 当节点类型不支持或文件加载失败时抛出
         */
        @Override
        public Object construct(Node node) {
            if (node instanceof ScalarNode scalarNode) {
                String filename = constructScalar(scalarNode);
                String path = Paths.get(filename).isAbsolute() ? filename : basePath == null ? filename : new File(basePath, filename).getAbsolutePath();
                try {
                    return TestDataLoaderChain.loadTestData(path, Object.class);
                } catch (Exception e) {
                    throw new RuntimeException("加载 !include\\!import YAML文件错误: " + path, e);
                }
            }
            if (node instanceof SequenceNode sequenceNode) {
                var resp = new ArrayList<>();
                sequenceNode.getValue().forEach(item -> resp.add(construct(item)));
                return resp;
            }
            throw new RuntimeException("节点内容格式错误，请使用关键字: !include 或 !import 引入文件(file.yaml)\\文件组([file1.yaml, file2.yaml])");
        }
    }
}