package faang.school.postservice.redis;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.serializer.GenericToStringSerializer;

@Configuration
public class RedisConfig {

    @Value("${spring.data.redis.topic.userBan}")
    private String userBan;

    @Bean
    JedisConnectionFactory jedisConnectionFactory() {
        return new JedisConnectionFactory();
    }

    @Bean
    public RedisTemplate<String, Object> redisTemplate(JedisConnectionFactory connectionFactory) {
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(connectionFactory);
        template.setValueSerializer(new GenericToStringSerializer<>(Object.class));
        return template;
    }

    @Bean
    ChannelTopic userBanTopic() {
        return new ChannelTopic(userBan);
    }

    @Bean
    MessagePublisher redisPublisher(RedisTemplate<String, Object> redisTemplate, ChannelTopic userBanTopic) {
        return new RedisMessagePublisher(redisTemplate, userBanTopic);
    }
}