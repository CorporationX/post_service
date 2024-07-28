package faang.school.postservice.kafka.consumer;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.config.context.UserContext;
import faang.school.postservice.dto.event.PostEventDto;
import faang.school.postservice.dto.user.UserDto;
import faang.school.postservice.redis.cache.Feed;
import faang.school.postservice.redis.cache.RedisFeedCache;
import faang.school.postservice.redis.cache.RedisUserCache;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Setter
@Component
@RequiredArgsConstructor
public class KafkaPostConsumer {
    private final RedisFeedCache feedRepository;
    private final RedisUserCache userRepository;
    private final UserServiceClient userServiceClient;
    private final UserContext userContext;

    @Value("${spring.data.redis.feed-cache.max-posts-amount}")
    private int maxPostsAmount;

    @KafkaListener(topics = "${spring.kafka.topics-names.post}", groupId = "spring.kafka.group-id")
    public void handleNewPost(PostEventDto postEventDto, Acknowledgment acknowledgment) {
        postEventDto.getAuthorFollowersIds()
                .forEach(followerId -> addPostToFeed(postEventDto, followerId));

        cachePostAuthor(postEventDto.getAuthorId());

        acknowledgment.acknowledge();
    }

    @Transactional
    public void addPostToFeed(PostEventDto postEventDto, Long followerId) {
        Feed followerFeed = feedRepository.findById(followerId)
                .orElseGet(() -> new Feed(followerId));

        followerFeed.addNewPost(postEventDto.getPostId(), maxPostsAmount);
        feedRepository.save(followerFeed);
    }

    private void cachePostAuthor(long authorId) {
        userContext.setUserId(authorId);
        UserDto user = userServiceClient.getUser(authorId);

        userRepository.save(user);
    }
}