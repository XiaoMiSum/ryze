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

package io.github.xiaomisum.ryze.protocol.mongo.config;


import com.alibaba.fastjson2.annotation.JSONField;
import groovy.lang.Closure;
import io.github.xiaomisum.ryze.config.ConfigureItem;
import io.github.xiaomisum.ryze.context.ContextWrapper;
import io.github.xiaomisum.ryze.testelement.AbstractTestElement;
import io.github.xiaomisum.ryze.protocol.mongo.MongoConstantsInterface;
import io.github.xiaomisum.ryze.support.Collections;
import io.github.xiaomisum.ryze.support.Customizer;
import org.apache.commons.lang3.StringUtils;
import org.codehaus.groovy.runtime.DefaultGroovyMethods;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * MongoDB 配置项类
 * <p>
 * 该类用于封装 MongoDB 操作的配置信息，包括连接地址、数据库名、集合名、操作类型、数据和条件等。
 * 它实现了 ConfigureItem 接口，支持配置项的合并和上下文求值功能。
 * </p>
 * <p>
 * 主要功能：
 * <ul>
 *   <li>存储 MongoDB 操作的所有配置信息</li>
 *   <li>支持配置项的合并功能</li>
 *   <li>支持上下文求值功能</li>
 *   <li>提供 Builder 模式构建配置项</li>
 *   <li>支持多种操作类型的便捷方法（find、insert、update、delete）</li>
 * </ul>
 * </p>
 *
 * @author xiaomi
 * Created at 2025/7/19 20:17
 */
@SuppressWarnings({"unchecked", "rawtypes"})
public class MongoConfigItem implements ConfigureItem<MongoConfigItem>, MongoConstantsInterface {

    /**
     * 引用名称
     */
    @JSONField(name = REF)
    protected String ref;
    
    /**
     * 连接地址
     */
    @JSONField(name = URL, ordinal = 1)
    protected String url;
    
    /**
     * 数据库名称
     */
    @JSONField(name = DATABASE, ordinal = 2)
    protected String database;
    
    /**
     * 集合名称
     */
    @JSONField(name = COLLECTION, ordinal = 3)
    protected String collection;
    
    /**
     * 操作类型
     */
    @JSONField(name = ACTION, ordinal = 4)
    protected String action;
    
    /**
     * 操作数据
     */
    @JSONField(name = DATA, ordinal = 5)
    protected Object data;
    
    /**
     * 操作条件
     */
    @JSONField(name = CONDITION, ordinal = 6)
    protected Map<String, Object> condition;

    /**
     * 无参构造方法
     */
    public MongoConfigItem() {
    }

    /**
     * 创建 Builder 实例
     * <p>
     * 静态工厂方法，用于创建 MongoConfigItem.Builder 实例。
     * </p>
     *
     * @return MongoConfigItem.Builder 实例
     */
    public static Builder builder() {
        return new Builder();
    }

    /**
     * 合并配置项
     * <p>
     * 将当前配置项与另一个配置项合并，优先使用当前配置项的值。
     * 如果当前配置项的某个属性为空，则使用另一个配置项的对应属性值。
     * </p>
     *
     * @param other 另一个 MongoConfigItem 实例
     * @return 合并后的新 MongoConfigItem 实例
     */
    @Override
    public MongoConfigItem merge(MongoConfigItem other) {
        if (other == null) {
            return copy();
        }
        var localOther = other.copy();
        var self = copy();
        self.url = StringUtils.isBlank(self.url) ? localOther.url : self.url;
        self.database = StringUtils.isBlank(self.database) ? localOther.database : self.database;
        self.collection = StringUtils.isBlank(self.collection) ? localOther.collection : self.collection;
        self.action = StringUtils.isBlank(self.action) ? localOther.action : self.action;
        self.data = self.data == null ? localOther.data : self.data;
        self.condition = self.condition == null ? localOther.condition : self.condition;
        return self;
    }

    /**
     * 上下文求值
     * <p>
     * 对配置项中的所有属性进行上下文求值，替换其中的变量占位符。
     * </p>
     *
     * @param context 上下文包装器
     * @return 求值后的 MongoConfigItem 实例
     */
    @Override
    public MongoConfigItem evaluate(ContextWrapper context) {
        url = (String) context.evaluate(url);
        database = (String) context.evaluate(database);
        collection = (String) context.evaluate(collection);
        action = (String) context.evaluate(action);
        condition = (Map<String, Object>) context.evaluate(condition);
        data = context.evaluate(data);
        return this;
    }

    /**
     * 获取引用名称
     *
     * @return 引用名称
     */
    public String getRef() {
        return ref;
    }

    /**
     * 设置引用名称
     *
     * @param ref 引用名称
     */
    public void setRef(String ref) {
        this.ref = ref;
    }

    /**
     * 获取连接地址
     *
     * @return 连接地址
     */
    public String getUrl() {
        return url;
    }

