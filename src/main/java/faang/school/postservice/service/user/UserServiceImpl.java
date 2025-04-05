package faang.school.postservice.service.user;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.config.context.UserContext;
import faang.school.postservice.config.redis.RedisProperties;
import faang.school.postservice.dto.subscription.SubscriptionUserDto;
import faang.school.postservice.dto.user.UserDto;
import faang.school.postservice.dto.user.UserResponseDto;
import faang.school.postservice.exception.DataFetchException;
import faang.school.postservice.mapper.user.UserMapper;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserServiceClient userServiceClient;
    private final UserMapper userMapper;
    private final RedisTemplate <String, UserResponseDto> userRedisTemplate;
    private final RedisProperties redisProperties;
    private final UserContext userContext;

    @Override
    public UserDto getUserWithCache(long userId) {

        String cacheKey = redisProperties.cache().userCacheName() + userId;
        UserResponseDto cachedUser = userRedisTemplate.opsForValue().get(cacheKey);

        if (cachedUser != null) {
            log.info("Returning cached user for id: {}", userId);
            return userMapper.toUserDto(cachedUser);
        }

        log.info("Cache miss for user id: {}. Fetching from external service...", userId);
        try {
            UserResponseDto userResponseDto = userServiceClient.getUser(userId);

            if (userResponseDto != null) {

                userRedisTemplate.opsForValue().set(
                        cacheKey,
                        userResponseDto,
                        redisProperties.cache().userTtlMinutes(),
                        TimeUnit.MINUTES
                );

            }
            return userMapper.toUserDto(userResponseDto);
        } catch (FeignException e) {
            log.error("Error fetching data from external service for ID: {}", userId, e);
            throw new DataFetchException("Failed to fetch user with id: " + userId);
        }
    }

    @Async("asyncTaskExecutor")
    public List<SubscriptionUserDto> getFollowersAsync(long userId) {
        return getFollowers(userId);
    }

    @Cacheable("followers")
    public List<SubscriptionUserDto> getFollowers(long userId) {
        return userServiceClient.getFollowers(userId);
    }

    @Override
    public List<UserResponseDto> getAllUsers() {
        userContext.setUserId(0L); // предположим, что это некий системный пользователь для обращения к feign client
        return userServiceClient.getAllUsers();
    }
}
