package faang.school.postservice.redis.cache.service.comment_post;

import faang.school.postservice.redis.cache.entity.CommentCache;

public interface CommentPostCacheService {

    void tryDeleteCommentFromPost(CommentCache comment);

    void tryAddCommentToPost(CommentCache comment);
}
