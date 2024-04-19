package faang.school.postservice.publisher;

import faang.school.postservice.dto.event.UserEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class PublisherUsersBan {
    private final RedisTemplate<String, Object> redisTemplate;
    private final ChannelTopic userBanTopic;

    public void publish(UserEvent userEvent){
        redisTemplate.convertAndSend(userBanTopic.getTopic(), userEvent);
        log.info(String.format("PostService sending userId:%d , who should be banned", userEvent.getUserId()));
    }
}
