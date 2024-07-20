package faang.school.postservice.kafka.consumer.like;

import faang.school.postservice.kafka.consumer.KafkaConsumer;
import faang.school.postservice.kafka.event.like.CommentLikeKafkaEvent;
import faang.school.postservice.redis.cache.service.comment.CommentRedisCacheService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class CommentLikeConsumer implements KafkaConsumer<CommentLikeKafkaEvent> {

    private final CommentRedisCacheService commentRedisCacheService;

    @Override
    @KafkaListener(topics = "${spring.data.kafka.topics.topic-settings.comment-likes.name}", groupId = "${spring.data.kafka.group-id}")
    public void consume(@Payload CommentLikeKafkaEvent event, Acknowledgment ack) {

        log.info("Received new comment like event {}", event);

        switch (event.getState()) {
            case ADD -> commentRedisCacheService.incrementLikes(event.getCommentId());
            case DELETE -> commentRedisCacheService.decrementLikes(event.getCommentId());
        }

        ack.acknowledge();
    }
}