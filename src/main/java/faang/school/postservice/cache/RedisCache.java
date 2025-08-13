package faang.school.postservice.cache;

import faang.school.postservice.dto.feed.CommentFeedDto;
import faang.school.postservice.dto.feed.UserFeedDto;
import faang.school.postservice.dto.redis.RedisPostDto;

import java.time.LocalDateTime;
import java.util.List;

public interface RedisCache {

    void putUser(UserFeedDto user);

    void putPost(RedisPostDto post);

    void putFeedForUserBatch(List<Long> userId, Long postId, LocalDateTime postCreatedAt);

    void putPostsBatch(List<RedisPostDto> posts);

    void putComment(CommentFeedDto dto);

    boolean updatePost(long postId, String event);

    List<Long> getFeed(Long userId, long start, long end);

    UserFeedDto getUser(Long userId);

    RedisPostDto getPost(Long postId);

    List<RedisPostDto> getPostsBatch(List<Long> postIds);

    List<CommentFeedDto> getComments(Long postId);

    void putFeedForSubscribers(Long user, List<Long> followerIds);

    Long getPostRank(long userId, Long postId);
}
