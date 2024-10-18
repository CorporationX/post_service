package faang.school.postservice.service;

import faang.school.postservice.dto.post.PostDto;
import faang.school.postservice.repository.redisCache.RedisFeedRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class FeedService {
    private final RedisFeedRepository redisFeedRepository;

    public List<PostDto> getFeed(Long lastId) {
        return null;
    }
}
