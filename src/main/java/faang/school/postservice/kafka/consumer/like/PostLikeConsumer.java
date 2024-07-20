package faang.school.postservice.kafka.consumer.like;

import faang.school.postservice.kafka.consumer.KafkaConsumer;
import faang.school.postservice.kafka.event.like.PostLikeKafkaEvent;
import faang.school.postservice.redis.cache.service.post.PostRedisCacheService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class PostLikeConsumer implements KafkaConsumer<PostLikeKafkaEvent> {

    private final PostRedisCacheService commentRedisCacheService;

    @Override
    @KafkaListener(topics = "${spring.data.kafka.topics.topic-settings.post-likes.name}", groupId = "${spring.data.kafka.group-id}")
    public void consume(@Payload PostLikeKafkaEvent event, Acknowledgment ack) {

        switch (event.getState()) {
            case ADD -> commentRedisCacheService.incrementLikes(event.getPostId());
            case DELETE -> commentRedisCacheService.decrementLikes(event.getPostId());
        }

        log.info("Received new post like event {}", event);

        ack.acknowledge();
    }
}
