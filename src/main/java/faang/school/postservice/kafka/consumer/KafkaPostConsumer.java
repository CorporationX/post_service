package faang.school.postservice.kafka.consumer;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.dto.event.PostEventDto;
import faang.school.postservice.dto.user.UserDto;
import faang.school.postservice.redis.cache.Feed;
import faang.school.postservice.redis.cache.RedisFeedRepository;
import faang.school.postservice.redis.cache.RedisUserRepository;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

@Slf4j
@Setter
@Component
@RequiredArgsConstructor
public class KafkaPostConsumer {
    private final RedisFeedRepository feedRepository;

    private int maxPostsAmount;

    /**
     * Feed time to live in days (in cache)
     */
    @Value("${spring.data.redis.feed-cache.ttl}")
    private int feedTtl;


    @KafkaListener(topics = "post_topic", groupId = "news_feed")
    public void handleNewPost(PostEventDto postEventDto, Acknowledgment acknowledgment) {
        postEventDto.getAuthorFollowersIds()
                .forEach(followerId -> addPostToFeed(postEventDto, followerId));

        acknowledgment.acknowledge();
    }



    private void addPostToFeed(PostEventDto postEventDto, Long followerId) {
        Feed followerFeed = feedRepository.findById(followerId)
                .orElseGet(() -> new Feed(followerId, feedTtl));

        followerFeed.addNewPost(postEventDto.getPostId(), maxPostsAmount);

        feedRepository.save(followerFeed);
    }
}
