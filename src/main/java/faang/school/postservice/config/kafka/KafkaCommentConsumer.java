package faang.school.postservice.config.kafka;

import faang.school.postservice.dto.comment.CommentEvent;
import faang.school.postservice.service.redis.RedisCommentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class KafkaCommentConsumer {
    private final RedisCommentService redisCommentService;

    @KafkaListener(topics = "${spring.kafka.listener.topics.news-feed-comment}",
            groupId = "${spring.kafka.listener.groupId.news-feed-group}")
    public void listen(CommentEvent event, Acknowledgment ack) {
        redisCommentService.saveComment(event);
        log.info("Received message {}", event);
        ack.acknowledge();
    }
}