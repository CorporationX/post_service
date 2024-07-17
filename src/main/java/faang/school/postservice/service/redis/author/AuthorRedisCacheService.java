package faang.school.postservice.service.redis.author;

import faang.school.postservice.model.redis.AuthorRedisCache;

public interface AuthorRedisCacheService {

    AuthorRedisCache save(AuthorRedisCache entity);
}
