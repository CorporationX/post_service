package faang.school.postservice.publisher;

import faang.school.postservice.dto.event.LikeEvent;
import faang.school.postservice.properties.RedisProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class LikePublisher {

    private final RedisProperties redisProperties;
    private final RedisTemplate<String, Object> redisTemplate;

    public void publish(LikeEvent event){
        String topic =  redisProperties.getTopic(event.getType());
        redisTemplate.convertAndSend(topic,event);
    }
}
