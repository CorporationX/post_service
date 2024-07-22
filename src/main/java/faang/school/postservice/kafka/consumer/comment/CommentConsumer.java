package faang.school.postservice.kafka.consumer.comment;

import faang.school.postservice.kafka.consumer.KafkaConsumer;
import faang.school.postservice.kafka.event.comment.CommentEvent;
import faang.school.postservice.mapper.comment.CommentMapper;
import faang.school.postservice.redis.cache.service.comment.CommentCacheService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class CommentConsumer implements KafkaConsumer<CommentEvent> {

    private final CommentMapper commentMapper;
    private final CommentCacheService commentCacheService;

    @Override
    @KafkaListener(topics = "${spring.data.kafka.topics.topic-settings.comments.name}", groupId = "${spring.data.kafka.group-id}")
    public void consume(@Payload CommentEvent event, Acknowledgment ack) {

        log.info("Received new comment event {}", event);

        switch (event.getState()) {
            case ADD, UPDATE -> commentCacheService.save(commentMapper.toRedisCache(event));
            case DELETE -> commentCacheService.deleteById(event.getId());
        }

        ack.acknowledge();
    }
}
