package faang.school.postservice.redis.cache.service;

import faang.school.postservice.redis.cache.model.RedisPost;

public interface RedisPostService {
    void savePost(RedisPost post);

    RedisPost findByPostId(Long postId);
}
