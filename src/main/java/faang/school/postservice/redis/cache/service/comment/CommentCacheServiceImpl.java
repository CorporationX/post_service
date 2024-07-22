package faang.school.postservice.redis.cache.service.comment;

import faang.school.postservice.redis.cache.entity.CommentCache;
import faang.school.postservice.redis.cache.repository.CommentCacheRepository;
import faang.school.postservice.redis.cache.service.RedisOperations;
import faang.school.postservice.redis.cache.service.author.AuthorCacheService;
import faang.school.postservice.redis.cache.service.comment_post.CommentPostCacheService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;

@Slf4j
@Service
@RequiredArgsConstructor
@Async("commentsCacheTaskExecutor")
public class CommentCacheServiceImpl implements CommentCacheService {

    private final CommentCacheRepository commentCacheRepository;
    private final RedisOperations redisOperations;
    private final CommentPostCacheService commentPostCacheService;
    private final AuthorCacheService authorCacheService;

    @Override
    public CompletableFuture<CommentCache> save(CommentCache entity) {

        entity = redisOperations.updateOrSave(commentCacheRepository, entity, entity.getId());

        log.info("Saved comment with id {} to cache: {}", entity.getId(), entity);

        authorCacheService.save(entity.getAuthor());
        commentPostCacheService.tryAddCommentToPost(entity);

        return CompletableFuture.completedFuture(entity);
    }

    @Override
    public void deleteById(long commentId) {

        CommentCache comment = redisOperations.findById(commentCacheRepository, commentId).orElse(null);
        commentPostCacheService.tryDeleteCommentFromPost(comment);
        redisOperations.deleteById(commentCacheRepository, commentId);
        log.info("Deleted comment with id={} from cache", commentId);
    }

    @Override
    public void incrementLikes(long commentId) {

        commentCacheRepository.findById(commentId).ifPresent(comment -> {
            comment.setLikesCount(comment.getLikesCount() + 1);
            commentPostCacheService.tryAddCommentToPost(comment);
            redisOperations.updateOrSave(commentCacheRepository, comment, commentId);
        });
    }

    @Override
    public void decrementLikes(long commentId) {

        redisOperations.findById(commentCacheRepository, commentId).ifPresent(comment -> {
            comment.setLikesCount(comment.getLikesCount() - 1);
            commentPostCacheService.tryAddCommentToPost(comment);
            redisOperations.updateOrSave(commentCacheRepository, comment, commentId);
        });
    }
}
