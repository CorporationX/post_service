package faang.school.postservice.service;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.dto.client.UserDto;
import faang.school.postservice.mapper.redis.RedisUserMapper;
import faang.school.postservice.model.redis.RedisPost;
import faang.school.postservice.model.redis.RedisUser;
import faang.school.postservice.repository.redis.RedisPostRepository;
import faang.school.postservice.repository.redis.RedisUserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.data.redis.core.RedisKeyValueTemplate;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class RedisCacheService {

    private final RedisUserRepository redisUserRepository;
    private final RedisPostRepository redisPostRepository;
    private final RedisUserMapper redisUserMapper;
    private final RedisKeyValueTemplate keyValueTemplate;
    private final UserServiceClient userServiceClient;

    @Retryable(retryFor = OptimisticLockingFailureException.class, maxAttempts = 4, backoff = @Backoff(1000))
    public RedisUser saveRedisUser(RedisUser redisUser) {
        return redisUserRepository.save(redisUser);
    }

    @Retryable(retryFor = OptimisticLockingFailureException.class, maxAttempts = 4, backoff = @Backoff(1000))
    public RedisPost saveRedisPost(RedisPost redisPost) {
        return redisPostRepository.save(redisPost);
    }

    public RedisUser updatedRedisUSer(long userId, RedisUser redisUser) {
        return keyValueTemplate.update(userId, redisUser);
    }

    public RedisPost updateRedisPost(long postId, RedisPost redisPost) {
        return keyValueTemplate.update(postId, redisPost);
    }

    public Optional<RedisPost> findByRedisPostBy(long postId) {
        return redisPostRepository.findById(postId);
    }

    public Optional<RedisUser> findRedisUserBy(long userId) {
        return redisUserRepository.findById(userId);
    }

    public RedisUser findOrCacheRedisUser(long userId) {
        return redisUserRepository.findById(userId)
                .orElseGet(() -> findAndCacheUser(userId));
    }

    public RedisUser findAndCacheUser(long userId) {
        log.warn("User with ID: {} was not found in Redis. Attempting to retrieve from the database and cache in Redis.", userId);

        UserDto userDto = findUserBy(userId);
        RedisUser redisUser = mapUserToRedisUser(userDto);

        return saveRedisUser(redisUser);
    }

    public UserDto findUserBy(long userId) {
        log.info("Received request to find User with ID: {}", userId);
        return userServiceClient.getUser(userId);
    }

    public RedisUser mapUserToRedisUser(UserDto userDto) {
        RedisUser redisUser = redisUserMapper.toRedisUser(userDto);
        redisUser.setVersion(1);

        return redisUser;
    }
}
