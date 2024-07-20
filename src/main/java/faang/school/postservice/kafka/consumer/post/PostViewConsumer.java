package faang.school.postservice.kafka.consumer.post;

import faang.school.postservice.kafka.consumer.KafkaConsumer;
import faang.school.postservice.kafka.event.post.PostViewKafkaEvent;
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
public class PostViewConsumer implements KafkaConsumer<PostViewKafkaEvent> {

    private final PostRedisCacheService postRedisCacheService;

    @Override
    @KafkaListener(topics = "${spring.data.kafka.topics.topic-settings.post-views.name}", groupId = "${spring.data.kafka.group-id}")
    public void consume(@Payload PostViewKafkaEvent event, Acknowledgment ack) {

        log.info("Received new post view event {}", event);

        postRedisCacheService.incrementViews(event.getPostId());

        ack.acknowledge();
    }
}
