package faang.school.postservice.service.feed;

import faang.school.postservice.dto.redis.PostRedis;
import faang.school.postservice.repository.feed.FeedRedisRepository;
import faang.school.postservice.repository.post.PostRedisRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class FeedServiceImpl implements FeedService {

    private final FeedRedisRepository redisRepository;
    private final PostRedisRepository postRedisRepository;

    @Override
    public List<PostRedis> getPosts(Long userId) {
        Set<Object> postsIds = redisRepository.getFirst20Posts(userId);
        return postsIds.stream()
                .map(Object::toString)
                .map(id -> postRedisRepository.findById(id)
                        .orElseThrow(() -> new EntityNotFoundException(String.format("Post with id %s not found", id))))
                .toList();
    }
}
