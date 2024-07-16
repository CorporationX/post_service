package faang.school.postservice.kafka.consumer;

import faang.school.postservice.dto.event.PostEventDto;
import faang.school.postservice.redis.cache.Feed;
import faang.school.postservice.redis.cache.RedisFeedRepository;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

@Slf4j
@Setter
@Component
@RequiredArgsConstructor
@ConfigurationProperties(prefix = "spring.data.redis.feed-cache")
public class KafkaPostConsumer {
    private final RedisFeedRepository feedRepository;

    private int maxPostsAmount;

    /**
     * Time to live in days
     */
    private int ttl;

    @KafkaListener(topics = "post_topic", groupId = "news_feed")
    public void listenPostTopic(PostEventDto postEventDto, Acknowledgment acknowledgment) {
        postEventDto.getAuthorFollowersIds()
                .forEach(followerId -> addPostToFeed(postEventDto, followerId));

        acknowledgment.acknowledge();
    }

    public void addPostToFeed(PostEventDto postEventDto, Long followerId) {
        Feed followerFeed = feedRepository.findById(followerId)
                .orElseGet(() -> new Feed(followerId, ttl));

        followerFeed.addNewPost(postEventDto, maxPostsAmount);

        feedRepository.save(followerFeed);
    }
}
