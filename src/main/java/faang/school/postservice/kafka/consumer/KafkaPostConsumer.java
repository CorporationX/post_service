package faang.school.postservice.kafka.consumer;

import faang.school.postservice.dto.event.PostEventDto;
import faang.school.postservice.redis.cache.Feed;
import faang.school.postservice.redis.cache.RedisFeedCache;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

@Slf4j
@Setter
@Component
@RequiredArgsConstructor
public class KafkaPostConsumer {
    private final RedisFeedCache feedRepository;

    @Value("${spring.data.redis.feed-cache.max-posts-amount}")
    private int maxPostsAmount;


    @KafkaListener(topics = "post_topic", groupId = "news_feed")
    public void handleNewPost(PostEventDto postEventDto, Acknowledgment acknowledgment) {
        postEventDto.getAuthorFollowersIds()
                .forEach(followerId -> addPostToFeed(postEventDto, followerId));

        acknowledgment.acknowledge();
    }


    private void addPostToFeed(PostEventDto postEventDto, Long followerId) {
        Feed followerFeed = feedRepository.findById(followerId)
                .orElseGet(() -> new Feed(followerId));

        followerFeed.addNewPost(postEventDto.getPostId(), maxPostsAmount);
        feedRepository.save(followerFeed);
    }
}
