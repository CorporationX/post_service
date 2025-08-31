package faang.school.postservice.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PostLikeService {

    private final RedisTemplate<String, Long> postServiceRedisTemplate;

    public Long incrementLike(String postId) {
        return postServiceRedisTemplate
                .opsForValue()
                .increment("post:likes:" + postId);
    }
}