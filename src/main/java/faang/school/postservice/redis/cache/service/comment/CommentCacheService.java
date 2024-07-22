package faang.school.postservice.redis.cache.service.comment;

import faang.school.postservice.redis.cache.entity.CommentCache;

import java.util.concurrent.CompletableFuture;

public interface CommentCacheService {

    CompletableFuture<CommentCache> save(CommentCache entity);

    void incrementLikes(long commentId);

    void decrementLikes(long commentId);

    void deleteById(long postId);
}
