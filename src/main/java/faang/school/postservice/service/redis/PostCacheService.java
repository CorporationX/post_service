package faang.school.postservice.service.redis;

import faang.school.postservice.mapper.PostMapper;
import faang.school.postservice.model.Post;
import faang.school.postservice.model.redis.PostForCache;
import faang.school.postservice.repository.redis.PostCacheRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PostCacheService {

    private final PostCacheRepository postCacheRepository;
    private final PostMapper postMapper;

    public void save(Post postEntity) {
        PostForCache postForSaveToCache = postMapper.toPostForCache(postEntity);
        postCacheRepository.save(postForSaveToCache);
    }
}