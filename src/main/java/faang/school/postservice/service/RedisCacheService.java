package faang.school.postservice.service;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.dto.PostDto;
import faang.school.postservice.dto.UserDto;
import faang.school.postservice.mapper.RedisPostMapper;
import faang.school.postservice.mapper.RedisUserMapper;
import faang.school.postservice.model.redis.RedisPost;
import faang.school.postservice.model.redis.RedisUser;
import faang.school.postservice.repository.redis.RedisPostRepository;
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
public class RedisCacheService {
    private final RedisUserRepository redisUserRepository;
    private final RedisPostRepository redisPostRepository;
    private final UserServiceClient userServiceClient;
    private final RedisUserMapper redisUserMapper;
    private final RedisPostMapper redisPostMapper;
    private final RedisKeyValueTemplate redisTemplate;
    @Value("${spring.data.redis.cache.ttl.post}")
    private int postTtl;
    @Value("${spring.data.redis.cache.ttl.user}")
    private int userTtl;

    public void savePost(PostDto postDto) {
        saveUser(postDto.getAuthorId());

        RedisPost redisPost = redisPostMapper.toEntity(postDto);
        redisPost.setTtl(postTtl);

        redisPostRepository.findById(postDto.getId())
                .ifPresentOrElse(
                        (post) -> redisTemplate.update(redisPost),
                        () -> redisPostRepository.save(redisPost)
                );
    }

    public void deletePostById(long postId) {
        redisPostRepository.deleteById(postId);
    }

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
