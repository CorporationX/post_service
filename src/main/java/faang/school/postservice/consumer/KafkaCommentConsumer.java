package faang.school.postservice.consumer;

import faang.school.postservice.events.CommentEventForKafka;
import faang.school.postservice.service.redis.PostCacheService;
import faang.school.postservice.service.redis.UserCacheService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class KafkaCommentConsumer {

    private final PostCacheService postCacheService;
    private final UserCacheService userCacheService;

    @Value(value = "${spring.data.kafka.topic.comments_topic}")
    private String commentsTopic;

    @KafkaListener(topics = "commentsTopic",
            containerFactory = "eventKafkaListenerContainerFactory")
    public void listenCommentEventForKafka(CommentEventForKafka event, Acknowledgment ack) {
        log.info("Received comment event: {}", event);
        Long commentId = event.getCommentId();
        Long postId = event.getPostId();
        Long authorId = event.getAuthorId();
        postCacheService.addCommentToPostInCache(postId, commentId);
        userCacheService.getUserFromCache(authorId);
        ack.acknowledge();
    }
}
