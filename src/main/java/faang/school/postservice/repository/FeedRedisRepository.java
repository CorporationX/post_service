package faang.school.postservice.repository;

import faang.school.postservice.dto.feed.PostFollowersEvent;
import faang.school.postservice.dto.redis.FeedRedisDto;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.stereotype.Repository;

import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

@Repository
@RequiredArgsConstructor
public class FeedRedisRepository {

    private final RedisTemplate<String, Object> redisTemplate;
    private final RedisScript<Boolean> addAndTrimZSetScript;
    private final RedisScript<Boolean> addMultipleAndTrimZSetScript;

    @Value("${spring.data.redis.object-cache-options.posts-count}")
    private int postsCount;

    public void addPostsForFollowersToFeed(PostFollowersEvent postDto) {
        long score = postDto.publishedAt().toInstant(ZoneOffset.UTC).getEpochSecond();
        String value = postDto.postId().toString();

        postDto.followerIds().forEach(followerId -> {
            String key = "feed:" + followerId;

            redisTemplate.execute(
                    addAndTrimZSetScript,
                    Collections.singletonList(key),
                    value,
                    String.valueOf(score),
                    String.valueOf(postsCount)
            );
        });
    }

    public void addFeedForUser(Long userId, List<FeedRedisDto> feedDtoList) {
        String key = "feed:" + userId;
        List<String> args = new ArrayList<>();

        feedDtoList.forEach(dto -> {
            args.add(dto.postId().toString());
            long score = dto.publishedAt().toInstant(ZoneOffset.UTC).getEpochSecond();
            args.add(String.valueOf(score));
        });
        args.add(String.valueOf(postsCount));

        redisTemplate.execute(
                addMultipleAndTrimZSetScript,
                Collections.singletonList(key),
                (Object) args.toArray(new String[0])
        );
    }

    public List<Long> getFeed(Long userId, int limit, Long postId) {
        String key = "feed:" + userId;

        Double lastScore = redisTemplate.opsForZSet().score(key, postId.toString());
        if (lastScore == null) {
            return List.of();
        }

        Set<Object> raw = redisTemplate.opsForZSet()
                .reverseRangeByScore(key, lastScore - 1, 0, 0, limit);

        return raw != null
                ? raw.stream().map(object -> Long.parseLong(object.toString())).toList()
                : List.of();
    }
}
