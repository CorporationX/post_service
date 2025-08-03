package faang.school.postservice.redis.cache.service;

import faang.school.postservice.redis.cache.model.RedisFeed;

public interface RedisFeedService {
    RedisFeed getFeed(Long userId, Long lastPostId, Integer pageSize);
}
