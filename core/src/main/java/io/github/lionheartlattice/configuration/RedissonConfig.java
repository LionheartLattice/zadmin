package io.github.lionheartlattice.configuration;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import com.esotericsoftware.kryo.serializers.CompatibleFieldSerializer;
import io.github.lionheartlattice.entity.user_center.po.*;
import io.github.lionheartlattice.entity.user_center.vo.UserWithMenu;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.buffer.ByteBufInputStream;
import io.netty.buffer.ByteBufOutputStream;
import lombok.extern.slf4j.Slf4j;
import org.objenesis.strategy.SerializingInstantiatorStrategy;
import org.objenesis.strategy.StdInstantiatorStrategy;
import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.client.codec.BaseCodec;
import org.redisson.client.protocol.Decoder;
import org.redisson.client.protocol.Encoder;
import org.redisson.config.Config;
import org.redisson.config.SingleServerConfig;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

/**
 * Redisson 配置
 * 自定义 Kryo 序列化器,注册项目中的实体类
 */
@Slf4j
@Configuration
public class RedissonConfig {

    @Bean(destroyMethod = "shutdown")
    public RedissonClient redissonClient(@Value("${spring.data.redis.host:localhost}") String host,
                                         @Value("${spring.data.redis.port:6379}") int port,
                                         @Value("${spring.data.redis.database:0}") int database,
                                         @Value("${spring.data.redis.password:}") String password,
                                         @Value("${spring.data.redis.timeout:5s}") Duration timeout) {
        Config config = new Config();

        // 使用自定义的 Kryo 编解码器
        config.setCodec(new CustomKryoCodec());

        SingleServerConfig single = config.useSingleServer()
                                          .setAddress("redis://" + host + ":" + port)
                                          .setDatabase(database);

        if (StringUtils.hasText(password)) {
            single.setPassword(password);
        }

        int timeoutMs = Math.toIntExact(timeout.toMillis());
        single.setTimeout(timeoutMs);
        single.setConnectTimeout(timeoutMs);

        log.info("RedissonClient initialized with CustomKryoCodec. redis://{}:{}, db={}", host, port, database);
        return Redisson.create(config);
    }

    /**
     * 自定义 Kryo Codec,注册所有需要序列化的类
     */
    public static class CustomKryoCodec extends BaseCodec {

        private final ThreadLocal<Kryo> kryoThreadLocal = ThreadLocal.withInitial(() -> {
            Kryo kryo = new Kryo();

            // 设置为 false 避免循环引用问题
            kryo.setReferences(true);

            // 设置类注册行为(允许未注册的类)
            kryo.setRegistrationRequired(false);

            // 使用兼容的字段序列化器
            kryo.setDefaultSerializer(CompatibleFieldSerializer.class);

            // 设置实例化策略 - 修改这里
            kryo.setInstantiatorStrategy(new SerializingInstantiatorStrategy());

            // 注册 Java 基础类型
            kryo.register(BigDecimal.class);
            kryo.register(LocalDate.class);
            kryo.register(LocalDateTime.class);
            kryo.register(Date.class);

            // 注册集合类
            kryo.register(ArrayList.class);
            kryo.register(LinkedList.class);
            kryo.register(HashSet.class);
            kryo.register(HashMap.class);
            kryo.register(LinkedHashMap.class);

            // 注册项目实体类
            kryo.register(UserWithMenu.class);
            kryo.register(User.class);
            kryo.register(Role.class);
            kryo.register(Menu.class);
            kryo.register(Dept.class);
            kryo.register(Tenant.class);

            // 注册数组类型
            kryo.register(Object[].class);
            kryo.register(BigDecimal[].class);

            log.debug("Kryo instance initialized with custom class registrations");
            return kryo;
        });

        private final Decoder<Object> decoder = (buf, state) -> {
            Kryo kryo = kryoThreadLocal.get();
            try (Input input = new Input(new ByteBufInputStream(buf))) {
                return kryo.readClassAndObject(input);
            }
        };

        private final Encoder encoder = in -> {
            Kryo kryo = kryoThreadLocal.get();
            ByteBuf out = ByteBufAllocator.DEFAULT.buffer();
            try (Output output = new Output(new ByteBufOutputStream(out))) {
                kryo.writeClassAndObject(output, in);
                output.flush();
                return out;
            } catch (Exception e) {
                out.release();
                throw e;
            }
        };

        @Override
        public Decoder<Object> getValueDecoder() {
            return decoder;
        }

        @Override
        public Encoder getValueEncoder() {
            return encoder;
        }

        public void cleanup() {
            kryoThreadLocal.remove();
        }
    }
}
