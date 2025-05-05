package faang.school.postservice.redis;

import faang.school.postservice.contants.InfoMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserBanPublisher {
    private final StringRedisTemplate redisTemplate;
    private final ChannelTopic userTopic;

    public void publishUserBan(Long userId) {
        log.info(InfoMessage.INFO_PUBLISHING_USER, userId);
        redisTemplate.convertAndSend(userTopic.getTopic(), userId.toString());
    }
}

