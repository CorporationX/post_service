package faang.school.postservice.service;

import faang.school.postservice.model.Post;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@RequiredArgsConstructor
public class PostCacheService {

    private final RedisTemplate<String, Object> postRedisTemplate;

    @Value("${app.redis.post-cache.ttl-hours:24}")
    private int ttlHours;

    @Value("${app.redis.post-cache.key-prefix:post:}")
    private String keyPrefix;

    @Value("${app.redis.post-cache.user-feed-prefix:user_feed:}")
    private String userFeedPrefix;

    @Value("${app.redis.post-cache.trending-posts-key:trending_posts}")
    private String trendingPostsKey;

    /**
     * Кэширует пост в Redis с TTL
     *
     * @param post пост для кэширования
     */
    public void cachePost(Post post) {
        if (post == null || post.getId() == null) {
            log.warn("Cannot cache null post or post without ID");
            return;
        }

        try {
            String key = keyPrefix + post.getId();
            postRedisTemplate.opsForValue().set(key, post, Duration.ofHours(ttlHours));

            log.debug("Cached post ID: {} with TTL {} hours", post.getId(), ttlHours);
        } catch (Exception e) {
            log.error("Failed to cache post ID: {}", post.getId(), e);
        }
    }

    /**
     * Получает пост из кэша
     *
     * @param postId ID поста
     * @return пост или null если не найден
     */
    public Optional<Post> getCachedPost(Long postId) {
        if (postId == null) {
            return Optional.empty();
        }

        try {
            String key = keyPrefix + postId;
            Object cached = postRedisTemplate.opsForValue().get(key);

            if (cached instanceof Post post) {
                log.debug("Found cached post ID: {}", postId);
                return Optional.of(post);
            } else {
                log.debug("Post ID: {} not found in cache", postId);
                return Optional.empty();
            }
        } catch (Exception e) {
            log.error("Failed to get cached post ID: {}", postId, e);
            return Optional.empty();
        }
    }

    /**
     * Удаляет пост из кэша
     *
     * @param postId ID поста
     */
    public void evictPost(Long postId) {
        if (postId == null) {
            return;
        }

        try {
            String key = keyPrefix + postId;
            Boolean deleted = postRedisTemplate.delete(key);
            log.debug("Evicted post ID: {} from cache, result: {}", postId, deleted);
        } catch (Exception e) {
            log.error("Failed to evict post ID: {} from cache", postId, e);
        }
    }

    /**
     * Кэширует список постов для пользователя (для News Feed)
     *
     * @param userId ID пользователя
     * @param posts список постов
     */
    public void cacheUserFeed(Long userId, List<Post> posts) {
        if (userId == null || posts == null) {
            return;
        }

        try {
            String key = userFeedPrefix + userId;

            List<Long> postIds = posts.stream()
                    .map(Post::getId)
                    .filter(Objects::nonNull)
                    .toList();

            postRedisTemplate.opsForValue().set(key, postIds, Duration.ofHours(ttlHours));

            posts.forEach(this::cachePost);

            log.debug("Cached feed for user ID: {} with {} posts", userId, postIds.size());
        } catch (Exception e) {
            log.error("Failed to cache feed for user ID: {}", userId, e);
        }
    }

    /**
     * Получает кэшированную ленту пользователя
     *
     * @param userId ID пользователя
     * @return список постов или пустой список
     */
    public List<Post> getCachedUserFeed(Long userId) {
        if (userId == null) {
            return Collections.emptyList();
        }

        try {
            String key = userFeedPrefix + userId;
            Object cached = postRedisTemplate.opsForValue().get(key);

            if (cached instanceof List<?> postIds) {
                List<Post> posts = new ArrayList<>();

                for (Object id : postIds) {
                    if (id instanceof Number) {
                        Long postId = ((Number) id).longValue();
                        getCachedPost(postId).ifPresent(posts::add);
                    }
                }

                log.debug("Retrieved cached feed for user ID: {} with {} posts", userId, posts.size());
                return posts;
            }

            return Collections.emptyList();
        } catch (Exception e) {
            log.error("Failed to get cached feed for user ID: {}", userId, e);
            return Collections.emptyList();
        }
    }

    /**
     * Добавляет пост в кэш трендовых постов
     *
     * @param post пост
     * @param score оценка (например, количество лайков + просмотров)
     */
    public void addToTrendingPosts(Post post, double score) {
        if (post == null || post.getId() == null) {
            return;
        }

        try {
            cachePost(post);

            postRedisTemplate.opsForZSet().add(trendingPostsKey, post.getId(), score);

            postRedisTemplate.expire(trendingPostsKey, Duration.ofHours(ttlHours));

            log.debug("Added post ID: {} to trending with score: {}", post.getId(), score);
        } catch (Exception e) {
            log.error("Failed to add post ID: {} to trending", post.getId(), e);
        }
    }

