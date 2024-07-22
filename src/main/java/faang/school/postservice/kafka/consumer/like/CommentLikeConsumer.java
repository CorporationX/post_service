package faang.school.postservice.kafka.consumer.like;

import faang.school.postservice.kafka.consumer.KafkaConsumer;
import faang.school.postservice.kafka.event.like.CommentLikeEvent;
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
public class CommentLikeConsumer implements KafkaConsumer<CommentLikeEvent> {

    private final CommentCacheService commentCacheService;

    @Override
    @KafkaListener(topics = "${spring.data.kafka.topics.topic-settings.comment-likes.name}", groupId = "${spring.data.kafka.group-id}")
    public void consume(@Payload CommentLikeEvent event, Acknowledgment ack) {

        log.info("Received new comment like event {}", event);

        switch (event.getState()) {
            case ADD -> commentCacheService.incrementLikes(event.getCommentId());
            case DELETE -> commentCacheService.decrementLikes(event.getCommentId());
        }

        ack.acknowledge();
    }
}