    /**
     * 设置连接地址
     *
     * @param url 连接地址
     */
    public void setUrl(String url) {
        this.url = url;
    }

    /**
     * 获取数据库名称
     *
     * @return 数据库名称
     */
    public String getDatabase() {
        return database;
    }

    /**
     * 设置数据库名称
     *
     * @param database 数据库名称
     */
    public void setDatabase(String database) {
        this.database = database;
    }

    /**
     * 获取集合名称
     *
     * @return 集合名称
     */
    public String getCollection() {
        return collection;
    }

    /**
     * 设置集合名称
     *
     * @param collection 集合名称
     */
    public void setCollection(String collection) {
        this.collection = collection;
    }

    /**
     * 获取操作类型
     * <p>
     * 如果未设置操作类型，默认返回 FIND（查询）。
     * </p>
     *
     * @return 操作类型
     */
    public String getAction() {
        return StringUtils.isBlank(action) ? FIND : action;
    }

    /**
     * 设置操作类型
     *
     * @param action 操作类型
     */
    public void setAction(String action) {
        this.action = action;
    }

    /**
     * 获取操作数据
     *
     * @return 操作数据
     */
    public Object getData() {
        return data;
    }

    /**
     * 设置操作数据
     *
     * @param data 操作数据
     */
    public void setData(Object data) {
        this.data = data;
    }

    /**
     * 获取操作条件
     *
     * @return 操作条件
     */
    public Map<String, Object> getCondition() {
        return condition;
    }

    /**
     * 设置操作条件
     *
     * @param condition 操作条件
     */
    public void setCondition(Map<String, Object> condition) {
        this.condition = condition;
    }

    /**
     * MongoDB 配置项构建器类
     * <p>
     * 该类用于通过 Builder 模式构建 MongoConfigItem 实例，提供链式调用和多种配置方式。
     * </p>
     */
    public static class Builder extends AbstractTestElement.ConfigureBuilder<Builder, MongoConfigItem> {

        private final MongoConfigItem configure = new MongoConfigItem();

        /**
         * 无参构造方法
         */
        public Builder() {
        }

        /**
         * 设置引用名称
         *
         * @param ref 引用名称
         * @return 当前构建器实例，支持链式调用
         */
        public Builder ref(String ref) {
            configure.ref = ref;
            return self;
        }

        /**
         * 设置连接地址
         *
         * @param url 连接地址
         * @return 当前构建器实例，支持链式调用
         */
        public Builder url(String url) {
            configure.url = url;
            return self;
        }

        /**
         * 设置数据库名称
         *
         * @param database 数据库名称
         * @return 当前构建器实例，支持链式调用
         */
        public Builder database(String database) {
            configure.database = database;
            return self;
        }

        /**
         * 设置集合名称
         *
         * @param collection 集合名称
         * @return 当前构建器实例，支持链式调用
         */
        public Builder collection(String collection) {
            configure.collection = collection;
            return self;
        }

        /**
         * 设置操作类型
         *
         * @param action 操作类型
         * @return 当前构建器实例，支持链式调用
         */
        public Builder action(String action) {
            configure.action = action;
            return self;
        }

        /**
         * 设置操作类型为查询（find）
         *
         * @return 当前构建器实例，支持链式调用
         */
        public Builder find() {
            return action(FIND);
        }

        /**
         * 设置操作类型为查询（find）并指定条件
         *
         * @param condition 查询条件
         * @return 当前构建器实例，支持链式调用
         */
        public Builder find(Map<String, Object> condition) {
            return action(FIND).condition(condition);
        }

        /**
         * 设置操作类型为查询（find）并使用 Customizer 配置条件
         *
         * @param customizer 用于配置条件的 Customizer
         * @return 当前构建器实例，支持链式调用
         */
        public Builder find(Customizer<Map<String, Object>> customizer) {
            return action(FIND).condition(customizer);
        }

        /**
         * 设置操作类型为查询（find）并使用 Customizer 配置条件
         * <p>
         * 在 Groovy 中使用 select 代替 find，因为在 groovy 中有个默认方法名称 find，会与 find 方法冲突
         * </p>
         *
         * @param customizer 函数式表达式
         * @return 当前构建器实例，支持链式调用
         * @see DefaultGroovyMethods#find(Object, Closure)
         */
        public Builder select(Customizer<Map<String, Object>> customizer) {
            return action(FIND).condition(customizer);
        }


        /**
         * 设置操作类型为插入（insert）并指定数据
         *
         * @param data 插入数据
         * @return 当前构建器实例，支持链式调用
         */
        public Builder insert(Map<String, Object> data) {
            return action(INSERT).data(data);
        }

        /**
         * 设置操作类型为插入（insert）并使用 Customizer 配置数据
         *
         * @param customizer 用于配置数据的 Customizer
         * @return 当前构建器实例，支持链式调用
         */
        public Builder insertData(Customizer<Map<String, Object>> customizer) {
            return action(INSERT).dataMap(customizer);
        }

