package faang.school.postservice.consumer;

import faang.school.postservice.dto.event.CommentEvent;
import faang.school.postservice.exception.NonRetryableException;
import faang.school.postservice.exception.RetryableException;
import faang.school.postservice.mapper.CommentMapper;
import faang.school.postservice.service.cache.PostCacheService;
import faang.school.postservice.util.JsonMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class CommentListener extends KafkaEventListener {
    private final JsonMapper jsonMapper;
    private final CommentMapper commentMapper;
    private final PostCacheService postCacheService;

    @KafkaListener(
            topics = "${spring.kafka.topic.commentNew}",
            groupId = "comment")
    public void consumeNew(String data, Acknowledgment ack) {
        try {
            CommentEvent event = jsonMapper.fromJson(data, CommentEvent.class);
            log.info("New comment event received from Kafka: {}", data);
            postCacheService.addComment(event.postId(), commentMapper.toCommentDto(event));
            ack.acknowledge();
        } catch (RetryableException e) {
            log.error(e.getMessage(), e);
            throw e;
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new NonRetryableException(e);
        }
    }
}
