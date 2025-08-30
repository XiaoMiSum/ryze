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

package io.github.xiaomisum.ryze;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import io.github.xiaomisum.ryze.config.RyzeVariables;

import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;

import static io.github.xiaomisum.ryze.testelement.TestElementConstantsInterface.*;

/**
 * JSON测试用例树结构解析器
 * <p>
 * 该类负责将原始JSON测试用例转换为Ryze框架可识别的标准化格式，主要功能包括：
 * 1. 解析并标准化测试用例结构
 * 2. 处理测试用例中的变量定义
 * 3. 转换键名为小写以保持一致性
 * 4. 递归处理嵌套的测试组件
 * 5. 兼容旧版本的键名（如将CHILD替换为CHILDREN）
 * </p>
 *
 * @author xiaomi
 */
public class JsonTree extends JSONObject {

    /**
     * 构造函数，接收Map类型的测试用例数据
     *
     * @param testcase 测试用例数据，可以是JSONObject或其他Map类型
     */
    public JsonTree(Map<String, Object> testcase) {
        this(testcase instanceof JSONObject json ? json : new JSONObject(testcase));
    }


    /**
     * 构造函数，接收JSONObject类型的测试用例数据
     * <p>
     * 初始化流程：
     * 1. 替换过期的键名（如将CHILD替换为CHILDREN）
     * 2. 预处理测试用例数据，确保数据结构一致性
     * 3. 初始化并标准化测试用例结构
     * </p>
     *
     * @param testcase JSONObject类型的测试用例数据
     */
    public JsonTree(JSONObject testcase) {
        replaceExpiredKeys(testcase);
        var json = prepare(testcase);
        initialize(json, isRyzeTestFramework(json));
    }

    /**
     * 初始化并标准化测试用例结构
     * <p>
     * 处理流程：
     * 1. 如果是Ryze测试框架组件，先移除变量定义以减少遍历次数
     * 2. 遍历所有键值对：
     *    - 将Ryze组件的键名转换为小写
     *    - 递归处理嵌套的JSONObject和JSONArray对象
     * 3. 根据组件类型设置测试类标识
     * 4. 重新添加变量定义到Ryze组件中
     * </p>
     *
     * @param json    预处理后的测试用例数据
     * @param isRyze  是否为Ryze测试框架组件
     */
    private void initialize(JSONObject json, boolean isRyze) {
        // 1、先删除 Ryze组件中的变量，减少一次遍历
        var variables = isRyze ? json.remove(VARIABLES) : null;
        json.forEach((key, value) -> {
            // 将 ryze 测试组件 的key 转换为小写
            var newKey = isRyze ? key.toLowerCase(Locale.ROOT) : key;
            switch (value) {
                case JSONObject object -> put(newKey, parse(object));
                case JSONArray objects -> {
                    for (int i = 0; i < objects.size(); i++) {
                        var item = objects.get(i);
                        // 这里主要是为了处理 配置的子组件，组件配置一定是Map
                        objects.set(i, item instanceof Map ? parse(objects.getJSONObject(i)) : item);
                    }
                    put(newKey, objects);
                }
                case null, default -> put(newKey, value);
            }
        });
        if (isRyzeTestsuite(json)) {
            put(TEST_CLASS, "__testsuite__");
        }
        if (isRyzeSampler(json)) {
            put(TEST_CLASS, json.getString(TEST_CLASS).toLowerCase(Locale.ROOT));
        }
        if (isRyze) {
            // 3、重新添加变量到 Ryze 组件
            put(VARIABLES, variables instanceof RyzeVariables ? variables :
                    new RyzeVariables(Optional.ofNullable((JSONObject) variables).orElse(new JSONObject())));
        }
    }

    /**
     * 解析JSONObject对象，如果是Ryze测试框架组件则递归创建JsonTree实例
     *
     * @param json 待解析的JSONObject对象
     * @return 解析后的对象，如果是Ryze组件则返回新的JsonTree实例，否则返回原对象
     */
    private JSONObject parse(JSONObject json) {
        return isRyzeTestFramework(json) ? new JsonTree(json) : json;
    }

    /**
     * 预处理Map对象，确保数据结构一致性
     *
     * @param object 待处理的Map对象
     * @return 处理后的JSONObject对象
     */
    private JSONObject prepare(Map<?, ?> object) {
        var json = new JSONObject(object.size());
        object.keySet().forEach(key -> {
            var keyString = (String) key;
            var value = object.get(key);
            switch (value) {
                case Map<?, ?> map -> json.put(keyString, prepare(map));
                case List<?> objects -> json.put(keyString, prepare(objects));
                case null, default -> json.put(keyString, value);
            }
        });
        return json;
    }

    /**
     * 预处理List对象，确保数据结构一致性
     *
     * @param items 待处理的List对象
     * @return 处理后的JSONArray对象
     */
    private JSONArray prepare(List<?> items) {
        var temp = new JSONArray(items.size());
        items.forEach(item -> {
            switch (item) {
                case List<?> objects -> temp.add(prepare(objects));
                case Map<?, ?> map -> temp.add(prepare(map));
                case null, default -> temp.add(item);
            }
        });
        return temp;
    }

    /**
     * 判断是否为Ryze测试框架组件
     *
     * @param json 待判断的JSONObject对象
     * @return 如果是Ryze测试框架组件返回true，否则返回false
     */
    private boolean isRyzeTestFramework(JSONObject json) {
        return isRyzeTestsuite(json) || isRyzeSampler(json);
    }

    /**
     * 判断是否为Ryze测试套件组件
     *
     * @param json 待判断的JSONObject对象
     * @return 如果是Ryze测试套件组件返回true，否则返回false
     */
    private boolean isRyzeTestsuite(JSONObject json) {
        return ((json.containsKey(CHILDREN) || json.containsKey(CHILD)) && json.containsKey(TITLE));
    }

    /**
     * 判断是否为Ryze取样器组件
     *
     * @param json 待判断的JSONObject对象
     * @return 如果是Ryze取样器组件返回true，否则返回false
     */
    private boolean isRyzeSampler(JSONObject json) {
        return json.containsKey(TEST_CLASS);
    }

    /**
     * 替换过期的键名，保持向后兼容性
     * <p>
     * 目前主要处理将旧版本的CHILD键名替换为CHILDREN
     * </p>
     *
     * @param json 需要处理的JSONObject对象
     */
    private void replaceExpiredKeys(JSONObject json) {
        if (!isRyzeTestFramework(json)) {
            return;
        }
        if (json.containsKey(CHILD)) {
            json.put(CHILDREN, json.remove(CHILD));
        }
    }
}