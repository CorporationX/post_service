package faang.school.postservice.newsfeed.repository;

import faang.school.postservice.newsfeed.dto.FeedDto;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class RedisFeedRepository implements FeedRepository {
    private final RedisTemplate<String, String> redisTemplate;

    @Override

    public FeedDto getFeedByUserId(Long userId) {
        return null;
    }

    @Override
    public void addPostToFeed(Long userId, Long PostId) {
        redisTemplate.opsForZSet().add()
    }
}
