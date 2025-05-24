package faang.school.postservice.repository;

import faang.school.postservice.dto.PostResponseDto;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CachePut;
import org.springframework.stereotype.Repository;

@Repository
@CacheConfig(cacheNames = "posts")
public class RedisPostRepository {

    @CachePut(key = "#postDto.id")
    public PostResponseDto savePost(PostResponseDto postDto) {
        return postDto;
    }
}
