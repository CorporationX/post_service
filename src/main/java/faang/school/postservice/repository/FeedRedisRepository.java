package faang.school.postservice.repository;

import faang.school.postservice.dto.feed.PostFollowersEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.stereotype.Repository;

import java.time.ZoneOffset;
import java.util.Collections;
import java.util.List;
import java.util.Set;

@Repository
@RequiredArgsConstructor
public class FeedRedisRepository {

    private final RedisTemplate<String, Object> redisTemplate;
    private final RedisScript<Boolean> addAndTrimZSetScript;

    @Value("${spring.data.redis.object-cache-options.posts-count}")
    private int postsCount;

    public void addPostsForFollowersToFeed(PostFollowersEvent postDto) {
        String key = "feed:" + postDto.authorId();
        String value = postDto.postId().toString();
        long score = postDto.publishedAt().toInstant(ZoneOffset.UTC).getEpochSecond();

        redisTemplate.execute(
                addAndTrimZSetScript,
                Collections.singletonList(key),
                value,
                String.valueOf(score),
                String.valueOf(postsCount)
        );
    }

    public List<Long> getFeed(Long userId, int limit) {
        String key = "feed:" + userId;
        Set<Object> raw = redisTemplate.opsForZSet().reverseRange(key, 0, limit - 1);
        return raw != null
                ? raw.stream().map(object -> Long.parseLong(object.toString())).toList()
                : null;
    }
}
