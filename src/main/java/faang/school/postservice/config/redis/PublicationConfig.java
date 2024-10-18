package faang.school.postservice.config.redis;

import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.postservice.dto.event.CommentEvent;
import faang.school.postservice.dto.event.LikeEvent;
import faang.school.postservice.service.publisher.messagePublishers.CommentEventPublisher;
import faang.school.postservice.service.publisher.PublicationService;
import faang.school.postservice.service.publisher.messagePublishers.LikeEventPublisher;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@org.springframework.context.annotation.Configuration
@RequiredArgsConstructor
public class PublicationConfig {

    @Bean
    public JedisConnectionFactory jedisConnectionFactory() {
        return new JedisConnectionFactory();
    }


    @Bean
    RedisTemplate<String, Object> redisTemplate(JedisConnectionFactory jedisConnectionFactory) {
        final RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(jedisConnectionFactory);
        template.setKeySerializer(new StringRedisSerializer());
        template.setValueSerializer(new StringRedisSerializer());
        return template;
    }

    @Bean
    public PublicationService<CommentEventPublisher, CommentEvent> commentPublicationService(CommentEventPublisher commentEventPublisher,
                                                                                      ObjectMapper objectMapper) {
        return new PublicationService<>(commentEventPublisher, objectMapper);
    }

    @Bean
    CommentEventPublisher commentEventPublisher(RedisTemplate<String, Object> redisTemplate,
                                                @Value("${spring.data.redis.channels.comment}") String topic){
        return new CommentEventPublisher(redisTemplate, topic);
    }

    @Bean
    public PublicationService<LikeEventPublisher, LikeEvent> likePublicationService(LikeEventPublisher likeEventPublisher,
                                                                                ObjectMapper objectMapper) {
        return new PublicationService<>(likeEventPublisher, objectMapper);
    }

    @Bean
    LikeEventPublisher likeEventPublisher(RedisTemplate<String, Object> redisTemplate,
                                          @Value("${spring.data.redis.channels.like_event_channel}") String topic){
        return new LikeEventPublisher(redisTemplate, topic);
    }
}