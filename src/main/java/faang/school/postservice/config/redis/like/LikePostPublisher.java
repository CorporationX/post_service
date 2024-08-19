package faang.school.postservice.config.redis.like;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.postservice.config.redis.MessagePublisher;
import faang.school.postservice.dto.like.LikeDto;
import faang.school.postservice.dto.like.LikeEvent;
import faang.school.postservice.mapper.LikeEventMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;


@Component
@RequiredArgsConstructor
@Slf4j
public class LikePostPublisher implements MessagePublisher {

    private final RedisTemplate<String, Object> redisTemplate;
    private final ChannelTopic likePostTopic;
    private final ObjectMapper objectMapper;
    private final LikeEventMapper likeEventMapper;

    @Override
    public void publish(String message) {
        redisTemplate.convertAndSend(likePostTopic.getTopic(), message);
    }

    @Async
    public void createLikeEvent(LikeDto likeDto) {
        try {
            LikeEvent likeEvent = likeEventMapper.mapLikeEvent(likeDto);
            publish(objectMapper.writeValueAsString(likeEvent));
        } catch (JsonProcessingException e) {
            log.error(e.getMessage(), e);
        }
    }
}