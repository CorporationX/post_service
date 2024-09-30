package faang.school.postservice.service;

import faang.school.postservice.cache.dto.CachedPost;
import faang.school.postservice.exception.DataNotFoundException;
import faang.school.postservice.mapper.PostMapper;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.PostCacheRepository;
import faang.school.postservice.repository.PostRepository;
import jakarta.persistence.OptimisticLockException;
import lombok.RequiredArgsConstructor;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class FeedService {

    private final PostCacheRepository postCacheRepository;
    private final PostRepository postRepository;
    private final PostMapper postMapper;

    @Retryable(retryFor = OptimisticLockException.class, maxAttempts = 3, backoff = @Backoff(delay = 3000))
    public void addLikeToPost(long postId) {
        CachedPost cachePost = postCacheRepository.findById(postId).orElseGet(() -> getFromBD(postId));
        cachePost.incrementVersion();
        cachePost.incrementLike();
        postCacheRepository.save(cachePost);
    }

    private CachedPost getFromBD(long postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new DataNotFoundException("Post with id " + postId + " not found"));
        postCacheRepository.save(postMapper.toCachedPost(post));
        return postMapper.toCachedPost(post);
    }
}
