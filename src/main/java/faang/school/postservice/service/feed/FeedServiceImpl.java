package faang.school.postservice.service.feed;

import faang.school.postservice.dto.post.PostDto;
import faang.school.postservice.model.redis.CachedPost;
import faang.school.postservice.repository.PostRepository;
import faang.school.postservice.repository.redis.RedisFeedRepository;
import faang.school.postservice.repository.redis.RedisPostRepository;
import faang.school.postservice.repository.redis.RedisUserRepository;
import faang.school.postservice.service.FeedService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class FeedServiceImpl implements FeedService {
    private final PostRepository postRepository;
    private final RedisFeedRepository redisFeedRepository;
    private final RedisPostRepository redisPostRepository;
    private final RedisUserRepository redisUserRepository;

    @Override
    public Set<PostDto> getFeed(Long userId, Long postId) {
        Set<String> postIds = redisFeedRepository.getFeed(userId);
        Set<CachedPost> posts = postIds.stream()
                .map(id -> redisPostRepository.getPost(Long.valueOf(id)))
                .collect(Collectors.toSet());




        return Set.of();
    }
}
