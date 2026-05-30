package io.github.xiaomisum.ryze.support;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.util.DefaultInstantiatorStrategy;
import org.objenesis.strategy.StdInstantiatorStrategy;

public class KryoUtil {

    private static final ThreadLocal<Kryo> kryoThreadLocal = ThreadLocal.withInitial(() -> {
        Kryo kryo = new Kryo();
        kryo.setRegistrationRequired(false);
        kryo.setInstantiatorStrategy(new DefaultInstantiatorStrategy(new StdInstantiatorStrategy()));
        return kryo;
    });

    public static Kryo getKryo() {
        return kryoThreadLocal.get();
    }

    /**
     * 返回一个对象的深拷贝
     *
     * @param object 原对象
     * @param <T>    原对象类型
     * @return 原对象的深拷贝
     */
    public static <T> T copy(T object) {
        try {
            return kryoThreadLocal.get().copy(object);
        } finally {
            kryoThreadLocal.remove(); // 自动清理 ThreadLocal，防止内存泄漏
        }
    }

    /**
     * 清理当前线程的 Kryo 实例
     * <p>
     * 建议在测试方法执行完毕后显式调用，或在线程池环境中线程归还前调用，
     * 以防止 ThreadLocal 导致的内存泄漏。
     * </p>
     */
    public static void cleanup() {
        kryoThreadLocal.remove();
    }

}