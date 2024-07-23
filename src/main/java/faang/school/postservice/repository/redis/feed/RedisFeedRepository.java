package faang.school.postservice.repository.redis.feed;

import faang.school.postservice.model.redis.RedisFeed;

public interface RedisFeedRepository {

    RedisFeed getById(Long id);

    void save(RedisFeed feedRedis);
}
