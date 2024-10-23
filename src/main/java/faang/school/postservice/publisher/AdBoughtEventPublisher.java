package faang.school.postservice.publisher;

import faang.school.postservice.dto.event.AdBoughtEvent;
import jdk.jfr.Registered;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.stereotype.Component;

import java.nio.channels.Channel;

@RequiredArgsConstructor
@Component
public class AdBoughtEventPublisher {

    private final RedisTemplate<String, Object> redisTemplate;

    private ChannelTopic adBoughtEventTopic;

    public void publish(AdBoughtEvent event){redisTemplate.convertAndSend(adBoughtEventTopic.getTopic(), event);}
}