        /**
         * 设置操作类型为插入（insert）并指定数据列表
         *
         * @param data 数据列表
         * @return 当前构建器实例，支持链式调用
         */
        public Builder insert(List<Object> data) {
            return action(INSERT).data(data);
        }

        /**
         * 设置操作类型为插入（insert）并使用 Customizer 配置数据列表
         *
         * @param customizer 用于配置数据列表的 Customizer
         * @return 当前构建器实例，支持链式调用
         */
        public Builder insertList(Customizer<List<Object>> customizer) {
            return action(INSERT).dataList(customizer);
        }

        /**
         * 设置操作类型为更新（update）并指定数据和条件
         *
         * @param data      更新数据
         * @param condition 更新条件
         * @return 当前构建器实例，支持链式调用
         */
        public Builder update(Map<String, Object> data, Map<String, Object> condition) {
            return action(UPDATE).data(data).condition(condition);
        }

        /**
         * 设置操作类型为更新（update）并使用 Customizer 配置数据和条件
         *
         * @param data      用于配置更新数据的 Customizer
         * @param condition 用于配置更新条件的 Customizer
         * @return 当前构建器实例，支持链式调用
         */
        public Builder update(Customizer<Map<String, Object>> data, Customizer<Map<String, Object>> condition) {
            return action(UPDATE).dataMap(data).condition(condition);
        }

        /**
         * 设置操作类型为删除（delete）
         *
         * @return 当前构建器实例，支持链式调用
         */
        public Builder delete() {
            return action(DELETE);
        }

        /**
         * 设置操作类型为删除（delete）并指定条件
         *
         * @param condition 删除条件
         * @return 当前构建器实例，支持链式调用
         */
        public Builder delete(Map<String, Object> condition) {
            return action(DELETE).condition(condition);
        }

        /**
         * 设置操作类型为删除（delete）并使用 Customizer 配置条件
         *
         * @param customizer 用于配置删除条件的 Customizer
         * @return 当前构建器实例，支持链式调用
         */
        public Builder delete(Customizer<Map<String, Object>> customizer) {
            return action(DELETE).condition(customizer);
        }

        /**
         * 设置条件
         *
         * @param condition 条件
         * @return 当前构建器实例，支持链式调用
         */
        public Builder condition(Map<String, Object> condition) {
            configure.condition = Collections.putAllIfNonNull(configure.condition, condition);
            return self;
        }

        /**
         * 使用 Customizer 设置条件
         *
         * @param customizer 用于配置条件的 Customizer
         * @return 当前构建器实例，支持链式调用
         */
        public Builder condition(Customizer<Map<String, Object>> customizer) {
            Map<String, Object> condition = new HashMap<>();
            customizer.customize(condition);
            configure.condition = Collections.putAllIfNonNull(configure.condition, condition);
            return self;
        }

        /**
         * 设置数据
         *
         * @param data 数据
         * @return 当前构建器实例，支持链式调用
         */
        public Builder data(Map<String, Object> data) {
            if (configure.data != null && !(configure.data instanceof Map)) {
                return self;
            }
            configure.data = Collections.putAllIfNonNull((Map<String, Object>) configure.data, data);
            return self;
        }

        /**
         * 使用 Customizer 设置数据
         *
         * @param customizer 用于配置数据的 Customizer
         * @return 当前构建器实例，支持链式调用
         */
        public Builder dataMap(Customizer<Map<String, Object>> customizer) {
            if (configure.data != null && !(configure.data instanceof Map)) {
                return self;
            }
            Map<String, Object> data = new HashMap<>();
            customizer.customize(data);
            configure.data = Collections.putAllIfNonNull((Map<String, Object>) configure.data, data);
            return self;
        }

        /**
         * 设置数据列表
         *
         * @param data 数据列表
         * @return 当前构建器实例，支持链式调用
         */
        public Builder data(List<Object> data) {
            if (configure.data != null && !(configure.data instanceof Map)) {
                return self;
            }
            configure.data = Collections.addAllIfNonNull((List<Object>) configure.data, data);
            return self;
        }

        /**
         * 使用 Customizer 设置数据列表
         *
         * @param customizer 用于配置数据列表的 Customizer
         * @return 当前构建器实例，支持链式调用
         */
        public Builder dataList(Customizer<List<Object>> customizer) {
            if (configure.data != null && !(configure.data instanceof Map)) {
                return self;
            }
            List<Object> data = Collections.newArrayList();
            customizer.customize(data);
            configure.data = Collections.addAllIfNonNull((List<Object>) configure.data, data);
            return self;
        }


        /**
         * 构建 MongoConfigItem 实例
         *
         * @return MongoConfigItem 实例
         */
        @Override
        public MongoConfigItem build() {
            return configure;
        }
    }
}