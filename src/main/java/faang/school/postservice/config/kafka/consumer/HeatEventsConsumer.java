package faang.school.postservice.config.kafka.consumer;

import faang.school.postservice.dto.post.PostDto;
import faang.school.postservice.event.FeedDto;
import faang.school.postservice.redis.service.FeedCacheService;
import faang.school.postservice.redis.service.PostCacheService;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class HeatEventsConsumer {
    private final PostCacheService postCacheService;
    private final FeedCacheService feedCacheService;

    @KafkaListener(topics = "${spring.data.kafka.topic_name.heat_feed}", groupId = "${spring.data.kafka.consumer.group-id}")
    void listener(FeedDto event) {
        feedCacheService.saveUserFeedHeat(event);
    }

    @KafkaListener(topics = "${spring.data.kafka.topic_name.heat_posts}", groupId = "${spring.data.kafka.consumer.group-id}")
    void listener(PostDto event) {
        postCacheService.savePostCache(event);
    }
}