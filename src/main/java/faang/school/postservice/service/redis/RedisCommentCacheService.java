package faang.school.postservice.service.redis;

import faang.school.postservice.dto.redis.RedisCommentDto;
import faang.school.postservice.mapper.redis.RedisCommentMapper;
import faang.school.postservice.mapper.redis.RedisPostMapper;
import faang.school.postservice.model.Comment;
import faang.school.postservice.model.redis.RedisPost;
import faang.school.postservice.repository.redis.RedisPostRepository;
import faang.school.postservice.service.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisKeyValueTemplate;

@RequiredArgsConstructor
public class RedisCommentCacheService {
    private final RedisPostRepository redisPostRepository;
    private final RedisKeyValueTemplate redisKeyValueTemplate;
    private final PostService postService;
    private final RedisPostMapper redisPostMapper;
    private final RedisCommentMapper redisCommentMapper;
    @Value("${feed.batch_size.comment}")
    private int commentsBatch;

    public void addCommentToPost(Comment comment) {
        RedisCommentDto redisCommentDto = redisCommentMapper.toRedisDto(comment);
        long postId = redisCommentDto.getPostId();
        RedisPost redisPost = redisPostRepository.findById(postId)
                .orElseGet(() -> {
                    RedisPost redisPostToSave = redisPostMapper.toEntity(postService.getPost(postId));
                    return redisPostRepository.save(redisPostToSave);
                });

        redisPost.addComment(redisCommentDto);

        if (redisPost.getComments().size() > commentsBatch) {
            redisPost.removeLastRedisCommentDto();
        }

        redisKeyValueTemplate.update(redisPost);
    }

    public void deleteCommentFromPost(long postId, long commentId) {
        redisPostRepository.findById(postId).ifPresent(
                redisPost -> redisPost.removeComment(commentId));
    }
}