package faang.school.postservice.service;

import faang.school.postservice.dto.PostDto;
import faang.school.postservice.repository.ResourceRepository;
import faang.school.postservice.repository.redis.PostRedisRepository;
import faang.school.postservice.repository.redis.UserRedisRepository;
import faang.school.postservice.service.redis.RedisCacheService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class FeedService {
    private final UserRedisRepository userRedisRepository;
    private final PostRedisRepository postRedisRepository;

    public List<PostDto> getFeed(Long postId) {
    }
}
