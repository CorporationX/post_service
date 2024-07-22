package faang.school.postservice.redis.cache.service.post;

import faang.school.postservice.redis.cache.entity.PostCache;

public interface PostCacheService {

    void save(PostCache entity);

    void incrementLikes(long postId);

    void incrementViews(long postId);

    void decrementLikes(long postId);

    void deleteById(long postId);
}
