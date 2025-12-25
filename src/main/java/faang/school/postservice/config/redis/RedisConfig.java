package faang.school.postservice.config.redis;

import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.postservice.dto.post.PostV2Dto;
import io.lettuce.core.ClientOptions;
import io.lettuce.core.SocketOptions;
import io.lettuce.core.TimeoutOptions;
import lombok.RequiredArgsConstructor;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettucePoolingClientConfiguration;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.time.Duration;

@Configuration
@RequiredArgsConstructor
public class RedisConfig {
    private final RedisProperties redisProperties;

    @Bean(value = "forRedisTemplate")
    @Primary
    public LettuceConnectionFactory redisConnectionFactory() {
        RedisStandaloneConfiguration config = new RedisStandaloneConfiguration(redisProperties.getHost(), redisProperties.getPort());
        config.setDatabase(0);

        return baseConfigRedis(config);
    }

    @Bean(value = "forRedisTemplatePost")
    public LettuceConnectionFactory redisConnectionFactoryPost() {
        RedisStandaloneConfiguration config = new RedisStandaloneConfiguration(redisProperties.getHost(), redisProperties.getPort());
        config.setDatabase(1);

        return baseConfigRedis(config);
    }

    @Bean(value = "forRedisTemplateAuthorPost")
    public LettuceConnectionFactory redisConnectionFactoryAuthorPost() {
        RedisStandaloneConfiguration config = new RedisStandaloneConfiguration(redisProperties.getHost(), redisProperties.getPort());
        config.setDatabase(2);

        return baseConfigRedis(config);
    }

    @Bean(value = "forRedisTemplateFeed")
    public LettuceConnectionFactory redisConnectionFactoryFeed() {
        RedisStandaloneConfiguration config = new RedisStandaloneConfiguration(redisProperties.getHost(), redisProperties.getPort());
        config.setDatabase(3);

        return baseConfigRedis(config);
    }

    @Bean(name = "redisTemplateFeed")
    public RedisTemplate<String, Object> redisTemplateFeed(@Qualifier("forRedisTemplateFeed") RedisConnectionFactory connectionFactory) {
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(connectionFactory);

        StringRedisSerializer stringRedisSerializer = new StringRedisSerializer();
        Jackson2JsonRedisSerializer<Object> jsonSerializer = new Jackson2JsonRedisSerializer<>(Object.class);

        template.setKeySerializer(stringRedisSerializer);
        template.setValueSerializer(jsonSerializer);

        template.setHashKeySerializer(stringRedisSerializer);
        template.setHashValueSerializer(jsonSerializer);

        template.setEnableTransactionSupport(false);
        template.setExposeConnection(false);

        return template;
    }

    @Bean(name = "redisTemplatePost")
    public RedisTemplate<String, PostV2Dto> redisTemplate(@Qualifier("forRedisTemplate") RedisConnectionFactory connectionFactory,
                                                              ObjectMapper objectMapper) {
        RedisTemplate<String, PostV2Dto> template = new RedisTemplate<>();
        template.setConnectionFactory(connectionFactory);

        StringRedisSerializer stringRedisSerializer = new StringRedisSerializer();
        Jackson2JsonRedisSerializer<PostV2Dto> jsonSerializer = new Jackson2JsonRedisSerializer<>(objectMapper, PostV2Dto.class);

        template.setKeySerializer(stringRedisSerializer);
        template.setValueSerializer(jsonSerializer);

        template.setHashKeySerializer(stringRedisSerializer);
        template.setHashValueSerializer(stringRedisSerializer);

        template.setEnableTransactionSupport(false);
        template.setExposeConnection(false);

        return template;
    }

    @Bean(name = "redisTemplatePost")
    public RedisTemplate<String, PostV2Dto> redisTemplatePost(@Qualifier("forRedisTemplatePost") RedisConnectionFactory connectionFactory,
                                                              ObjectMapper objectMapper) {
        RedisTemplate<String, PostV2Dto> template = new RedisTemplate<>();
        template.setConnectionFactory(connectionFactory);

        StringRedisSerializer stringRedisSerializer = new StringRedisSerializer();
        Jackson2JsonRedisSerializer<PostV2Dto> jsonSerializer = new Jackson2JsonRedisSerializer<>(objectMapper, PostV2Dto.class);

        template.setKeySerializer(stringRedisSerializer);
        template.setValueSerializer(jsonSerializer);

        template.setHashKeySerializer(stringRedisSerializer);
        template.setHashValueSerializer(stringRedisSerializer);

        template.setEnableTransactionSupport(false);
        template.setExposeConnection(false);

        return template;
    }

    @Bean(name = "redisTemplateAuthorPost")
    public RedisTemplate<String, Object> redisTemplateAuthorPosts(@Qualifier("forRedisTemplateAuthorPost") RedisConnectionFactory connectionFactory) {
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(connectionFactory);

        StringRedisSerializer stringRedisSerializer = new StringRedisSerializer();
        Jackson2JsonRedisSerializer<Object> jsonSerializer = new Jackson2JsonRedisSerializer<>(Object.class);


        template.setKeySerializer(stringRedisSerializer);
        template.setValueSerializer(jsonSerializer);

        template.setHashKeySerializer(stringRedisSerializer);
        template.setHashValueSerializer(stringRedisSerializer);

        template.setEnableTransactionSupport(false);
        template.setExposeConnection(false);

        return template;
    }

    private LettuceConnectionFactory baseConfigRedis(RedisStandaloneConfiguration config){
        SocketOptions socketOptions = SocketOptions.builder()
                .connectTimeout(Duration.ofMillis(redisProperties.getConnectTimeout()))
                .keepAlive(true)
                .tcpNoDelay(true)
                .build();

        TimeoutOptions timeoutOptions = TimeoutOptions.builder()
                .fixedTimeout(Duration.ofMillis(redisProperties.getTimeout()))
                .build();

        ClientOptions clientOptions = ClientOptions.builder()
                .socketOptions(socketOptions)
                .timeoutOptions(timeoutOptions)
                .disconnectedBehavior(ClientOptions.DisconnectedBehavior.REJECT_COMMANDS)
                .autoReconnect(true)
                .cancelCommandsOnReconnectFailure(false)
                .suspendReconnectOnProtocolFailure(false)
                .requestQueueSize(Integer.MAX_VALUE)
                .build();

        GenericObjectPoolConfig genericObjectPoolConfig = new GenericObjectPoolConfig();
        genericObjectPoolConfig.setMaxIdle(redisProperties.getLettuce().getPool().getMaxIdle());
        genericObjectPoolConfig.setMinIdle(redisProperties.getLettuce().getPool().getMinIdle());
        genericObjectPoolConfig.setMaxTotal(redisProperties.getLettuce().getPool().getMaxActive());
        genericObjectPoolConfig.setMaxWait(Duration.ofMillis(redisProperties.getLettuce().getPool().getMaxWait()));

        LettucePoolingClientConfiguration poolConfig = LettucePoolingClientConfiguration.builder()
                .clientOptions(clientOptions)
                .commandTimeout(Duration.ofMillis(redisProperties.getConnectTimeout()))
                .poolConfig(genericObjectPoolConfig)
                .build();
        return new LettuceConnectionFactory(config, poolConfig);
    }
}
