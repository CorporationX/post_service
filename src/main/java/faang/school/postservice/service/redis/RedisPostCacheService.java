package faang.school.postservice.service.redis;

import faang.school.postservice.dto.post.PostDto;
import faang.school.postservice.model.redis.RedisPost;

import java.util.Optional;

public interface RedisPostCacheService {

    RedisPost save(PostDto postDto);

    Optional<RedisPost> get(long postId);

    void deletePostById(long postId);
}
