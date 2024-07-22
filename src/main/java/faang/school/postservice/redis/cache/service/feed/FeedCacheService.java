package faang.school.postservice.redis.cache.service.feed;

import faang.school.postservice.redis.cache.entity.FeedCache;
import faang.school.postservice.redis.cache.entity.PostCache;

public interface FeedCacheService {

    void addPostToFeed(PostCache post, long subscriberId);

    void deletePostFromFeed(PostCache post, long subscriberId);

    FeedCache findByUserId(long userId);
}
