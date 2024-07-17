package faang.school.postservice.service.redis.post;

import faang.school.postservice.model.redis.CommentRedisCache;
import faang.school.postservice.model.redis.PostRedisCache;

public interface PostRedisCacheService {

    PostRedisCache save(PostRedisCache entity);

    void addCommentToPost(CommentRedisCache comment);

    void deleteById(long postId);
}
