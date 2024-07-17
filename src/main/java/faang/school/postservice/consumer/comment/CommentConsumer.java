package faang.school.postservice.consumer.comment;

import faang.school.postservice.consumer.KafkaConsumer;
import faang.school.postservice.event.kafka.CommentKafkaEvent;
import faang.school.postservice.mapper.comment.CommentMapper;
import faang.school.postservice.service.redis.comment.CommentRedisCacheService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class CommentConsumer implements KafkaConsumer<CommentKafkaEvent> {

    private final CommentMapper commentMapper;
    private final CommentRedisCacheService commentRedisCacheService;

    @Override
    @KafkaListener(topics = "${spring.data.topics.topic-settings.comments.name}", groupId = "${spring.data.kafka.group-id}")
    public void consume(@Payload CommentKafkaEvent event, Acknowledgment ack) {

        commentRedisCacheService.save(commentMapper.toRedisCache(event));

        log.info("Received new event {}", event);

        ack.acknowledge();
    }
}