    /**
     * Получает топ трендовых постов
     *
     * @param limit количество постов
     * @return список трендовых постов
     */
    public List<Post> getTrendingPosts(int limit) {
        try {
            Set<Object> postIds = postRedisTemplate.opsForZSet()
                    .reverseRange(trendingPostsKey, 0, limit - 1);

            if (postIds == null || postIds.isEmpty()) {
                return Collections.emptyList();
            }

            List<Post> trendingPosts = new ArrayList<>();

            for (Object id : postIds) {
                if (id instanceof Number) {
                    Long postId = ((Number) id).longValue();
                    getCachedPost(postId).ifPresent(trendingPosts::add);
                }
            }

            log.debug("Retrieved {} trending posts", trendingPosts.size());
            return trendingPosts;
        } catch (Exception e) {
            log.error("Failed to get trending posts", e);
            return Collections.emptyList();
        }
    }

    /**
     * Batch операция - кэширует несколько постов одновременно
     *
     * @param posts список постов
     */
    public void cachePosts(List<Post> posts) {
        if (posts == null || posts.isEmpty()) {
            return;
        }

        try {
            Map<String, Post> postMap = new HashMap<>();

            for (Post post : posts) {
                if (post != null && post.getId() != null) {
                    String key = keyPrefix + post.getId();
                    postMap.put(key, post);
                }
            }

            if (!postMap.isEmpty()) {
                postRedisTemplate.opsForValue().multiSet(postMap);

                for (String key : postMap.keySet()) {
                    postRedisTemplate.expire(key, Duration.ofHours(ttlHours));
                }

                log.debug("Batch cached {} posts", postMap.size());
            }
        } catch (Exception e) {
            log.error("Failed to batch cache posts", e);
        }
    }

    /**
     * Проверяет, закэширован ли пост
     *
     * @param postId ID поста
     * @return true если пост в кэше
     */
    public boolean isPostCached(Long postId) {
        if (postId == null) {
            return false;
        }

        try {
            String key = keyPrefix + postId;
            return Boolean.TRUE.equals(postRedisTemplate.hasKey(key));
        } catch (Exception e) {
            log.error("Failed to check if post ID: {} is cached", postId, e);
            return false;
        }
    }

    /**
     * Получает оставшееся время жизни поста в кэше
     *
     * @param postId ID поста
     * @return время в секундах или -1 если ключ не существует
     */
    public long getPostCacheTtl(Long postId) {
        if (postId == null) {
            return -1;
        }

        try {
            String key = keyPrefix + postId;
            return postRedisTemplate.getExpire(key, TimeUnit.SECONDS);
        } catch (Exception e) {
            log.error("Failed to get TTL for post ID: {}", postId, e);
            return -1;
        }
    }

    public List<Long> getUserFeedIds(Long userId, Long afterPostId, int limit) {
        String key = userFeedPrefix + userId;
        Object cached = postRedisTemplate.opsForValue().get(key);

        if (!(cached instanceof List<?> ids)) {
            return Collections.emptyList();
        }

        List<Long> allIds = ids.stream()
                .filter(i -> i instanceof Number)
                .map(i -> ((Number) i).longValue())
                .toList();

        if (afterPostId == null) {
            return allIds.stream().limit(limit).toList();
        }

        int index = allIds.indexOf(afterPostId);
        if (index == -1 || index + 1 >= allIds.size()) {
            return Collections.emptyList();
        }

        return allIds.subList(index + 1, Math.min(index + 1 + limit, allIds.size()));
    }

    /**
     * Инкрементирует количество просмотров поста, если он есть в кэше
     *
     * @param postId ID поста
     * @return новое значение просмотров или -1 если поста нет в кэше
     */
    public long incrementPostViews(Long postId) {
        if (postId == null) {
            return -1;
        }

        String key = keyPrefix + postId;

        try {
            Boolean exists = postRedisTemplate.hasKey(key);
            if (Boolean.TRUE.equals(exists)) {
                Long newCount = postRedisTemplate.opsForHash().increment(key, "views", 1);
                log.debug("Incremented views for post ID {} → {}", postId, newCount);
                return newCount != null ? newCount : -1;
            } else {
                log.debug("Post ID {} not found in Redis, skipping view increment", postId);
                return -1;
            }
        } catch (Exception e) {
            log.error("Failed to increment views for post ID {}", postId, e);
            return -1;
        }
    }

    /**
     * Получает текущее количество просмотров поста
     *
     * @param postId ID поста
     * @return количество просмотров или 0, если поста нет в кэше
     */
    public long getPostViews(Long postId) {
        if (postId == null) {
            return 0;
        }

        String key = keyPrefix + postId;

        try {
            Object views = postRedisTemplate.opsForHash().get(key, "views");
            if (views instanceof Number number) {
                return number.longValue();
            }
            return 0;
        } catch (Exception e) {
            log.error("Failed to get views for post ID {}", postId, e);
            return 0;
        }
    }

    /**
     * Очищает весь кэш постов
     */
    public void clearAllPostCache() {
        try {
            Set<String> keys = postRedisTemplate.keys(keyPrefix + "*");
            if (keys != null && !keys.isEmpty()) {
                postRedisTemplate.delete(keys);
                log.info("Cleared {} post cache entries", keys.size());
            }
        } catch (Exception e) {
            log.error("Failed to clear post cache", e);
        }
    }
}