package faang.school.postservice.config.redis;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
public class RedisConfig {

    @Value("${spring.data.redis.channels.comment}")
    private String commentChannel;
    @Value("${spring.data.redis.channels.postView}")
    private String postViewChannel;
    @Value("${spring.data.redis.channels.like}")
    private String likeChannel;

    @Bean
    public RedisTemplate<String, Object> redisTemplate(
            RedisConnectionFactory connectionFactory,
            ObjectMapper objectMapper) {
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(connectionFactory);

        GenericJackson2JsonRedisSerializer serializer = new GenericJackson2JsonRedisSerializer(objectMapper);
        template.setDefaultSerializer(serializer);

        // Use String serializer for keys
        template.setKeySerializer(new StringRedisSerializer());
        template.setHashKeySerializer(new StringRedisSerializer());
        
        // Use JSON serializer for values
        template.setValueSerializer(new GenericJackson2JsonRedisSerializer());
        template.setHashValueSerializer(new GenericJackson2JsonRedisSerializer());

        return template;
    }

    @Bean
    @Qualifier("commentTopic")
    public ChannelTopic commentTopic() {
        return new ChannelTopic(commentChannel);
    }

    @Bean
    @Qualifier("postViewTopic")
    public ChannelTopic postViewTopic() {
        return new ChannelTopic(postViewChannel);
    }

    @Bean
    @Qualifier("likeTopic")
    public ChannelTopic likeTopic() {
        return new ChannelTopic(likeChannel);
    }
}