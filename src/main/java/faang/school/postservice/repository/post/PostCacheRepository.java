package faang.school.postservice.repository.post;

import faang.school.postservice.dto.post.PostDto;
import faang.school.postservice.properties.PostCacheProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.time.ZoneOffset;

@Repository
@Slf4j
@RequiredArgsConstructor
public class PostCacheRepository {
    private final RedisTemplate<String, Object> redisTemplate;
    private final PostCacheProperties postCacheProperties;

    public PostDto cachePost(PostDto postDto) {
        long score = postDto.getCreatedAt().toEpochSecond(ZoneOffset.UTC);
        String postId = postDto.getId().toString();

        redisTemplate.opsForValue().set(postId, postDto, postCacheProperties.getLiveTime(),
                postCacheProperties.getTimeUnit());
        redisTemplate.opsForZSet().add(postCacheProperties.getSetKey(), postId, score);
        log.info("post saved to cache: {}", postDto);

        return postDto;
    }
}
