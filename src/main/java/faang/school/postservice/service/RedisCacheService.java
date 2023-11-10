package faang.school.postservice.service;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.dto.client.UserDto;
import faang.school.postservice.mapper.redis.RedisPostMapper;
import faang.school.postservice.mapper.redis.RedisUserMapper;
import faang.school.postservice.model.Post;
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
    private final RedisPostMapper redisPostMapper;
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

    @Retryable(retryFor = OptimisticLockingFailureException.class, maxAttempts = 2, backoff = @Backoff(1000))
    public RedisUser updateRedisUser(long userId, RedisUser redisUser) {
        return keyValueTemplate.update(userId, redisUser);
    }

    @Retryable(retryFor = OptimisticLockingFailureException.class, maxAttempts = 2, backoff = @Backoff(1000))
    public RedisPost updateRedisPost(long postId, RedisPost redisPost) {
        return keyValueTemplate.update(postId, redisPost);
    }

    public void deleteRedisPost(long postId) {
        redisPostRepository.deleteById(postId);
    }

    public void deleteRedisUser(long userId) {
        redisUserRepository.deleteById(userId);
    }

    public Optional<RedisPost> findRedisPostBy(long postId) {
        return redisPostRepository.findById(postId);
    }

    public Optional<RedisUser> findRedisUserBy(long userId) {
        return redisUserRepository.findById(userId);
    }

    public boolean existsInRedisByPostId(long postId) {
        return redisPostRepository.existsById(postId);
    }

    public boolean existInRedisByUserId(long userId) {
        return redisUserRepository.existsById(userId);
    }

    public RedisUser findOrCacheRedisUser(long userId) {
        return redisUserRepository.findById(userId)
                .orElseGet(() -> findAndCacheUser(userId));
    }

    public RedisUser findAndCacheUser(long userId) {
        log.warn("User with ID: {} was not found in Redis. Attempting to retrieve from the database and cache in Redis.", userId);

        UserDto userDto = findUserBy(userId);
        return cacheUser(userDto);
    }

    public RedisUser updateOrCacheUser(UserDto userDto) {
        long userId = userDto.getId();

        Optional<RedisUser> optionalUser = findRedisUserBy(userId);

        if (optionalUser.isPresent()) {
            log.info("User with ID: {} exist in Redis. Attempting to update User", userId);

            RedisUser oldUser = optionalUser.get();
            return updateUser(oldUser, userDto);
        } else {
            log.warn("User with ID: {} not found in Redis. Caching...", userId);

            return cacheUser(userDto);
        }
    }

    public RedisPost updateOrCachePost(Post post) {
        long postId = post.getId();

        Optional<RedisPost> optionalPost = findRedisPostBy(postId);

        if (optionalPost.isPresent()) {
            log.info("Post with ID: {} exist in Redis. Attempting to update Post", postId);

            RedisPost oldPost = optionalPost.get();
            return updatePost(oldPost, post);
        } else {
            log.warn("Post with ID: {} not found in Redis. Caching...", postId);

            return cachePost(post);
        }
    }

    public RedisUser updateUser(RedisUser oldUser, UserDto newUser) {
        long userId = newUser.getId();
        oldUser.incrementUserVersion();

        RedisUser updatedUser = mapUserToRedisUser(newUser);
        updatedUser.setVersion(oldUser.getVersion());

        RedisUser redisUser = updateRedisUser(userId, updatedUser);
        log.info("User with ID: {}, was successfully updated in Redis", userId);

        return redisUser;
    }

    public RedisPost updatePost(RedisPost oldPost, Post newPost) {
        long postId = newPost.getId();
        oldPost.incrementPostVersion();

        RedisPost updatedPost = mapPostToRedisPost(newPost);
        updatedPost.setVersion(oldPost.getVersion());

        RedisPost redisPost = updateRedisPost(postId, updatedPost);
        log.info("Post with ID: {}, was successfully updated in Redis", postId);

        return redisPost;
    }

    public RedisUser cacheUser(UserDto userDto) {
        long userId = userDto.getId();
        log.warn("User with ID: {} doesn't exist in Redis. Attempting to cache User", userId);

        RedisUser userToSave = mapUserToRedisUserAndSetDefaultVersion(userDto);
        RedisUser redisUser = saveRedisUser(userToSave);
        log.info("User with ID: {} has been successfully save into a Redis", userId);

        return redisUser;
    }

    public RedisPost cachePost(Post post) {
        RedisPost newPost = mapPostToRedisPostAndSetDefaultVersion(post);

        RedisPost redisPost = saveRedisPost(newPost);
        log.info("Post with ID: {} has been successfully save into a Redis", post.getId());

        return redisPost;
    }

    public UserDto findUserBy(long userId) {
        log.info("Received request to find User with ID: {}", userId);
        return userServiceClient.getUser(userId);
    }

    public RedisUser mapUserToRedisUserAndSetDefaultVersion(UserDto userDto) {
        RedisUser redisUser = redisUserMapper.toRedisUser(userDto);
        redisUser.setVersion(1);

        return redisUser;
    }

    public RedisUser mapUserToRedisUser(UserDto userDto) {
        return redisUserMapper.toRedisUser(userDto);
    }

    public RedisPost mapPostToRedisPostAndSetDefaultVersion(Post post) {
        RedisPost redisPost = redisPostMapper.toRedisPost(post);
        redisPost.setVersion(1);

        return redisPost;
    }

    public RedisPost mapPostToRedisPost(Post post) {
        return redisPostMapper.toRedisPost(post);
    }
}
