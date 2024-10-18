package faang.school.postservice.redis.cache.service.post;

import faang.school.postservice.dto.comment.CommentDto;
import faang.school.postservice.redis.cache.entity.PostCache;
import faang.school.postservice.redis.cache.repository.PostCacheRepository;
import faang.school.postservice.redis.cache.service.RedisOperations;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Async("postsCacheTaskExecutor")
public class PostCacheServiceImpl implements PostCacheService {

    @Value("${spring.data.redis.cache.settings.max-post-comments-size}")
    private int maxCommentsQuantity;

    private final PostCacheRepository postCacheRepository;
    private final RedisOperations redisOperations;

    @Override
    public void save(PostCache entity) {

        entity = redisOperations.updateOrSave(postCacheRepository, entity, entity.getId());

        log.info("Saved post with id {} to cache: {}", entity.getId(), entity);

    }

    public List<PostCache> getPostCacheByIds(List<Long> postIds){
        return postCacheRepository.findAllById(postIds);
    }

    @Override
    public void deleteById(long postId) {

        PostCache post = redisOperations.findById(postCacheRepository, postId).orElse(null);

        log.info("Deleted post with id={} from cache", postId);

        if (post != null) {
            redisOperations.deleteById(postCacheRepository, postId);
        }
    }

    @Override
    public void incrementLikes(long postId) {

        redisOperations.customUpdate(postCacheRepository, postId,  () -> {
            postCacheRepository.findById(postId).ifPresent(post ->{
                post.setLikesCount(post.getLikesCount() + 1);
                postCacheRepository.save(post);
            });
        });
    }

    @Override
    public void incrementViews(long postId) {

        redisOperations.customUpdate(postCacheRepository, postId,  () -> {
            postCacheRepository.findById(postId).ifPresent(postCache ->{
                postCache.setViewsCount(postCache.getViewsCount() + 1);
                postCacheRepository.save(postCache);
            });
        });
    }

    @Override
    public void addCommentToCachedPost(Long postId, CommentDto commentDto) {

        redisOperations.customUpdate(postCacheRepository, postId, ()->{
            postCacheRepository.findById(postId).ifPresent(postCache -> {
                var comments = postCache.getComments();
                comments.add(commentDto);
                if(comments.size() > maxCommentsQuantity){
                    postCache.getComments().pollFirst();
                }
            });
        });
    }
}
