package faang.school.postservice.service.redis;

import faang.school.postservice.mapper.PostMapper;
import faang.school.postservice.model.Post;
import faang.school.postservice.model.cache.PostForCache;
import faang.school.postservice.repository.redis.PostCacheRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.TreeSet;

@Service
@RequiredArgsConstructor
@Slf4j
public class PostCacheService {

    private final PostCacheRepository postCacheRepository;
    private final PostMapper postMapper;

    @Value("${cache.last_comments_amount}")
    private long lastCommentsAmount;

    @Async
    public void save(Post post) {
        PostForCache postForSaveToCache = postMapper.toPostForCache(post);
        postCacheRepository.save(postForSaveToCache);
        log.info("Post with id = {} saved to cache", post.getId());
    }

    public TreeSet<PostForCache> getAllPostsByIds(Iterable<Long> ids) {
        return (TreeSet<PostForCache>) postCacheRepository.findAllById(ids);
        //return StreamSupport.stream(postCacheRepository.findAllById(ids).spliterator(), false).toList();
    }

    public void addCommentToPostInCache(Long postId, Long commentId) {
        Optional<PostForCache> postInCacheOptional = postCacheRepository.findById(postId);
        postInCacheOptional.ifPresent(postForCache -> {
            TreeSet<Long> lastCommentIds = postForCache.getLastCommentIds();
            if(lastCommentIds.size() >= lastCommentsAmount) {
                lastCommentIds.remove(lastCommentIds.first());
            }
            lastCommentIds.add(commentId);
        });
    }
}