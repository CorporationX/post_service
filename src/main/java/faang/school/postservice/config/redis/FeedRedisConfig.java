package faang.school.postservice.config.redis;

import faang.school.postservice.config.properties.FeedRedisProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceClientConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettucePoolingClientConfiguration;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.time.Duration;

@Configuration
@RequiredArgsConstructor
@Slf4j
public class FeedRedisConfig {

    private final FeedRedisProperties feedRedisProperties;

    @Bean
    public LettuceConnectionFactory lettuceConnectionFactory() {
        log.info("Creating LettuceConnectionFactory with standalone configuration and connection pool");
        var factory = new LettuceConnectionFactory(
                createRedisStandaloneConfiguration(),
                createLettuceClientConfiguration());
        log.info("LettuceConnectionFactory created for host={}, port={}",
                feedRedisProperties.getHost(), feedRedisProperties.getPort());
        return factory;
    }

    @Bean(name = "feedRedisTemplate")
    public RedisTemplate<String, Object> feedRedisTemplate(LettuceConnectionFactory connectionFactory) {
        log.info("Creating RedisTemplate using LettuceConnectionFactory");
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        var stringSerializer = new StringRedisSerializer();
        var jsonSerializer = new GenericJackson2JsonRedisSerializer();
        template.setConnectionFactory(connectionFactory);
        template.setKeySerializer(stringSerializer);
        template.setHashKeySerializer(stringSerializer);
        template.setValueSerializer(jsonSerializer);
        template.setHashValueSerializer(jsonSerializer);
        template.setEnableTransactionSupport(true);
        log.info("RedisTemplate configured successfully and ready to use");
        return template;
    }

    @Bean
    public StringRedisTemplate stringRedisTemplate(LettuceConnectionFactory lettuceConnectionFactory) {
        log.info("Creating StringRedisTemplate using LettuceConnectionFactory");
        StringRedisTemplate template = new StringRedisTemplate();
        template.setConnectionFactory(lettuceConnectionFactory);
        log.info("StringRedisTemplate configured successfully");
        return template;
    }

    private RedisStandaloneConfiguration createRedisStandaloneConfiguration() {
        String host = feedRedisProperties.getHost();
        Integer port = feedRedisProperties.getPort();
        log.info("Creating Redis connection factory for {}:{}", host, port);
        return new RedisStandaloneConfiguration(host, port);
    }

    private LettuceClientConfiguration createLettuceClientConfiguration() {
        Long timeout = feedRedisProperties.getTimeout();
        log.info("Creating LettuceClientConfiguration with timeout {} ms and connection pool", timeout);
        var config = LettucePoolingClientConfiguration.builder()
                .poolConfig(createLettucePoolConfig())
                .commandTimeout(Duration.ofMillis(timeout))
                .shutdownTimeout(Duration.ofMillis(timeout))
                .build();
        log.info("LettuceClientConfiguration created");
        return config;
    }

    private GenericObjectPoolConfig<?> createLettucePoolConfig() {
        FeedRedisProperties.LettucePool pool = feedRedisProperties.getLettucePool();
        log.info("Configuring GenericObjectPoolConfig: maxTotal={}, maxIdle={}, minIdle={}, maxWait={}ms",
                pool.getMaxTotal(), pool.getMaxIdle(), pool.getMinIdle(), pool.getMaxWait());
        var poolConfig = new GenericObjectPoolConfig<>();
        poolConfig.setMaxTotal(pool.getMaxTotal());
        poolConfig.setMaxIdle(pool.getMaxIdle());
        poolConfig.setMinIdle(pool.getMinIdle());
        poolConfig.setMaxWait(Duration.ofMillis(pool.getMaxWait()));
        return poolConfig;
    }
}
