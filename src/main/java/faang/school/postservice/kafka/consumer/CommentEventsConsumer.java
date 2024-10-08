package faang.school.postservice.kafka.consumer;

import faang.school.postservice.kafka.events.CommentEvent;
import faang.school.postservice.redis.service.PostCacheService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class CommentEventsConsumer {
    private final PostCacheService postCacheService;

    @KafkaListener(topics = "${spring.kafka.topic-name.comments:comments}")
    void listener(CommentEvent event, Acknowledgment acknowledgment){
        try {
            postCacheService.addCommentToCachedPost(event.postId(), event.commentDto());
            acknowledgment.acknowledge();
            log.info("Comment with id:{} is successfully added to post.", event.commentDto().getId());
        } catch (Exception e) {
            log.error("Comment with id:{} is not added to post.", event.commentDto().getId());
            throw e;
        }
    }
}