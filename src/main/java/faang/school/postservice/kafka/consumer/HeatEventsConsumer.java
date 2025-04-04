package faang.school.postservice.kafka.consumer;

import faang.school.postservice.dto.feed.FeedDto;
import faang.school.postservice.dto.post.PostDto;
import faang.school.postservice.service.feed.FeedCacheService;
import faang.school.postservice.service.feed.PostCacheService;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class HeatEventsConsumer {

    private final PostCacheService postCacheService;
    private final FeedCacheService feedCacheService;

    @KafkaListener(
            topics = "${spring.kafka.topic-name.heat-posts:heat_posts}",
            containerFactory = "postDtoKafkaListenerContainerFactory"
    )
    public void listenPost(PostDto event) {
        postCacheService.savePostCache(event);
    }

    @KafkaListener(
            topics = "${spring.kafka.topic-name.heat-feed:heat_feed}",
            containerFactory = "feedDtoKafkaListenerContainerFactory"
    )
    public void listenFeed(FeedDto event) {
        feedCacheService.saveUserFeedHeat(event);
    }
}