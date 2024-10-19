package faang.school.postservice.kafka.consumer;

import faang.school.postservice.cache.service.PostRedisService;
import faang.school.postservice.kafka.event.comment.CommentAddedEvent;
import faang.school.postservice.mapper.CommentMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class CommentEventConsumer {
    private final PostRedisService postRedisService;
    private final CommentMapper commentMapper;

    @Async
    @KafkaListener(topics = "${spring.kafka.topic.comment.added}", groupId = "${spring.kafka.consumer.group-id}")
    public void consume(CommentAddedEvent event, Acknowledgment ack) {
        log.info("Received {}", event.toString());
        postRedisService.addCommentConcurrent(commentMapper.toRedis(event));
        ack.acknowledge();
    }
}
