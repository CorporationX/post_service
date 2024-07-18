package faang.school.postservice.publishers;

import faang.school.postservice.mapper.JsonMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.stereotype.Component;

@Slf4j
@RequiredArgsConstructor
@Component
public class UserBannerPublisher implements MessagePublisher {

    private final RedisTemplate<String, Object> redisTemplate;
    private final ChannelTopic userBannerChannel;
    private final JsonMapper jsonMapper;

    @Override
    public <T> void publish(T event) {
        String message = jsonMapper.toJson(event);
        redisTemplate.convertAndSend(userBannerChannel.getTopic(), message);
    }

}
