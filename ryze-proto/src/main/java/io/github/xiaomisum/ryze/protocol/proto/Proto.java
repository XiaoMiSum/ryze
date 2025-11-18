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

package io.github.xiaomisum.ryze.protocol.proto;

import com.alibaba.fastjson2.JSON;
import com.google.protobuf.DescriptorProtos;
import com.google.protobuf.Descriptors;
import com.google.protobuf.DynamicMessage;
import com.google.protobuf.InvalidProtocolBufferException;
import com.google.protobuf.util.JsonFormat;
import io.github.xiaomisum.ryze.protocol.proto.config.ProtoConfigureItem;
import io.github.xiaomisum.ryze.testelement.sampler.DefaultSampleResult;
import org.apache.commons.lang3.Strings;
import org.apache.hc.core5.http.message.BasicHeader;
import xyz.migoo.simplehttp.Form;
import xyz.migoo.simplehttp.Request;
import xyz.migoo.simplehttp.RequestEntity;
import xyz.migoo.simplehttp.Response;

import java.io.FileInputStream;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.regex.Pattern;

import static io.github.xiaomisum.ryze.protocol.proto.ProtoConstantsInterface.*;

/**
 * Proto工具类，提供Protocol Buffer消息的序列化和反序列化功能，
 * 以及与HTTP请求相关的操作方法集合
 *
 * @author xiaomi
 */
public class Proto {


    /**
     * 根据配置项构建HTTP请求对象
     *
     * @param config Proto协议配置项
     * @return 构建好的HTTP请求对象
     */
    public static Request build(ProtoConfigureItem config) {
        var url = "%s://%s%s%s".formatted(config.getProtocol(DEFAULT_PROTOCOL), config.getHost(), config.getFullPort(), config.getFullPath());
        var cli = Request.create(config.getMethod(), url);
        if (config.getQuery() != null && !config.getQuery().isEmpty()) {
            cli.query(Form.create((config.getQuery())));
        }
        if (config.getHeaders() != null && !config.getHeaders().isEmpty()) {
            config.getHeaders().forEach(cli::addHeader);
        }
        return cli;
    }

    /**
     * 执行HTTP请求并处理ProtoBuf消息
     *
     * @param config        proto 配置
     * @param descriptorMap proto 消息描述符集合
     * @param result        采样结果对象
     * @param request       proto真实请求数据
     * @return HTTP响应对象
     */
    public static RealProtoResponse execute(ProtoConfigureItem config, Map<String, Descriptors.FileDescriptor> descriptorMap, DefaultSampleResult result, RealProtoRequest request) {
        try {
            var requestMessageName = Proto.getMessageDescriptor(descriptorMap, config.getProtoDesc().getRequestMessageName());
            var responseMessageName = Proto.getMessageDescriptor(descriptorMap, config.getProtoDesc().getResponseMessageName());

            request.headers = config.getHeaders();
            var body = convert(request.body = config.getStringBody(), requestMessageName);
            var url = request.url = "%s://%s%s%s".formatted(config.getProtocol(), config.getHost(), config.getFullPort(), config.getPath());
            if (Strings.CI.equalsAny(config.getProtocol(), WS, WSS)) {
                request.query = config.getQuery() == null ? "" : JSON.toJSONString(config.getQuery());
                io.github.xiaomisum.simplewebsocket.Request ws = new io.github.xiaomisum.simplewebsocket.Request(url)
                        .headers(config.getHeaders())
                        .timeout(config.getTimeout() == null ? 0 : config.getTimeout())
                        .query(config.getQuery())
                        .bytes(body);
                Function<byte[], String> converter = bytes -> convert(bytes, responseMessageName);
                Function<String, Boolean> closeConnectHandler = x -> Pattern.compile(config.getResponsePattern()).matcher(x).find();
                io.github.xiaomisum.simplewebsocket.Response response = ws.execute(closeConnectHandler, converter);
                return new RealProtoResponse(response, responseMessageName);
            }
            var http = Request.create(config.getMethod(), url);
            if (config.getQuery() != null && !config.getQuery().isEmpty()) {
                http.query(Form.create((config.getQuery())));
            }
            if (config.getHeaders() != null && !config.getHeaders().isEmpty()) {
                config.getHeaders().forEach(http::addHeader);
            }
            request.method = config.getMethod();
            request.query = http.query();
            return new RealProtoResponse(execute(http, body, result), responseMessageName);
        } catch (Exception e) {
            if (e instanceof RuntimeException) {
                throw (RuntimeException) e;
            }
            throw new RuntimeException(e);
        }
    }

