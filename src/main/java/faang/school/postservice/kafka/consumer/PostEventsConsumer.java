package faang.school.postservice.kafka.consumer;

import faang.school.postservice.kafka.events.PostFollowersEvent;
import faang.school.postservice.redis.service.FeedCacheService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class PostEventsConsumer {
    private final FeedCacheService feedCacheService;

    @KafkaListener(topics = "${spring.kafka.topic-name.posts:posts}")
    void listener(PostFollowersEvent event, Acknowledgment acknowledgment){
        try {
            feedCacheService.addPostIdToAuthorFollowers(event.postId(), event.followersIds(), event.publishedAt());
            acknowledgment.acknowledge();
        } catch (Exception e) {
            log.error("Post with id:{} is not added to followers feeds.", event.postId());
            throw e;
        }
    }
}