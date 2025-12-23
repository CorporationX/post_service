package faang.school.postservice.service;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.config.PostCache;
import faang.school.postservice.config.UserCache;
import faang.school.postservice.dto.post.PostEventDto;
import faang.school.postservice.dto.post.PostFeedDto;
import faang.school.postservice.dto.user.UserDto;
import faang.school.postservice.mapper.post.PostMapper;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.PostCacheRepository;
import faang.school.postservice.repository.PostRepository;
import faang.school.postservice.repository.UserCacheRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Slf4j
@Service
@RequiredArgsConstructor
public class FeedServiceImpl implements FeedService {
    private final RedisTemplate<String, Object> redisTemplate;
    private final PostRepository postRepository;
    private final UserServiceClient userServiceClient;
    private final PostMapper postMapper;
    private final PostCacheRepository postCacheRepository;
    private final UserCacheRepository userCacheRepository;

    @Value("${app.feed.max-size}")
    private int maxFeedSize;

    @Value("${app.feed.ttl-days:1}")
    private int feedTtlDays;

    @Value("${app.feed.default-size:20}")
    private int defaultSize;

    @Override
    public void updateFeeds(PostEventDto event) {
        List<Long> followerIds = event.followerIds();
        if (followerIds == null || followerIds.isEmpty()) {
            log.info("No followers for post {}", event.postId());
            return;
        }

        String processedKey = "processed:post:" + event.postId();

        if (Boolean.TRUE.equals(redisTemplate.hasKey(processedKey))) {
            log.info("Post {} already processed, skipping", event.postId());
            return;
        }

        try {
            savePostDetails(event);
            double score = -event.publishedAt().toInstant(ZoneOffset.UTC).toEpochMilli();

            for (Long followerId : followerIds) {
                updateSingleFeed(followerId, event.postId(), score);
            }

            redisTemplate.opsForValue().set(processedKey, "1", Duration.ofDays(feedTtlDays));
            log.info("Successfully updated feeds for {} followers, postId={}",
                    followerIds.size(), event.postId());

        } catch (Exception e) {
            log.error("Failed to process post {}", event.postId(), e);
            throw e;
        }
    }

    private void savePostDetails(PostEventDto event) {
        final String postKey = "post:" + event.postId();

        Map<String, String> postData = new HashMap<>();
        postData.put("id", event.postId().toString());
        postData.put("content", event.content());
        postData.put("publishedAt", event.publishedAt().toString());

        if (event.authorId() != null) {
            postData.put("authorId", event.authorId().toString());
        }
        if (event.projectId() != null) {
            postData.put("projectId", event.projectId().toString());
        }

        redisTemplate.opsForHash().putAll(postKey, postData);
        redisTemplate.expire(postKey, Duration.ofDays(feedTtlDays));
    }

    private void updateSingleFeed(Long followerId, Long postId, double score) {
        String feedKey = "feed:" + followerId;

        redisTemplate.opsForZSet().add(feedKey, postId.toString(), score);
        redisTemplate.opsForZSet().removeRange(feedKey, maxFeedSize, -1);
        redisTemplate.expire(feedKey, Duration.ofDays(feedTtlDays));
    }

    @Override
    public List<PostFeedDto> getUserFeed(Long userId, Long afterId, Integer pageSize) {
        int size = (pageSize != null && pageSize > 0) ? pageSize : defaultSize;

        List<Long> redisIds = getRedisIds(userId, afterId, size);
        List<PostFeedDto> feed = new ArrayList<>(buildFeed(redisIds));

        if (feed.size() < size) {
            Long lastFeedId = feed.isEmpty() ? afterId : feed.get(feed.size() - 1).id();
            feed.addAll(getFromDb(userId, lastFeedId, size - feed.size()));
        }

        return feed.size() > size ? feed.subList(0, size) : feed;
    }

