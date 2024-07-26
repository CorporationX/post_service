package faang.school.postservice.kafka.consumer;

import faang.school.postservice.config.context.UserContext;
import faang.school.postservice.dto.event.FeedHeatEventDto;
import faang.school.postservice.dto.post.PostForFeedDto;
import faang.school.postservice.dto.user.UserDto;
import faang.school.postservice.redis.cache.Feed;
import faang.school.postservice.redis.cache.RedisFeedCache;
import faang.school.postservice.redis.cache.RedisPostCache;
import faang.school.postservice.redis.cache.RedisUserCache;
import faang.school.postservice.service.feed.FeedService;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Setter
@Component
@RequiredArgsConstructor
public class KafkaFeedHeatEventConsumer {
    private final RedisFeedCache feedCache;
    private final RedisPostCache postCache;
    private final RedisUserCache userCache;
    private final FeedService feedService;
    private final UserContext userContext;


    @Transactional
    @KafkaListener(topics = "${spring.kafka.topics-names.feed-heat}", groupId = "spring.kafka.group-id")
    public void handleFeedHeat(FeedHeatEventDto feedHeatEventDto, Acknowledgment acknowledgment) {
        feedHeatEventDto.getUsersIds().forEach(this::heatFeedForUser);

        acknowledgment.acknowledge();
    }

    private void heatFeedForUser(Long userId) {
        userContext.setUserId(userId);

        List<PostForFeedDto> completeUserFeed = feedService.getFeed(userId, null);

        List<UserDto> authorsDtos = completeUserFeed.stream()
                .map(PostForFeedDto::getPostAuthor)
                .toList();

        Set<Long> usersFeedPostsIds = completeUserFeed.stream()
                .map(PostForFeedDto::getPostId)
                .collect(Collectors.toSet());

        Feed feed = new Feed(userId, new LinkedHashSet<>(usersFeedPostsIds));

        feedCache.save(feed);
        userCache.saveAll(authorsDtos);
        postCache.saveAll(completeUserFeed);
    }
}
