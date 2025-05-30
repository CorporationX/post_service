package faang.school.postservice.service.newsfeed;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.config.context.UserContext;
import faang.school.postservice.dto.newsfeed.KafkaTimePostIdEvent;
import faang.school.postservice.dto.post.PostDto;
import faang.school.postservice.dto.user.UserDto;
import faang.school.postservice.mapper.PostMapper;
import faang.school.postservice.model.Post;
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

    private static final String FEED_KEY_PREFIX = "feed:";

    public List<PostDto> getFeed(int page, int size) {
        Long userId = userContext.getUserId();
        try {
            UserDto user = userServiceClient.getUser(userId);
            if (user == null) {
                log.warn("User with id {} not found via UserServiceClient", userId);
                return Collections.emptyList();
            }
        } catch (Exception e) {
            log.error("Error fetching user {} from UserServiceClient: {}", userId, e.getMessage());
            return Collections.emptyList();
        }

        String feedKey = FEED_KEY_PREFIX + userId;
        long start = (long) page * size;
        long end = start + size - 1;

        Set<ZSetOperations.TypedTuple<Object>> feedItemsWithScores = feedRedisTemplate.opsForZSet()
                .reverseRangeWithScores(feedKey, start, end);

        if (feedItemsWithScores == null || feedItemsWithScores.isEmpty()) {
            log.info("Feed for user {} is empty or not found in cache.", userId);
            return Collections.emptyList();
        }

        List<Long> postIds = feedItemsWithScores.stream()
                .map(item -> {
                    if (item.getValue() instanceof KafkaTimePostIdEvent event) {
                        return event.id();
                    } else if (item.getValue() instanceof Number num) {
                        return num.longValue();
                    }

                    log.warn("Unexpected object type in feed for user {}: {}",
                            userId, Objects.requireNonNull(item.getValue()).getClass().getName());
                    return null;
                })
                .filter(java.util.Objects::nonNull)
                .collect(Collectors.toList());

        if (postIds.isEmpty()) {
            return Collections.emptyList();
        }

        Iterable<Post> postsIterable = postRepository.findAllById(postIds);

        List<Post> posts = StreamSupport.stream(postsIterable.spliterator(), false)
                .toList();

        Map<Long, Post> postMap = posts.stream()
                .collect(Collectors.toMap(Post::getId, post -> post));

        List<PostDto> postDtos = postIds.stream()
                .map(postMap::get)
                .filter(java.util.Objects::nonNull)
                .map(postMapper::toDto)
                .toList();

        log.info("Retrieved {} posts for feed of user {}", postDtos.size(), userId);
        return postDtos;
    }
}
