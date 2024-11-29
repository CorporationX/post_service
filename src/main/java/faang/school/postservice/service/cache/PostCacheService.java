package faang.school.postservice.service.cache;

import faang.school.postservice.dto.post.PostDto;
import faang.school.postservice.repository.cache.CacheRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class PostCacheService implements SingleCacheService<Long, PostDto>,
        MultiGetCacheService<Long, PostDto>,
        MultiSaveCacheService<PostDto> {

    private final CacheRepository<PostDto> cacheRepository;

    @Value("${server.cache.post.count-hours-time-to-live}")
    private int timeToLivePost;

    @Override
    public void save(Long postId, PostDto post) {
        String postKey = createKey(postId);
        Duration timeToLive = Duration.ofHours(timeToLivePost);
        cacheRepository.set(postKey, post, timeToLive);
    }

    @Override
    public PostDto get(Long postId) {
        return cacheRepository.get(createKey(postId), PostDto.class)
                .orElseGet(() -> {
                    log.warn("Cannot find post with id {} in cache", postId);
                    return null;
                });
    }

    @Override
    public List<PostDto> getAll(Long postId) {
        PostDto postDto = get(postId);
        return Collections.singletonList(postDto);
    }

    @Override
    public void saveAll(List<PostDto> posts) {
        Map<String, PostDto> postKeyByPost = posts.stream()
                .collect(Collectors.toMap(
                        post -> createKey(post.getId()),
                        post -> post)
                );
        cacheRepository.multiSetIfAbsent(postKeyByPost);
    }

    private static String createKey(Long postId) {
        return postId + "::post";
    }
}
