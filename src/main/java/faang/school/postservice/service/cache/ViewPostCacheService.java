package faang.school.postservice.service.cache;

import faang.school.postservice.repository.cache.CacheRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ViewPostCacheService implements SingleCacheService<Long, Long> {

    private final CacheRepository<Long> cacheRepository;

    @Override
    public void save(Long postId, Long viewAuthorId) {
        String viewKey = createKey(postId);
        cacheRepository.incrementAndGet(viewKey);
    }

    @Override
    public Long get(Long postId) {
        return cacheRepository.get(createKey(postId), Long.class)
                .orElse(0L);
    }

    private static String createKey(Long postId) {
        return postId + "::count_post_view";
    }
}
