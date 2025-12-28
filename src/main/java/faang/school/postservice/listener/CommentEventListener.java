package faang.school.postservice.listener;

import faang.school.postservice.dto.comment.CommentEventDto;
import faang.school.postservice.repository.cache.PostCacheRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class CommentEventListener {
    final PostCacheRepository postCacheRepository;

    @KafkaListener(
            topics = "${kafka.topic.comment-event}",
            containerFactory = "concurrentKafkaCommentListenerFactory"
    )
    public void handleCommentPublishEvent(CommentEventDto commentEventDto, Acknowledgment acknowledgment) {
        log.info("New comment publish event: {}", commentEventDto);
        try {
            postCacheRepository.incrementCommentCount(commentEventDto.postId());
        } catch (Exception exception) {
            log.error("Failed to process a comment event: {}", commentEventDto, exception);
            return;
        }
        acknowledgment.acknowledge();
    }
}
