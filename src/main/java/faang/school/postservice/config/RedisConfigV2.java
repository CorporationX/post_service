package faang.school.postservice.config;

//import faang.school.postservice.publisher.MessagePublisher;

import faang.school.postservice.listener.LikeEventListenerV2;
import faang.school.postservice.publisher.LikeEventPublisherV2;
import faang.school.postservice.publisher.MessagePublisherV2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.listener.PatternTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.data.redis.listener.adapter.MessageListenerAdapter;
import org.springframework.data.redis.serializer.GenericToStringSerializer;
import org.springframework.data.redis.serializer.RedisSerializer;

@Configuration
public class RedisConfigV2 {

    @Value("${spring.data.redis.host}")
    private String hostName;
    @Value("${spring.data.redis.port}")
    private int port;

//    @Autowired
//    private RedisConnectionFactory connectionFactory;

    @Bean
    public JedisConnectionFactory connectionFactory() {
        return new JedisConnectionFactory(new RedisStandaloneConfiguration(hostName, port));
    }

    @Bean
    public ChannelTopic topic() {
        return new ChannelTopic("likeEventTopic");
    }

    @Bean
    public RedisTemplate<String, Object> template(JedisConnectionFactory connectionFactory) {
        RedisTemplate<String, Object> redisTemplate = new RedisTemplate<>();
        redisTemplate.setConnectionFactory(connectionFactory);
        redisTemplate.setValueSerializer(new GenericToStringSerializer<>(Object.class));
        return redisTemplate;
    }

//    @Bean
//    MessagePublisherV2 messagePublisherV2() {
//        return new LikeEventPublisherV2(template(connectionFactory), topic());
//    }

}
