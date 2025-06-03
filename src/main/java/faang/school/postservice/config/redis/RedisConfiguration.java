package faang.school.postservice.config.redis;

import faang.school.postservice.publisher.MessagePublisher;
import faang.school.postservice.publisher.user.UserPublisher;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.data.redis.serializer.GenericToStringSerializer;

@Configuration
@ConfigurationPropertiesScan
@RequiredArgsConstructor
public class RedisConfiguration {
    private final RedisParam redisParams;

    @Bean
    public RedisMessageListenerContainer redisContainer() {
        RedisMessageListenerContainer container = new RedisMessageListenerContainer();
        container.setConnectionFactory(jedisConnectionFactory());
        return container;
    }

    @Bean
    public JedisConnectionFactory jedisConnectionFactory() {
        RedisStandaloneConfiguration config = new RedisStandaloneConfiguration();
        config.setHostName(redisParams.host());
        config.setPort(redisParams.port());

        return new JedisConnectionFactory(config);
    }

    @Bean
    public ChannelTopic userTopic() {
        return new ChannelTopic(redisParams.channels().user());
    }

    @Bean
    public RedisTemplate<String, Object> redisTemplate() {
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(jedisConnectionFactory());
        template.setValueSerializer(new GenericToStringSerializer<Object>(Object.class));

        return template;
    }

    @Bean
    public MessagePublisher redisUserPublisher() {
        return new UserPublisher(redisTemplate(), userTopic());
    }
}
