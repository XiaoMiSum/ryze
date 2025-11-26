package io.github.xiaomisum.ryze.assertion.builtin;

import io.github.xiaomisum.ryze.assertion.AbstractAssertion;
import io.github.xiaomisum.ryze.testelement.KW;
import io.github.xiaomisum.ryze.testelement.sampler.SampleResult;

/**
 * 状态断言类，用于验证整个响应状态码
 *
 * <p>该类继承自AbstractAssertion，用于对响应状态码进行验证。
 * 它将响应的响应状态吗作为实际值与期望值进行比较。</p>
 *
 * <p>使用示例：
 * <pre>
 * {
 *   "testclass": "status"
 *   "expected": "200",
 *   "rule": "=="
 * }
 * </pre>
 * </p>
 *
 * @author xiaomi
 * @see AbstractAssertion 抽象断言类
 */
@KW({"status_assertion", "status", "status_code"})
public class StatusAssertion extends AbstractAssertion {

    @Override
    protected Object extractActualValue(SampleResult result) {
        return result.getResponse().status();
    }
}
