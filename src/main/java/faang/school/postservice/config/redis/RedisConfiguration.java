package faang.school.postservice.config.redis;

import faang.school.postservice.dto.like.LikeEventDto;
import faang.school.postservice.publisher.like.LikeEventPublisher;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;

@Configuration
@RequiredArgsConstructor
public class RedisConfiguration {

    @Value("${data.redis.channel.topic:like-events}")
    private String topic;

    @Bean
    public RedisMessageListenerContainer redisContainer(RedisConnectionFactory connectionFactory) {
        RedisMessageListenerContainer container = new RedisMessageListenerContainer();
        container.setConnectionFactory(connectionFactory);
        return container;
    }

    @Bean
    public ChannelTopic channelTopic() {
        return new ChannelTopic(topic);
    }

    @Bean
    public RedisTemplate<String, LikeEventDto> redisTemplate(RedisConnectionFactory connectionFactory) {
        RedisTemplate<String, LikeEventDto> redisTemplate = new RedisTemplate<>();
        redisTemplate.setConnectionFactory(connectionFactory);
        return redisTemplate;
    }

//    @Bean
//    public LikeEventPublisher likeEventPublisher(RedisTemplate<String,LikeEventDto> redisTemplate,
//                                                 ChannelTopic topic){
//        return new LikeEventPublisher(redisTemplate, topic);
//    }
}
