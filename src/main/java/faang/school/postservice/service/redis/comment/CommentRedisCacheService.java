package faang.school.postservice.service.redis.comment;

import faang.school.postservice.model.redis.CommentRedisCache;

public interface CommentRedisCacheService {

    CommentRedisCache save(CommentRedisCache entity);
}
