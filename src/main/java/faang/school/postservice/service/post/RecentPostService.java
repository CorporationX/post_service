package faang.school.postservice.service.post;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RecentPostService {
    private static final String ZSET_PREFIX = "user:%s:recent_posts";
    private static final int MAX_SIZE = 100;

    private final RedisTemplate<String, Object> redisTemplate;

    public void addPostToUser(Long userId, Long postId) {
        String key = String.format(ZSET_PREFIX, userId.toString());

        redisTemplate.opsForZSet().add(key, postId, System.currentTimeMillis());
        
        redisTemplate.opsForZSet().removeRange(key, 0, -MAX_SIZE - 1);
    }

    public List<Long> getRecentPosts(Long userId) {
        String key = String.format(ZSET_PREFIX, userId.toString());
        Set<Object> postIds = redisTemplate.opsForZSet().reverseRange(key, 0, MAX_SIZE - 1);
        if (postIds == null) {
            return List.of();
        }
        
        return postIds.stream()
            .map(id -> Long.parseLong(id.toString()))
            .collect(Collectors.toList());
    }
}
