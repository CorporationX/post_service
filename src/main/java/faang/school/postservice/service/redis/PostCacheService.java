package faang.school.postservice.service.redis;

import faang.school.postservice.mapper.PostMapper;
import faang.school.postservice.model.Post;
import faang.school.postservice.model.redis.PostForCache;
import faang.school.postservice.repository.redis.PostCacheRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.StreamSupport;

@Service
@RequiredArgsConstructor
public class PostCacheService {

    private final PostCacheRepository postCacheRepository;
    private final PostMapper postMapper;

    public void save(Post post) {
        PostForCache postForSaveToCache = postMapper.toPostForCache(post);
        postCacheRepository.save(postForSaveToCache);
    }

    public List<PostForCache> getAllPostsByIds(List<Long> ids) {
        return StreamSupport.stream(postCacheRepository.findAllById(ids).spliterator(), false).toList();
    }
}