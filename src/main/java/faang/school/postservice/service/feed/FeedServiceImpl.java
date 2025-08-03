package faang.school.postservice.service.feed;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.config.context.UserContext;
import faang.school.postservice.config.redis.RedisProperties;
import faang.school.postservice.dto.post.PostOutputDto;
import faang.school.postservice.dto.user.UserDto;
import faang.school.postservice.mapper.PostMapper;
import faang.school.postservice.repository.FeedCacheRepository;
import faang.school.postservice.repository.PostCacheRepository;
import faang.school.postservice.repository.PostRepository;
import faang.school.postservice.service.FeedService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class FeedServiceImpl implements FeedService {
    private final RedisProperties redisProperties;
    private final FeedCacheRepository feedCacheRepository;
    private final PostCacheRepository postCacheRepository;
    private final PostRepository postRepository;
    private final UserServiceClient userServiceCLient;
    private final UserContext userContext;
    private final PostMapper postMapper;

    @Override
    public List<PostOutputDto> getFeed(Integer offset) {
        List<PostOutputDto> cache = getFromCache(offset);
        if (!cache.isEmpty()) {
            return cache;
        }

        return postRepository.findPageForAuthors(
                        userServiceCLient.getFollowers(userContext.getUserId()).stream()
                                .map(UserDto::id)
                                .toList(), offset, redisProperties.getMaxFeedSize()
                ).stream()
                .map(postMapper::toPostDto)
                .toList();
    }

    private List<PostOutputDto> getFromCache(int offset) {
        Optional<Set<Long>> cache = feedCacheRepository.get(userContext.getUserId(), offset);
        return cache.map(longs -> longs.stream()
                .map((postId) -> postCacheRepository.get(postId)
                        .orElse(postMapper.toCacheDto(postRepository.findById(postId).orElse(null))))
                .filter(Objects::nonNull)
                .map(postMapper::toPostOutputDto)
                .toList()
        ).orElseGet(List::of);
    }
}
