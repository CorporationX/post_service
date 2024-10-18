package faang.school.postservice.redis.cache.service.post;

import faang.school.postservice.redis.cache.entity.PostCache;

import java.util.List;

public interface PostCacheService {

    void save(PostCache entity);

    void incrementLikes(long postId);

    void incrementViews(long postId);

    void deleteById(long postId);

    List<PostCache> getPostCacheByIds(List<Long> postIds);
}
