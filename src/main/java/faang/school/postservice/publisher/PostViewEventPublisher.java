package faang.school.postservice.publisher;

import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.postservice.event.PostViewEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class PostViewEventPublisher implements MessagePublisher<PostViewEvent> {

    @Value("${spring.data.redis.channels.post-view-event-channel.name.post_view}")
    private String channelTopic;
    private final RedisTemplate<String, Object> redisTemplate;
    private final GenericJackson2JsonRedisSerializer serializer = new GenericJackson2JsonRedisSerializer();

    @Override
    public void publish(PostViewEvent event) {

        try {
            byte[] message = serializer.serialize(event);

            if (message == null) {
                throw new IllegalArgumentException("Not possible to create an event " + event);
            }
            redisTemplate.convertAndSend(channelTopic, message);
            log.info("Published event to Redis: {} ", event);

        } catch (Exception e) {
            log.error("Error by publishing event in Redis", e);
            throw new RuntimeException(e);
        }


    }
}
