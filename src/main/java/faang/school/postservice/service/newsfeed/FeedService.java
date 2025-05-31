package faang.school.postservice.service.newsfeed;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.config.context.UserContext;
import faang.school.postservice.dto.newsfeed.KafkaTimePostIdEvent;
import faang.school.postservice.dto.post.PostDto;
import faang.school.postservice.dto.user.UserDto;
import faang.school.postservice.mapper.PostMapper;
import faang.school.postservice.model.Post;
import faang.school.postservice.model.redis.RedisKeyConstants;
import faang.school.postservice.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Service
@RequiredArgsConstructor
@Slf4j
public class FeedService {

    private final RedisTemplate<String, Object> feedRedisTemplate;
    private final PostRepository postRepository;
    private final PostMapper postMapper;
    private final UserContext userContext;
    private final UserServiceClient userServiceClient;

    public List<PostDto> getFeed(int page, int size) {
        Long userId = userContext.getUserId();

        if (!userExists(userId)) {
            return Collections.emptyList();
        }

        List<Long> postIds = fetchPostIdsFromCache(userId, page, size);
        if (postIds.isEmpty()) {
            log.info("No post IDs found in cache for user {} or feed is empty.", userId);
            return Collections.emptyList();
        }

        List<Post> posts = fetchPostsByIds(postIds);
        if (posts.isEmpty()) {
            log.warn("No posts found in repository for IDs: {}. Cache might be stale.", postIds);
            return Collections.emptyList();
        }

        List<PostDto> postDtos = mapPostsToDtoAndPreserveOrder(posts, postIds);
        log.info("Retrieved {} posts for feed of user {}", postDtos.size(), userId);
        return postDtos;
    }

    private boolean userExists(Long userId) {
        try {
            UserDto user = userServiceClient.getUser(userId);
            if (user == null) {
                log.warn("User with id {} not found via UserServiceClient", userId);
                return false;
            }
            return true;
        } catch (Exception e) {
            log.error("Error fetching user {} from UserServiceClient: {}", userId, e.getMessage());
            return false;
        }
    }

    private List<Long> fetchPostIdsFromCache(Long userId, int page, int size) {
        String feedKey = RedisKeyConstants.FEED_KEY_PREFIX.getValue() + userId;
        long start = (long) page * size;
        long end = start + size - 1;

        Set<ZSetOperations.TypedTuple<Object>> feedItemsWithScores = feedRedisTemplate.opsForZSet()
                .reverseRangeWithScores(feedKey, start, end);

        if (feedItemsWithScores == null || feedItemsWithScores.isEmpty()) {
            log.info("Feed for user {} is empty or not found in cache at page {}, size {}.", userId, page, size);
            return Collections.emptyList();
        }
        return mapFeedItemsToPostIds(feedItemsWithScores, userId);
    }

    private List<Long> mapFeedItemsToPostIds(Set<ZSetOperations.TypedTuple<Object>> feedItemsWithScores, Long userId) {
        return feedItemsWithScores.stream()
                .map(item -> convertFeedItemToPostId(item.getValue(), userId))
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    private Long convertFeedItemToPostId(Object feedItemValue, Long userId) {
        if (feedItemValue instanceof KafkaTimePostIdEvent event) {
            return event.id();
        } else if (feedItemValue instanceof Number num) {
            return num.longValue();
        }
        log.warn("Unexpected object type in feed for user {}: {}",
                userId, feedItemValue != null ? feedItemValue.getClass().getName() : "null");
        return null;
    }

    private List<Post> fetchPostsByIds(List<Long> postIds) {
        if (postIds.isEmpty()) {
            return Collections.emptyList();
        }
        Iterable<Post> postsIterable = postRepository.findAllById(postIds);
        return StreamSupport.stream(postsIterable.spliterator(), false).toList();
    }

    private List<PostDto> mapPostsToDtoAndPreserveOrder(List<Post> posts, List<Long> orderedPostIds) {
        Map<Long, Post> postMap = posts.stream()
                .collect(Collectors.toMap(Post::getId, post -> post));

        return orderedPostIds.stream()
                .map(postMap::get)
                .filter(Objects::nonNull)
                .map(postMapper::toDto)
                .collect(Collectors.toList());
    }
}
