package faang.school.postservice.repository;

import faang.school.postservice.dto.post.RedisPostDto;
import faang.school.postservice.dto.user.UserCacheDto;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.time.Duration;

@Repository
@RequiredArgsConstructor
public class UserCacheRepository {
    private final RedisTemplate<String, Object> redisTemplate;
    @Value("${cache.post-ttl-duration}")
    private Duration ttlDuration;
    public void saveUserToCache(UserCacheDto userCacheDto) {
        String key = "user:" + userCacheDto.userId();
        redisTemplate.opsForValue().set(key, userCacheDto, ttlDuration);
    }
    public UserCacheDto getUserFromCache(String userId) {
        String key = "user:" + userId ;
        return (UserCacheDto) redisTemplate.opsForValue().get(key);
    }
}
