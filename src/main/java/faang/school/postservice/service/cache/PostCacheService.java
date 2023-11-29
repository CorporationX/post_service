package faang.school.postservice.service.cache;

import faang.school.postservice.dto.redis.cash.CommentCache;
import faang.school.postservice.dto.redis.cash.PostCache;
import faang.school.postservice.repository.redis.PostCacheRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class PostCacheService {

    private final PostCacheRepository postCacheRepository;

    @Value("${news_feed.redis.cache.post_ttl}")
    private Long timeout;
    @Value("${news_feed.redis.cache.key_name_post}")
    public String keyName;

    @Value("${news_feed.redis.cache.comment_size}")
    private Integer commentSize;

    public PostCache get(Long id) {
        String cacheId = keyName + id;
        return postCacheRepository.get(cacheId, timeout);
    }

    public void save(PostCache postCache) {
        postCache.setId(keyName + postCache.getId());
        postCache.setNumElements(commentSize);
        postCacheRepository.save(postCache, timeout);
    }

    public void delete(Long postId) {
        String cacheId = keyName + postId;
        postCacheRepository.delete(cacheId);
    }

    public void update(PostCache postCache) {
        postCache.setId(keyName + postCache.getId());
        postCacheRepository.update(postCache, timeout);
    }

    public void addComment(CommentCache commentCache) {
        PostCache postCache = get(commentCache.getPostId());
        postCache.addComment(commentCache);
        System.out.println("addComment " + postCache);
        postCacheRepository.update(postCache, timeout);
    }
}