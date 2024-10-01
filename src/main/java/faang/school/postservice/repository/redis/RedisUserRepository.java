package faang.school.postservice.repository.redis;

import faang.school.postservice.dto.post.CachedPostDto;
import faang.school.postservice.dto.user.CachedUserDto;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public class RedisUserRepository extends AbstractRedisRepository<CachedUserDto> {
    private static final String CACHE_PREFIX = "user:";

    public RedisUserRepository(RedisTemplate<String, Object> redisTemplate,
                               @Value("${spring.data.redis.TTL}") long timeToLive) {
        super(CACHE_PREFIX, redisTemplate, timeToLive);
    }

    @Override
    public void saveAll(List<CachedUserDto> userDtos) {
        for (CachedUserDto userDto : userDtos) {
            save(userDto.getId(), userDto);
        }
    }
}
