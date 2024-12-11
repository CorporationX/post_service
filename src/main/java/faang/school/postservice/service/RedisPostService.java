package faang.school.postservice.service;

import faang.school.postservice.model.dto.redis.cache.RedisPostDto;
import faang.school.postservice.model.event.kafka.CommentSentKafkaEvent;

import java.util.List;

public interface RedisPostService {
    void savePostIfNotExists(RedisPostDto postDto);

    RedisPostDto getPost(Long postId);

    void addComment(Long postId, Long commentId, Long  commentAuthorId, String commentContent);

    void incrementLikesWithTransaction(Long postId, Long likeId);

    void savePost(RedisPostDto postDto);

    void savePosts(List<RedisPostDto> postDtos);

    void incrementPostViewsWithTransaction(Long postId, Long viewerId, String viewDateTime);
}