    private List<Long> getRedisIds(Long userId, Long afterId, int limit) {
        String key = "feed:" + userId;
        if (!Boolean.TRUE.equals(redisTemplate.hasKey(key))) {
            return List.of();
        }

        Set<Object> ids = (afterId == null)
                ? redisTemplate.opsForZSet().reverseRange(key, 0, limit - 1)
                : getIdsAfter(key, afterId, limit);

        return (ids == null ? List.<Object>of() : new ArrayList<>(ids)).stream()
                .map(String::valueOf)
                .map(Long::valueOf)
                .toList();
    }

    private Set<Object> getIdsAfter(String key, Long afterId, int limit) {
        Long rank = redisTemplate.opsForZSet().reverseRank(key, afterId.toString());
        return rank == null ? Set.of() :
                redisTemplate.opsForZSet().reverseRange(key, rank + 1, rank + limit);
    }

    private List<PostFeedDto> buildFeed(List<Long> postIds) {
        if (postIds.isEmpty()) {
            return List.of();
        }

        Map<Long, Post> posts = getPosts(postIds);
        Set<Long> authorIds = posts.values().stream()
                .map(Post::getAuthorId).collect(Collectors.toSet());
        Map<Long, UserDto> authors = getAuthors(authorIds);

        return postIds.stream()
                .map(posts::get).filter(Objects::nonNull)
                .filter(p -> authors.containsKey(p.getAuthorId()))
                .map(postMapper::toFeedDto).toList();
    }

    private List<PostFeedDto> getFromDb(Long userId, Long afterId, int needed) {
        List<Long> followeeIds = getFolloweeIds(userId);
        if (followeeIds.isEmpty()) {
            return List.of();
        }

        LocalDateTime afterTime = afterId == null ? null
                : postRepository.findById(afterId).map(Post::getPublishedAt).orElse(null);

        return getFeedPostsFromDb(followeeIds, afterTime, needed).stream()
                .map(postMapper::toFeedDto)
                .toList();
    }

    private List<Post> getFeedPostsFromDb(List<Long> authorIds, LocalDateTime afterTime, int limit) {
        Pageable page = PageRequest.of(0, limit);
        return afterTime == null
                ? postRepository.findFeedPosts(authorIds, page)
                : postRepository.findFeedPostsAfter(authorIds, afterTime, page);
    }

    private Map<Long, Post> getPostsByIdsFromDb(List<Long> ids) {
        return StreamSupport.stream(postRepository.findAllById(ids).spliterator(), false)
                .collect(Collectors.toMap(Post::getId, p -> p));
    }

    private Map<Long, Post> getPosts(List<Long> ids) {
        Map<Long, Post> result = new HashMap<>();
        postCacheRepository.findByPostIdIn(ids)
                .forEach(cache -> result.put(cache.getPostId(), toPost(cache)));
        getPostsByIdsFromDb(ids.stream()
                .filter(id -> !result.containsKey(id)).toList())
                .forEach(result::put);
        return result;
    }

    private Map<Long, UserDto> getAuthors(Set<Long> userIds) {
        Map<Long, UserDto> result = userCacheRepository.findByUserIdIn(new ArrayList<>(userIds))
                .stream()
                .collect(Collectors.toMap(
                        UserCache::getUserId,
                        cache -> new UserDto(cache.getUserId(), cache.getUsername(),
                                cache.getEmail())
                ));

        List<Long> missing = userIds.stream()
                .filter(id -> !result.containsKey(id))
                .toList();

        if (!missing.isEmpty()) {
            userServiceClient.getUsersByIds(missing)
                    .forEach(user -> result.put(user.id(), user));
        }

        return result;
    }

    private List<Long> getFolloweeIds(Long userId) {
        try {
            return userServiceClient.getFollowees(userId, null, null, 0,
                            Integer.MAX_VALUE)
                    .stream()
                    .map(UserDto::id)
                    .toList();
        } catch (Exception e) {
            log.error("Failed to get followees for user {}", userId, e);
            return List.of();
        }
    }

    private Post toPost(PostCache cache) {
        return Post.builder()
                .id(cache.getPostId())
                .content(cache.getContent())
                .publishedAt(cache.getPublishedAt())
                .authorId(cache.getAuthorId())
                .projectId(cache.getProjectId())
                .build();
    }
}
