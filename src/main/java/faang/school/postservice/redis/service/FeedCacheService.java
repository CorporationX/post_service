package faang.school.postservice.redis.service;

import faang.school.postservice.redis.mapper.PostCacheMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class FeedCacheService {

    private final RedisTemplate<String, Object> redisTemplate;
    private final PostCacheService postCacheService;
    private final PostCacheMapper postCacheMapper;


}
