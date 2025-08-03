package faang.school.postservice.service.cache;

import faang.school.postservice.dto.post.PostEvent;
import faang.school.postservice.mapper.PostEventToCachePostMapper;
import faang.school.postservice.model.cache.CachePost;
import faang.school.postservice.repository.cache.CachePostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CachePostService {
    private final CachePostRepository cachePostRepository;
    private final PostEventToCachePostMapper postEventToCachePostMapper;

    public void savePost(PostEvent postEvent) {
        CachePost cachePost = postEventToCachePostMapper.toCachePost(postEvent);
        cachePostRepository.save(cachePost);
    }

    @Cacheable(cacheNames = "posts", key = "#id")
    public Optional<CachePost> getPostById(String id) {
        return cachePostRepository.findById(id);
    }

    public void deletePost(String id) {
        cachePostRepository.deleteById(id);
    }
}
