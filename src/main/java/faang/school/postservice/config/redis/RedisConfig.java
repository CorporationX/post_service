package faang.school.postservice.config.redis;

import faang.school.postservice.dto.event.LikeEvent;
import faang.school.postservice.dto.redis.event.CommentEvent;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.serializer.GenericToStringSerializer;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
public class RedisConfig {

    @Bean
    JedisConnectionFactory jedisConnectionFactory() {
        return new JedisConnectionFactory();
    }

    @Bean
    RedisTemplate<String, LikeEvent> likeEventRedisTemplate() {
        RedisTemplate<String, LikeEvent> template = new RedisTemplate<>();
        template.setConnectionFactory(jedisConnectionFactory());
        template.setKeySerializer(new StringRedisSerializer());
        template.setValueSerializer(new Jackson2JsonRedisSerializer<>(LikeEvent.class));
        return template;
    }

    @Bean
    public RedisTemplate<String, Long> redisTemplate() {
        RedisTemplate<String, Long> template = new RedisTemplate<>();
        template.setConnectionFactory(jedisConnectionFactory());
        template.setValueSerializer(new GenericToStringSerializer<>(Object.class));
        return template;
    }

    @Bean
    public RedisTemplate<String, CommentEvent> commentEventRedisTemplate() {
        RedisTemplate<String, CommentEvent> template = new RedisTemplate<>();
        template.setConnectionFactory(jedisConnectionFactory());
        template.setKeySerializer(new StringRedisSerializer());
        template.setValueSerializer(new Jackson2JsonRedisSerializer<>(LikeEvent.class));
        template.setValueSerializer(new Jackson2JsonRedisSerializer<>(CommentEvent.class));
        return template;
    }

    @Bean(value = "likeChannel")
    public ChannelTopic likeChannelTopic(@Value("${spring.data.redis.like-channel.name}") String name) {
        return new ChannelTopic(name);
    }

    @Bean(value = "banChannel")
    public ChannelTopic channelTopic(@Value("${spring.data.redis.channels.user-ban-channel.name}") String userBanChannel) {
        return new ChannelTopic(userBanChannel);
    }

    @Bean(value = "commentChannel")
    public ChannelTopic commentChannel(@Value("${spring.data.redis.channels.comment-channel.name}") String commentChannel) {
        return new ChannelTopic(commentChannel);
    }
}
