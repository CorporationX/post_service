package faang.school.postservice.config.kafka.consumer;

import faang.school.postservice.event.CommentEventRecord;
import faang.school.postservice.redis.service.PostCacheService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class CommentEventsConsumer {
    private final PostCacheService postCacheService;

    @KafkaListener(topics = "${spring.data.kafka.topic_name.comments}", groupId = "${spring.data.kafka.consumer.group-id}")
    void listener(CommentEventRecord event, Acknowledgment acknowledgment) {
        try {
            postCacheService.addCommentToCachedPost(event.commentDto().getPostId(), event.commentDto());
            acknowledgment.acknowledge();
            log.info("Comment with id:{} is successfully added to post.", event.commentDto().getId());
        } catch (Exception e) {
            log.error("Comment with id:{} is not added to post.", event.commentDto().getId());
            throw e;
        }
    }
}