    /**
     * 执行HTTP请求
     *
     * @param cli    HTTP请求对象
     * @param json   请求体字节数组
     * @param result 采样结果对象
     * @return HTTP响应对象
     */
    private static Response execute(Request cli, byte[] json, DefaultSampleResult result) {
        result.sampleStart();
        try {
            if (json != null) {
                var contentType = cli.headers() == null || cli.headers().length == 0 ? APPLICATION_X_PROTOBUF :
                        Arrays.stream(cli.headers()).filter(x -> x.getName().equals(HEADER_CONTENT_TYPE)).findFirst().orElseGet(() -> new BasicHeader(HEADER_CONTENT_TYPE, APPLICATION_X_PROTOBUF)).getValue();
                cli.body(RequestEntity.bytes(json, contentType));
            }
            return cli.execute();
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            result.sampleEnd();
        }
    }

    /**
     * 读取 .desc 文件，返回所有文件的 Descriptor
     *
     * @param descPath 描述文件路径
     * @return 文件描述符映射
     */
    public static Map<String, Descriptors.FileDescriptor> loadFileDescriptors(String descPath) {
        try (var fis = new FileInputStream(descPath)) {
            var set = DescriptorProtos.FileDescriptorSet.parseFrom(fis);
            var fileDescMap = new HashMap<String, Descriptors.FileDescriptor>();
            for (var fdProto : set.getFileList()) {
                Descriptors.FileDescriptor[] deps = new Descriptors.FileDescriptor[fdProto.getDependencyCount()];
                for (int i = 0; i < fdProto.getDependencyCount(); i++) {
                    deps[i] = fileDescMap.get(fdProto.getDependency(i));
                }
                Descriptors.FileDescriptor fd = Descriptors.FileDescriptor.buildFrom(fdProto, deps);
                fileDescMap.put(fd.getName(), fd);
            }
            return fileDescMap;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 根据完整的消息类型名（如 "demo.User"）获取 Descriptor
     *
     * @param fileMap         文件描述符映射
     * @param fullMessageName 完整的消息类型名称
     * @return 消息描述符
     */
    public static Descriptors.Descriptor getMessageDescriptor(Map<String, Descriptors.FileDescriptor> fileMap, String fullMessageName) {
        for (var fd : fileMap.values()) {
            if (fd.findMessageTypeByName(fullMessageName.substring(fullMessageName.lastIndexOf('.') + 1)) != null) {
                return fd.findMessageTypeByName(fullMessageName.substring(fullMessageName.lastIndexOf('.') + 1));
            }
        }
        throw new IllegalArgumentException("Message type not found: " + fullMessageName);
    }

    /**
     * 将JSON字符串转换为Protocol Buffer二进制数据
     *
     * @param json 前端传来的 JSON 字符串
     * @param desc 目标消息的 Descriptor
     * @return protobuf 二进制（byte[]）
     * @throws InvalidProtocolBufferException 当Protocol Buffer解析失败时抛出
     */
    public static byte[] convert(String json, Descriptors.Descriptor desc) throws InvalidProtocolBufferException {
        if (json == null || json.isEmpty()) {
            return null;
        }
        // 1. 创建 DynamicMessage.Builder
        var builder = DynamicMessage.newBuilder(desc);
        // 2. 使用 JsonFormat 解析 JSON → Builder
        //   ignoringUnknownFields() 可以容忍前端多余字段
        JsonFormat.parser().ignoringUnknownFields().merge(json, builder);
        // 3. 构造 Message 并序列化为二进制
        return builder.build().toByteArray();


    }

    /**
     * 将Protocol Buffer二进制数据转换为JSON字符串
     *
     * @param binary 服务端返回的 protobuf 二进制
     * @param desc   目标消息的 Descriptor（必须与服务端使用的消息类型一致）
     * @return JSON 字符串
     */
    public static String convert(byte[] binary, Descriptors.Descriptor desc) {
        try {
            // 1. 检查输入数据
            if (binary == null || binary.length == 0) {
                return "{}";
            }
            // 2. 解析二进制为 DynamicMessage
            var msg = DynamicMessage.parseFrom(desc, binary);
            // 3. 使用 JsonFormat 打印为 JSON
            var printer = JsonFormat.printer().alwaysPrintFieldsWithNoPresence().preservingProtoFieldNames();
            return printer.print(msg);
        } catch (Exception e) {
            // 兜底解析，以防止接口返回的消息不是 protobuf 二进制数据
            return new String(binary);
        }
    }
}