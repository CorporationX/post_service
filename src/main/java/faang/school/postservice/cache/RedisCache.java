package faang.school.postservice.cache;

import faang.school.postservice.dto.redis.RedisPostDto;
import faang.school.postservice.dto.redis.RedisUserDto;
import org.springframework.scheduling.annotation.Async;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

public interface RedisCache {

    void putUser(Long user);

    void putPost(RedisPostDto post);

    void putFeed(Long userId, Long postId, LocalDateTime postCreatedAt);

    Set<RedisPostDto> getFeed(Long userId, long start, long end);

    RedisUserDto getUser(Long userId);

    RedisPostDto getPost(Long postId);

    @Async("redisTaskExecutor")
    void putFeedForSubscribers(Long redisUserDto, List<Long> followerIds);
}
