
package faang.school.postservice.cache.author;

import faang.school.postservice.dto.user.UserDto;
import faang.school.postservice.service.post.UserFeignService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

@RequiredArgsConstructor
@Component
public class AuthorCacheImpl implements AuthorCache {
    private static final String KEY_AUTHOR_PATTERN = "authors:%s";
    @Value("${spring.data.redis.cache.authors.ttl}")
    private int ttl;

    private final RedisTemplate<String, UserDto> cache;
    private final UserFeignService userFeignService;
    private final ReentrantLock lock = new ReentrantLock();

    private String getKey(Long authorId) {
        return String.format(KEY_AUTHOR_PATTERN, authorId);
    }

    @Override
    public void set(UserDto dto) {
        String key = getKey(dto.id());
        cache.opsForValue().set(key, dto, ttl, TimeUnit.SECONDS);
    }

    @Override
    public UserDto get(long id) {
        String key = getKey(id);
        UserDto userDto = cache.opsForValue().get(key);
        if (userDto == null) {
            lock.lock();
            try {
                userDto = cache.opsForValue().get(key);
                if (userDto == null) {
                    userDto = userFeignService.getUserOrFail(id);
                    set(userDto);
                }
            } finally {
                lock.unlock();
            }
        }
        return userDto;
    }

}
