package io.github.xiaomisum.ryze.support.testng;

/**
 * TestNG支持模块常量接口
 * <p>
 * 该接口定义了TestNG支持模块中使用的各种常量，
 * 包括数据提供者名称和属性标识等。
 * </p>
 *
 * @author xiaomi
 */
public interface TestNGConstantsInterface {

    /**
     * 串行数据提供者名称
     * <p>
     * 用于标识串行执行的测试数据提供者方法。
     * </p>
     */
    String DATASOURCE_PROVIDER = "__ryze_testng_data_provider__";

    /**
     * 并行数据提供者名称
     * <p>
     * 用于标识并行执行的测试数据提供者方法。
     * </p>
     */
    String DATASOURCE_PROVIDER_PARALLEL = "__ryze_testng_data_provider_parallel__";

    /**
     * Ryze测试方法标识属性名
     * <p>
     * 用于在TestNG测试结果中标识该方法是否为Ryze测试方法。
     * </p>
     */
    String RYZE_TEST_METHOD = "__ryze_test_method_in_testng__";

    /**
     * Ryze测试方法标识属性名
     * <p>
     * 用于在TestNG测试结果中标识该方法是否为Ryze测试方法。
     * </p>
     */
    String RYZE_TEST_CLASS = "__ryze_test_class_in_testng__";
}