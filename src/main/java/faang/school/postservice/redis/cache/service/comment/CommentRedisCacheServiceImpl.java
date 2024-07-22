package faang.school.postservice.redis.cache.service.comment;

import faang.school.postservice.redis.cache.entity.CommentRedisCache;
import faang.school.postservice.redis.cache.repository.CommentRedisRepository;
import faang.school.postservice.redis.cache.service.RedisOperations;
import faang.school.postservice.redis.cache.service.author.AuthorRedisCacheService;
import faang.school.postservice.redis.cache.service.comment_post.CommentPostRedisCacheService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;

@Slf4j
@Service
@RequiredArgsConstructor
@Async("commentsCacheTaskExecutor")
public class CommentRedisCacheServiceImpl implements CommentRedisCacheService {

    private final CommentRedisRepository commentRedisRepository;
    private final RedisOperations redisOperations;
    private  final CommentPostRedisCacheService commentPostRedisCacheService;
    private final AuthorRedisCacheService authorRedisCacheService;

    @Override
    public CompletableFuture<CommentRedisCache> save(CommentRedisCache entity) {

        entity = redisOperations.updateOrSave(commentRedisRepository, entity, entity.getId());

        log.info("Saved comment with id {} to cache: {}", entity.getId(), entity);

        authorRedisCacheService.save(entity.getAuthor());
        commentPostRedisCacheService.tryAddCommentToPost(entity);

        return CompletableFuture.completedFuture(entity);
    }

    @Override
    public void deleteById(long commentId) {

        CommentRedisCache comment = redisOperations.findById(commentRedisRepository, commentId).orElse(null);
        commentPostRedisCacheService.tryDeleteCommentFromPost(comment);
        redisOperations.deleteById(commentRedisRepository, commentId);
        log.info("Deleted comment with id={} from cache", commentId);
    }

    @Override
    public void incrementLikes(long commentId) {

        commentRedisRepository.findById(commentId).ifPresent(comment -> {
            comment.setLikesCount(comment.getLikesCount() + 1);
            commentPostRedisCacheService.tryAddCommentToPost(comment);
            redisOperations.updateOrSave(commentRedisRepository, comment, commentId);
        });
    }

    @Override
    public void decrementLikes(long commentId) {

        redisOperations.findById(commentRedisRepository, commentId).ifPresent(comment -> {
            comment.setLikesCount(comment.getLikesCount() - 1);
            commentPostRedisCacheService.tryAddCommentToPost(comment);
            redisOperations.updateOrSave(commentRedisRepository, comment, commentId);
        });
    }
}
