package faang.school.postservice.publisher.bought;

import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.postservice.dto.bought.AdBoughtEvent;
import faang.school.postservice.publisher.EventPublisherAbstract;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

@Component
public class AdBoughtEventPublisher extends EventPublisherAbstract<AdBoughtEvent> {
    @Value("${spring.data.redis.channels.ad-bought-channel}")
    private String channelName;

    public AdBoughtEventPublisher(RedisTemplate<String, Object> redisTemplate, ObjectMapper objectMapper) {
        super(redisTemplate, objectMapper);
    }

    public void publish(AdBoughtEvent adBoughtEvent) {
        handleEvent(adBoughtEvent, channelName);
    }
}
