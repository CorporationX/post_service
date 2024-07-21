package faang.school.postservice.kafka.consumer.post;

import faang.school.postservice.kafka.consumer.KafkaConsumer;
import faang.school.postservice.kafka.event.post.PostKafkaEvent;
import faang.school.postservice.mapper.PostMapper;
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
public class PostConsumer implements KafkaConsumer<PostKafkaEvent> {

    private final PostMapper postMapper;
    private final PostRedisCacheService postRedisCacheService;

    @Override
    @KafkaListener(topics = "${spring.data.kafka.topics.topic-settings.posts.name}", groupId = "${spring.data.kafka.group-id}")
    public void consume(@Payload PostKafkaEvent event, Acknowledgment ack) {

        log.info("Received new post event {}", event);

        switch (event.getState()) {
            case ADD, UPDATE -> postRedisCacheService.save(postMapper.toRedisCache(event), event.getSubscriberIds());
            case DELETE -> postRedisCacheService.deleteById(event.getPostId(), event.getSubscriberIds());
        }

        ack.acknowledge();
    }
}
