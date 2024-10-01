package faang.school.postservice.kafka.consumer;

import faang.school.postservice.kafka.events.PostFollowersEvent;
import faang.school.postservice.redis.service.FeedCacheService;
import faang.school.postservice.redis.service.PostCacheService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class KafkaPostConsumer {
    private final FeedCacheService feedCacheService;
    private final PostCacheService postCacheService;

    @KafkaListener(topics = "${spring.kafka.topic-name.posts:posts}")
    void listener(PostFollowersEvent event, Acknowledgment acknowledgment){
        try {
            addPostIdToAuthorFollowers(event.postId(), event.followersIds());
            acknowledgment.acknowledge();
        } catch (Exception e) {
            log.error("Post with id:{} is not added to followers feeds.", event.postId());
            throw e;
        }
    }

    private void addPostIdToAuthorFollowers(Long postId, List<Long> followersIds) {
        if (postCacheService.existsById(postId)) {
            feedCacheService.addPostToFeeds(postId, followersIds);
        } else {
            log.warn("Post with id:{} does not exist in Redis.", postId);
        }
    }
}