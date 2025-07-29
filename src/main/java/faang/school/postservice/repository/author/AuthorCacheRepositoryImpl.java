package faang.school.postservice.repository.author;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.config.redis.RedisProperties;
import faang.school.postservice.dto.user.UserCacheDto;
import faang.school.postservice.dto.user.UserDto;
import faang.school.postservice.mapper.UserMapper;
import faang.school.postservice.repository.AuthorCacheRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

@Slf4j
@Repository
@RequiredArgsConstructor
public class AuthorCacheRepositoryImpl implements AuthorCacheRepository {
    private final RedisTemplate<String, Object> redisTemplate;
    private final RedisProperties redisProperties;
    private final UserServiceClient userService;
    private final UserMapper userMapper;

    @Override
    public void set(long authorId) {
        try {
            UserDto user = userService.getUser(authorId);
            redisTemplate.opsForHash().put(redisProperties.getCacheNames().authors(),
                    String.valueOf(authorId), userMapper.dtoToCache(user));
            redisTemplate.expire(redisProperties.getCacheNames().authors(), redisProperties.getCacheDuration().authors());
        } catch (Exception e) {
            log.error("Unexpected exception on author caching [{}].", authorId, e);
        }
    }

    @Override
    public UserDto get(long authorId) {
        HashOperations<String, String, UserCacheDto> hashOps = redisTemplate.opsForHash();

        return userMapper.cacheToDto(hashOps.get(redisProperties.getCacheNames().authors(), String.valueOf(authorId)));
    }
}
