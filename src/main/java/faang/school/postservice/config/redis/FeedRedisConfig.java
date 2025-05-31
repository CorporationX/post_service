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
        log.info("Configuring Redis Lettuce connection to: {}:{}",
                feedRedisProperties.getHost(), feedRedisProperties.getPort());
        var factory = new LettuceConnectionFactory(
                createRedisStandaloneConfiguration(),
                createLettuceClientConfiguration());
        log.debug("LettuceConnectionFactory for feed Redis created");
        return factory;
    }

    @Bean(name = "feedRedisTemplate")
    public RedisTemplate<String, Object> feedRedisTemplate(LettuceConnectionFactory connectionFactory) {
        log.debug("Creating RedisTemplate...");
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        var stringSerializer = new StringRedisSerializer();
        var jsonSerializer = new GenericJackson2JsonRedisSerializer();
        template.setConnectionFactory(connectionFactory);
        template.setKeySerializer(stringSerializer);
        template.setHashKeySerializer(stringSerializer);
        template.setValueSerializer(jsonSerializer);
        template.setHashValueSerializer(jsonSerializer);
        template.setEnableTransactionSupport(true);
        log.debug("RedisTemplate configured successfully");
        return template;
    }

    @Bean
    public StringRedisTemplate stringRedisTemplate(LettuceConnectionFactory lettuceConnectionFactory) {
        log.debug("Creating StringRedisTemplate...");
        StringRedisTemplate template = new StringRedisTemplate();
        template.setConnectionFactory(lettuceConnectionFactory);
        log.debug("StringRedisTemplate configured successfully");
        return template;
    }

    private RedisStandaloneConfiguration createRedisStandaloneConfiguration() {
        String host = feedRedisProperties.getHost();
        Integer port = feedRedisProperties.getPort();
        log.debug("Creating RedisStandaloneConfiguration for {}:{}", host, port);
        return new RedisStandaloneConfiguration(host, port);
    }

    private LettuceClientConfiguration createLettuceClientConfiguration() {
        Long timeout = feedRedisProperties.getTimeout();
        log.debug("Creating LettuceClientConfiguration with timeout {} ms and connection pool", timeout);
        var config = LettucePoolingClientConfiguration.builder()
                .poolConfig(createLettucePoolConfig())
                .commandTimeout(Duration.ofMillis(timeout))
                .shutdownTimeout(Duration.ofMillis(timeout))
                .build();
        log.debug("LettuceClientConfiguration created");
        return config;
    }

    private GenericObjectPoolConfig<?> createLettucePoolConfig() {
        FeedRedisProperties.LettucePool pool = feedRedisProperties.getLettucePool();
        log.debug("Configuring Lettuce Pool: maxTotal={}, maxIdle={}, minIdle={}, maxWait={}ms",
                pool.getMaxTotal(), pool.getMaxIdle(), pool.getMinIdle(), pool.getMaxWait());
        var poolConfig = new GenericObjectPoolConfig<>();
        poolConfig.setMaxTotal(pool.getMaxTotal());
        poolConfig.setMaxIdle(pool.getMaxIdle());
        poolConfig.setMinIdle(pool.getMinIdle());
        poolConfig.setMaxWait(Duration.ofMillis(pool.getMaxWait()));
        return poolConfig;
    }
}
