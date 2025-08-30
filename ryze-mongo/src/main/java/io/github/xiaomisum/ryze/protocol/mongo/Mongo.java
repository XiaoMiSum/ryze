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

package io.github.xiaomisum.ryze.protocol.mongo;

import com.alibaba.fastjson2.JSON;
import com.mongodb.MongoClientSettings;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import io.github.xiaomisum.ryze.testelement.sampler.DefaultSampleResult;
import io.github.xiaomisum.ryze.protocol.mongo.config.MongoConfigItem;
import io.github.xiaomisum.ryze.support.Collections;
import org.bson.Document;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static io.github.xiaomisum.ryze.protocol.mongo.MongoConstantsInterface.*;

/**
 * Mongo 数据库操作执行器
 * <p>
 * 该类负责执行 MongoDB 数据库的各种操作，包括查询(find)、插入(insert)、更新(update)和删除(delete)。
 * 它封装了 MongoDB 客户端的创建、数据库连接、集合操作以及结果处理等核心逻辑。
 * </p>
 * <p>
 * 主要功能：
 * <ul>
 *   <li>根据配置创建 MongoDB 客户端连接</li>
 *   <li>执行不同类型的数据库操作（增删改查）</li>
 *   <li>处理操作结果并返回字节数据</li>
 *   <li>管理操作过程中的性能计时</li>
 * </ul>
 * </p>
 *
 * @author xiaomi
 */
@SuppressWarnings({"unchecked", "rawtypes"})
public class Mongo {

    /**
     * 执行 MongoDB 操作的主要方法
     * <p>
     * 该方法根据提供的配置项执行相应的 MongoDB 操作。它会创建 MongoDB 客户端连接，
     * 获取指定的数据库和集合，然后根据配置中的操作类型执行相应的数据库操作。
     * </p>
     *
     * @param settings MongoDB 客户端设置，包含连接信息
     * @param config   MongoDB 配置项，包含数据库名、集合名、操作类型等信息
     * @param result   采样结果对象，用于记录操作的开始和结束时间
     * @return 操作结果的字节数组表示
     */
    public static byte[] execute(MongoClientSettings settings, MongoConfigItem config, DefaultSampleResult result) {

        try (var client = MongoClients.create(settings)) {
            var database = client.getDatabase(config.getDatabase());
            var collection = database.getCollection(config.getCollection());
            var data = config.getData();
            result.sampleStart();
            return switch (config.getAction()) {
                case INSERT -> insert(collection, data);
                case UPDATE -> update(collection, config.getCondition(), data);
                case DELETE -> delete(collection, config.getCondition());
                case null, default -> find(collection, config.getCondition());
            };
        } finally {
            result.sampleEnd();
        }
    }

    /**
     * 执行 MongoDB 查询操作
     * <p>
     * 根据提供的条件在指定集合中查找文档。如果查找到多个结果，将以列表形式返回；
     * 如果只有一个结果，则返回单个对象；如果没有结果，则返回空的 HashMap。
     * </p>
     *
     * @param collection MongoDB 集合对象
     * @param condition  查询条件，以键值对形式表示
     * @return 查询结果的字节数组表示
     */
    private static byte[] find(MongoCollection<Document> collection, Map<String, Object> condition) {
        var result = collection.find(toDocument(condition));
        List<Object> response = new ArrayList<>();
        result.forEach(response::add);
        return JSON.toJSONBytes(response.isEmpty() ? Collections.newHashMap() : response.size() == 1 ? response.getFirst() : response);
    }

    /**
     * 执行 MongoDB 插入操作
     * <p>
     * 将提供的数据插入到指定集合中。支持插入单个文档或多个文档。
     * 插入完成后返回受影响的行数信息。
     * </p>
     *
     * @param collection MongoDB 集合对象
     * @param data       要插入的数据，可以是单个 Map 或 List
     * @return 插入操作结果信息的字节数组表示
     */
    private static byte[] insert(MongoCollection<Document> collection, Object data) {
        List<Document> documents = Objects.isNull(data) ? List.of()
                : data instanceof Map<?, ?> map ? List.of(toDocument((Map<String, Object>) map))
                : data instanceof List<?> items ? toDocuments(items) : List.of();
        collection.insertMany(documents);
        return ("Affected rows: " + documents.size()).getBytes(StandardCharsets.UTF_8);
    }

    /**
     * 执行 MongoDB 更新操作
     * <p>
     * 根据提供的条件更新指定集合中的文档。支持更新单个或多个文档。
     * 更新完成后返回受影响的行数信息。
     * </p>
     *
     * @param collection MongoDB 集合对象
     * @param condition  更新条件，用于定位要更新的文档
     * @param data       更新数据，包含要更新的字段和值
     * @return 更新操作结果信息的字节数组表示
     */
    private static byte[] update(MongoCollection<Document> collection, Map<String, Object> condition, Object data) {
        List<Document> documents = Objects.isNull(data) ? List.of()
                : data instanceof Map<?, ?> map ? List.of(toDocument((Map<String, Object>) map))
                : data instanceof List<?> items ? toDocuments(items) : List.of();
        var updateResult = collection.updateMany(toDocument(condition), documents);
        return ("Affected rows: " + updateResult.getModifiedCount()).getBytes(StandardCharsets.UTF_8);
    }

    /**
     * 执行 MongoDB 删除操作
     * <p>
     * 根据提供的条件删除指定集合中的文档。支持删除单个或多个文档。
     * 删除完成后返回受影响的行数信息。
     * </p>
     *
     * @param collection MongoDB 集合对象
     * @param condition  删除条件，用于定位要删除的文档
     * @return 删除操作结果信息的字节数组表示
     */
    private static byte[] delete(MongoCollection<Document> collection, Map<String, Object> condition) {
        var deleteResult = collection.deleteMany(toDocument(condition));
        return ("Affected rows: " + deleteResult.getDeletedCount()).getBytes(StandardCharsets.UTF_8);
    }

    /**
     * 将 List 转换为 Document 列表
     * <p>
     * 将包含 Map 对象的列表转换为 MongoDB Document 对象列表。
     * </p>
     *
     * @param sources 包含 Map 对象的列表
     * @return 转换后的 Document 对象列表
     */
    private static List<Document> toDocuments(List<?> sources) {
        var documents = new ArrayList<Document>();
        if (Objects.nonNull(sources)) {
            sources.forEach(item -> documents.add(toDocument((Map<String, Object>) item)));
        }
        return documents;
    }

    /**
     * 将 Map 转换为 Document
     * <p>
     * 将键值对映射转换为 MongoDB Document 对象。
     * </p>
     *
     * @param one 包含键值对的 Map 对象
     * @return 转换后的 Document 对象
     */
    private static Document toDocument(Map<String, Object> one) {
        var document = new Document();
        if (Objects.nonNull(one)) {
            one.forEach(document::append);
        }
        return document;
    }
}