package faang.school.postservice.service.post;

import faang.school.postservice.dto.post.PostCacheDto;
import faang.school.postservice.repository.PostCacheRepository;
import faang.school.postservice.service.PostCacheService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.locks.ReentrantLock;

@Slf4j
@Service
@RequiredArgsConstructor
public class PostCacheServiceImpl implements PostCacheService {
    private final PostCacheRepository postCacheRepository;
    private final ReentrantLock viewLock = new ReentrantLock();
    private final ReentrantLock likeLock = new ReentrantLock();

    @Override
    public void addView(long postId) {
        viewLock.lock();
        try {
            Optional<PostCacheDto> cache = postCacheRepository.get(postId);
            if (cache.isEmpty()) {
                log.info("Can't add post view to post [{}]: post cache not exists.", postId);
                return;
            }

            PostCacheDto post = cache.get();
            post.setViewCount(post.getViewCount() + 1);
            postCacheRepository.set(post);
        } catch (Exception e) {
            log.info("Error on adding view to post [{}]", postId, e);
        } finally {
            viewLock.unlock();
        }
    }

    @Override
    public void addLike(long postId, long userId) {
        likeLock.lock();
        try {
            Optional<PostCacheDto> cache = postCacheRepository.get(postId);
            if (cache.isEmpty()) {
                log.info("Message for liked post [{}] on user [{}] wasn't processed cause: post cache not exists.", postId, userId);
                return;
            }

            PostCacheDto postCache = cache.get();
            postCacheRepository.set(preparePostCache(postCache, userId));
        } finally {
            likeLock.unlock();
        }
    }

    private PostCacheDto preparePostCache(PostCacheDto post, long userId) {
        List<Long> likeIds = post.getLikeIds();
        if (likeIds.contains(userId)) {
            return post;
        }

        likeIds.add(userId);
        post.setLikeIds(likeIds);
        post.setLikeCount(post.getLikeCount() + 1);

        return post;
    }
}
