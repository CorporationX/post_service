package faang.school.postservice.publisher;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class AuthorBanPublisher {

    private final RedisTemplate<String, Object> redisTemplate;

    @Value("${spring.data.redis.channels.user-ban}")
    private String userBanTopic;

    public void publish(Long authorId) {
        redisTemplate.convertAndSend(userBanTopic, authorId);
        log.debug("Sending new request on ban author with id {}", authorId);
    }

}
