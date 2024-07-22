package faang.school.postservice.redis.cache.service.comment;

import faang.school.postservice.redis.cache.entity.CommentCache;

public interface CommentCacheService {

    void save(CommentCache entity);

    void incrementLikes(long commentId);

    void decrementLikes(long commentId);

    void deleteById(long postId);
}
