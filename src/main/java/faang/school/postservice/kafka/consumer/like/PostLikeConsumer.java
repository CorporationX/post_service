package faang.school.postservice.kafka.consumer.like;

import faang.school.postservice.kafka.consumer.KafkaConsumer;
import faang.school.postservice.kafka.event.like.PostLikeEvent;
import faang.school.postservice.redis.cache.service.post.PostCacheService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class PostLikeConsumer implements KafkaConsumer<PostLikeEvent> {

    private final PostCacheService commentRedisCacheService;

    @Override
    @KafkaListener(topics = "${spring.data.kafka.topics.topic-settings.post-likes.name}", groupId = "${spring.data.kafka.group-id}")
    public void consume(@Payload PostLikeEvent event, Acknowledgment ack) {

        log.info("Received new post like event {}", event);
        commentRedisCacheService.incrementLikes(event.getPostId());

        ack.acknowledge();
    }
}
