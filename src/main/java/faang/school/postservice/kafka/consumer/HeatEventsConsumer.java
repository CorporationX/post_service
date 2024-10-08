package faang.school.postservice.kafka.consumer;

import faang.school.postservice.dto.post.PostDto;
import faang.school.postservice.kafka.events.FeedDto;
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

    @KafkaListener(topics = "${spring.kafka.topic-name.heat-posts:heat_posts}")
    void listener(PostDto event){
        postCacheService.savePostCache(event);
    }

    @KafkaListener(topics = "${spring.kafka.topic-name.heat-feed:heat_feed}")
    void listener(FeedDto event){
        feedCacheService.saveUserFeedHeat(event);
    }
}