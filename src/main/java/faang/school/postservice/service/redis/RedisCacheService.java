package faang.school.postservice.service.redis;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.dto.UserDto;
import faang.school.postservice.mapper.PostMapper;
import faang.school.postservice.mapper.UserMapper;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.redis.PostRedisRepository;
import faang.school.postservice.repository.redis.UserRedisRepository;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RedisCacheService {
    private final int retryMaxAttempts = 5;
    private final int retryMultiplier = 5;
    private final int retryDelay = 1000;
    private final UserRedisRepository userRedisRepository;
    private final PostRedisRepository postRedisRepository;
    private final UserServiceClient userServiceClient;
    private final UserMapper userMapper;
    private final PostMapper postMapper;
    @Retryable(retryFor = {FeignException.class}, maxAttempts = retryMaxAttempts, backoff =
    @Backoff(delay = retryDelay, multiplier = retryMultiplier))
    public void userToCache(long ownerId) {
        UserDto userDto = userServiceClient.getUser(ownerId);
        userRedisRepository.save(userMapper.toRedisEntity(userDto));
    }

    public void postToCache(Post post) {
        postRedisRepository.save(postMapper.toRedisEntity(post));
    }

}
