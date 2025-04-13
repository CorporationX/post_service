package faang.school.postservice.publisher.like;

import faang.school.postservice.dto.like.LikeEvent;
import faang.school.postservice.publisher.AbstractEventPublisher;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import faang.school.postservice.dto.like.LikeEvent;
import faang.school.postservice.publisher.AbstractEventPublisher;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

@Component
public class LikeEventPublisher extends AbstractEventPublisher<LikeEvent> {
@RequiredArgsConstructor
@Slf4j
public class LikeEventPublisher {

    private final RedisTemplate<String, Object> redisTemplate;
public class LikeEventPublisher extends AbstractListEventPublisher {
public class LikeEventPublisher extends AbstractEventPublisher<LikeEvent> {

    @Value("${spring.data.redis.channels.like-channel.name}")
    private String likeChannel;

    public LikeEventPublisher(RedisTemplate<String, Object> redisTemplate) {
        super(redisTemplate);
    }

    @Override
    protected String getChannel() {
        return likeChannel;
    public void publish(LikeEvent likeEvent) {
        redisTemplate.convertAndSend(likeChannel, likeEvent);
        log.debug("Published LikeEvent to channel {}: {}", likeChannel, likeEvent);
    public LikeEventPublisher(RedisTemplate<String, Object> redisTemplate) {
        super(redisTemplate);
    }

    @Override
    protected String getChannel() {
        return likeChannel;
    }
}
