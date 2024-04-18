package faang.school.postservice.service.redis;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.dto.UserDto;
import faang.school.postservice.mapper.RedisUserMapper;
import faang.school.postservice.model.redis.RedisUser;
import faang.school.postservice.repository.redis.RedisUserRepository;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisKeyValueTemplate;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RedisUserCacheService {
    private final RedisUserRepository redisUserRepository;
    private final UserServiceClient userServiceClient;
    private final RedisUserMapper redisUserMapper;
    private final RedisKeyValueTemplate redisTemplate;
    @Value("${spring.data.redis.cache.ttl.user}")
    private int userTtl;

    @Retryable(retryFor = {FeignException.class}, maxAttempts = 5, backoff =
    @Backoff(delay = 500, multiplier = 3))
    public void saveUser(long userId) {
        UserDto userDto = userServiceClient.getUser(userId);
        RedisUser redisUser = redisUserMapper.toEntity(userDto);
        redisUser.setTtl(userTtl);

        redisUserRepository.findById(userId)
                .ifPresentOrElse(
                        (user) -> redisTemplate.update(redisUser),
                        () -> redisUserRepository.save(redisUser)
                );
    }
}
