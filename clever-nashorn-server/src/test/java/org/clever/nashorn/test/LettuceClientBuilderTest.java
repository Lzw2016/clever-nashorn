package org.clever.nashorn.test;

import io.lettuce.core.resource.ClientResources;
import io.lettuce.core.resource.DefaultClientResources;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.junit.Test;
import org.springframework.boot.autoconfigure.data.redis.RedisProperties;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceClientConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettucePoolingClientConfiguration;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.time.Duration;

/**
 * 作者：lizw <br/>
 * 创建时间：2019/10/08 16:09 <br/>
 */
@Slf4j
public class LettuceClientBuilderTest {

    @Test
    public void t1() {
        RedisStandaloneConfiguration config = new RedisStandaloneConfiguration();
        config.setHostName("redis.msvc.top");
        config.setPort(6379);
        config.setPassword("lizhiwei1993");
        config.setDatabase(1);

        RedisProperties.Pool pool = new RedisProperties.Pool();
        LettuceClientConfiguration.LettuceClientConfigurationBuilder builder = LettucePoolingClientConfiguration.builder().poolConfig(getPoolConfig(pool));
        builder.commandTimeout(Duration.ofSeconds(10));
        RedisProperties.Lettuce lettuce = new RedisProperties.Lettuce();
        lettuce.setPool(pool);
        if (lettuce.getShutdownTimeout() != null && !lettuce.getShutdownTimeout().isZero()) {
            builder.shutdownTimeout(lettuce.getShutdownTimeout());
        }
        ClientResources clientResources = DefaultClientResources.create();
        builder.clientResources(clientResources);
        LettuceClientConfiguration clientConfiguration = builder.build();
        LettuceConnectionFactory lettuceConnectionFactory = new LettuceConnectionFactory(config, clientConfiguration);
        lettuceConnectionFactory.afterPropertiesSet();

        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(lettuceConnectionFactory);
        // 设置value的序列化规则和 key的序列化规则
        template.setKeySerializer(new StringRedisSerializer());
        // template.setValueSerializer(new GenericJackson2JsonRedisSerializer(objectMapper));
        Jackson2JsonRedisSerializer<Object> serializer = new Jackson2JsonRedisSerializer<>(Object.class);
        // serializer.setObjectMapper(objectMapper);
        template.setValueSerializer(serializer);
        template.afterPropertiesSet();


        template.boundValueOps("111111111").set("222222222222");

        lettuceConnectionFactory.destroy();
        clientResources.shutdown();
    }


    private GenericObjectPoolConfig<?> getPoolConfig(RedisProperties.Pool properties) {
        GenericObjectPoolConfig<?> config = new GenericObjectPoolConfig<>();
        config.setMaxTotal(properties.getMaxActive());
        config.setMaxIdle(properties.getMaxIdle());
        config.setMinIdle(properties.getMinIdle());
        if (properties.getTimeBetweenEvictionRuns() != null) {
            config.setTimeBetweenEvictionRunsMillis(properties.getTimeBetweenEvictionRuns().toMillis());
        }
        if (properties.getMaxWait() != null) {
            config.setMaxWaitMillis(properties.getMaxWait().toMillis());
        }
        return config;
    }
}
