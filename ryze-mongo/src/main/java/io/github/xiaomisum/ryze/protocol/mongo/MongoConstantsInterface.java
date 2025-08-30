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

package io.github.xiaomisum.ryze.protocol.mongo;

import io.github.xiaomisum.ryze.testelement.TestElementConstantsInterface;

/**
 * MongoDB 协议常量接口
 * <p>
 * 该接口定义了 MongoDB 协议相关的常量，包括配置项的键名、操作类型等。
 * 这些常量用于在 MongoDB 相关组件中保持配置项键名的一致性。
 * </p>
 * <p>
 * 主要功能：
 * <ul>
 *   <li>定义 MongoDB 配置项的标准键名</li>
 *   <li>定义 MongoDB 操作类型常量</li>
 *   <li>继承测试元件基础常量接口</li>
 * </ul>
 * </p>
 *
 * @author xiaomi
 */
public interface MongoConstantsInterface extends TestElementConstantsInterface {

    /**
     * 默认引用名称键值
     * <p>
     * 当未指定 MongoDB 配置引用名称时使用的默认值
     * </p>
     */
    String DEF_REF_NAME_KEY = "__mongo_configure_element_default_ref_name__";

    /**
     * 连接地址配置键名
     * <p>
     * 用于配置 MongoDB 连接地址的键名，值格式如：mongodb://root:123456@127.0.0.1:27017/admin
     * </p>
     */
    String URL = "url";

    /**
     * 数据库配置键名
     * <p>
     * 用于配置要操作的数据库名称
     * </p>
     */
    String DATABASE = "database";

    /**
     * 集合配置键名
     * <p>
     * 用于配置要操作的集合名称，相当于关系型数据库中的表
     * </p>
     */
    String COLLECTION = "collection";

    /**
     * 操作类型配置键名
     * <p>
     * 用于配置要执行的操作类型，如 find、delete、insert、update
     * </p>
     */
    String ACTION = "action";

    /**
     * 数据配置键名
     * <p>
     * 用于配置 insert、update 操作的数据内容
     * </p>
     */
    String DATA = "data";

    /**
     * 条件配置键名
     * <p>
     * 用于配置查询或操作的条件
     * </p>
     */
    String CONDITION = "condition";

    /**
     * 查询操作类型值
     * <p>
     * 表示执行查询(find)操作
     * </p>
     */
    String FIND = "find";

    /**
     * 删除操作类型值
     * <p>
     * 表示执行删除(delete)操作
     * </p>
     */
    String DELETE = "delete";

    /**
     * 插入操作类型值
     * <p>
     * 表示执行插入(insert)操作
     * </p>
     */
    String INSERT = "insert";

    /**
     * 更新操作类型值
     * <p>
     * 表示执行更新(update)操作
     * </p>
     */
    String UPDATE = "update";
}