package faang.school.postservice.publisher.postevent;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.postservice.dto.post.PostViewDto;
import faang.school.postservice.event.PostViewEvent;
import faang.school.postservice.exception.ExceptionMessages;
import faang.school.postservice.publisher.MessagePublisher;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.stereotype.Component;
import java.time.LocalDateTime;

@Slf4j
@Component
@RequiredArgsConstructor
public class PostViewEventPublisher implements MessagePublisher<PostViewEvent> {
    private final RedisTemplate<String, Object> redisTemplate;
    private final ChannelTopic postViewChannelTopic;
    private final ObjectMapper objectMapper;

    @Override
    public void publish(PostViewEvent postViewEvent) {
        try {
            String message = objectMapper.writeValueAsString(postViewEvent);
            redisTemplate.convertAndSend(postViewChannelTopic.getTopic(), message);
            log.info("Published postViewEvent: {}", message);
        } catch (JsonProcessingException e) {
            log.error(ExceptionMessages.SERIALIZATION_ERROR + postViewEvent, e);
            throw new RuntimeException(e);
        } catch (Exception e) {
            log.error(ExceptionMessages.UNEXPECTED_ERROR + e.getMessage());
            throw new IllegalArgumentException(ExceptionMessages.UNEXPECTED_ERROR + e.getMessage());
        }
    }

    public void toEventAndPublish(@NotNull PostViewDto postViewDto) {
        publish(PostViewEvent.builder()
                .postId(postViewDto.getPostId())
                .authorId(postViewDto.getAuthorId())
                .userId(postViewDto.getUserId())
                .timestamp(LocalDateTime.now())
                .build());
    }
}