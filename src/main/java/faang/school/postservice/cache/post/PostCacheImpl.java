package faang.school.postservice.cache.post;

import faang.school.postservice.config.properties.cache.post.PostCacheProperties;
import faang.school.postservice.dto.cache.PostCacheDto;
import faang.school.postservice.mapper.feed.FeedMapper;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SessionCallback;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

@Component
@RequiredArgsConstructor
@Slf4j
public class PostCacheImpl implements PostCache {

    private final RedisTemplate<String, PostCacheDto> redisTemplate;
    private final PostCacheProperties properties;
    private final PostRepository postRepository;
    private final FeedMapper feedMapper;

    private String buildKey(Long postId) {
        return properties.keyPrefix() + postId;
    }

    @Override
    public void put(PostCacheDto postCacheDto) {
        try {
            String key = buildKey(postCacheDto.id());
            redisTemplate.opsForValue().set(key, postCacheDto);
            long ttlSeconds = properties.ttl().toSeconds();
            if (ttlSeconds > 0) {
                redisTemplate.expire(key, ttlSeconds, TimeUnit.SECONDS);
            }
        } catch (Exception e) {
            log.warn("Redis put failed id={}", postCacheDto.id(), e);
        }
    }

    @Override
    public void putAll(List<PostCacheDto> posts) {
        if (posts == null || posts.isEmpty()) {
            return;
        }
        try {
            long ttlSeconds = properties.ttl().toSeconds();
            redisTemplate.executePipelined(new SessionCallback<>() {
                @Override
                public Object execute(RedisOperations operations) {
                    for (PostCacheDto p : posts) {
                        if (p == null || p.id() == null) {
                            continue;
                        }
                        String key = buildKey(p.id());
                        operations.opsForValue().set(key, p);
                        if (ttlSeconds > 0) {
                            operations.expire(key, ttlSeconds, TimeUnit.SECONDS);
                        }
                    }
                    return null;
                }
            });
        } catch (Exception e) {
            log.warn("Redis putAll failed count={}", posts.size(), e);
        }
    }

    @Override
    public Optional<PostCacheDto> get(Long postId) {
        try {
            return Optional.ofNullable(redisTemplate.opsForValue().get(buildKey(postId)));
        } catch (Exception e) {
            log.warn("Redis get failed id={}", postId, e);
            return Optional.empty();
        }
    }

    @Override
    public List<PostCacheDto> getAll(List<Long> postIds) {
        if (postIds == null || postIds.isEmpty()) {
            return List.of();
        }

        try {
            List<String> keys = postIds.stream().map(this::buildKey).toList();
            List<PostCacheDto> cachedPosts = redisTemplate.opsForValue().multiGet(keys);
            if (cachedPosts == null) {
                cachedPosts = Collections.nCopies(postIds.size(), null);
            }

            List<Long> missingIds = new ArrayList<>();
            for (int i = 0; i < postIds.size(); i++) {
                if (cachedPosts.get(i) == null) {
                    missingIds.add(postIds.get(i));
                }
            }

            List<PostCacheDto> fetchedPosts = List.of();
            if (!missingIds.isEmpty()) {
                List<Post> postsFromDb = postRepository.getByIds(missingIds);
                fetchedPosts = feedMapper.toPostCacheEntryList(postsFromDb);
                putAll(fetchedPosts);
            }

            List<PostCacheDto> result = new ArrayList<>(postIds.size());
            int fetchIndex = 0;
            for (int i = 0; i < postIds.size(); i++) {
                PostCacheDto post = cachedPosts.get(i);
                if (post != null) {
                    result.add(post);
                } else {
                    result.add(fetchedPosts.get(fetchIndex++));
                }
            }
            return result;
        } catch (Exception e) {
            log.warn("Redis getAll failed ids={}", postIds, e);
            return List.of();
        }
    }

    @Override
    public void delete(Long postId) {
        try {
            redisTemplate.delete(buildKey(postId));
        } catch (Exception e) {
            log.warn("Redis delete failed id={}", postId, e);
        }
    }
}
