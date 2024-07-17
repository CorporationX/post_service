package faang.school.postservice.service.redis.post;

import faang.school.postservice.model.redis.CommentRedisCache;
import faang.school.postservice.model.redis.PostRedisCache;

import java.util.concurrent.CompletableFuture;

public interface PostRedisCacheService {

    CompletableFuture<PostRedisCache> save(PostRedisCache entity);

    void addCommentToPost(CommentRedisCache comment);

    void deleteById(long postId);
}
