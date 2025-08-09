package faang.school.postservice.cache;

import faang.school.postservice.dto.feed.UserFeedDto;
import faang.school.postservice.dto.kafka.KafkaCommentEventDto;
import faang.school.postservice.dto.redis.RedisPostDto;
import faang.school.postservice.dto.redis.RedisUserDto;
import org.springframework.scheduling.annotation.Async;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

public interface RedisCache {

    void putUser(UserFeedDto user);

    void putPost(RedisPostDto post);

    void putFeed(Long userId, Long postId, LocalDateTime postCreatedAt);

    void putFeedForUserBatch(List<Long> userId, Long postId, LocalDateTime postCreatedAt);

    void putPostsBatch(List<RedisPostDto> posts);

    void updateComment(long postId);

    void putComment(KafkaCommentEventDto dto);

    boolean updatePost(long postId, String event);

    Set<Long> getFeed(Long userId, long start, long end);

    RedisUserDto getUser(Long userId);

    RedisPostDto getPost(Long postId);

    List<RedisPostDto> getPostsBatch(Set<Long> postIds);

    List<Long> getComments(Long postId);

    @Async("redisTaskExecutor")
    void putFeedForSubscribers(Long user, List<Long> followerIds);
}
