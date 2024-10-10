package faang.school.postservice.service.post.cache;

import faang.school.postservice.dto.post.serializable.PostCacheDto;
import faang.school.postservice.service.post.hash.tag.PostHashTagParser;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZoneOffset;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Service
public class PostCacheOperations {
    @Value("${app.post.cache.post_id_prefix}")
    private String postIdPrefix;

    private final PostHashTagParser postHashTagParser;
    private final PostCacheService postCacheService;

    @Transactional(propagation = Propagation.MANDATORY)
    public void addPostToCache(PostCacheDto post, List<String> newTags) {
        log.info("Add post to cache, post with id: {}", post.getId());
        String postId = postIdPrefix + post.getId();
        long timestamp = post.getPublishedAt().toInstant(ZoneOffset.UTC).toEpochMilli();
        boolean toDeletePostFromCache = false;
        newTags = postCacheService.filterByTagsInCache(newTags);

        if (!newTags.isEmpty()) {
            postCacheService.saveChangesOfPost(post, postId, timestamp, newTags, List.of(), toDeletePostFromCache);
        }
    }

    @Transactional(propagation = Propagation.MANDATORY)
    public void deletePostOfCache(PostCacheDto post, List<String> primalTags) {
        log.info("Delete post of cache, post with id: {}", post.getId());
        String postId = postIdPrefix + post.getId();
        long timestamp = 0;
        boolean toDeletePostFromCache = true;
        primalTags = postCacheService.filterByTagsInCache(primalTags);

        if (!primalTags.isEmpty() || postCacheService.postIsInCache(postId)) {
            postCacheService.saveChangesOfPost(post, postId, timestamp, List.of(), primalTags, toDeletePostFromCache);
        }
    }

    @Transactional(propagation = Propagation.MANDATORY)
    public void updatePostOfCache(PostCacheDto post, List<String> primalTags, List<String> updTags) {
        log.info("Update post of cache, post with id: {}", post.getId());
        String postId = postIdPrefix + post.getId();
        long timestamp = post.getPublishedAt().toInstant(ZoneOffset.UTC).toEpochMilli();

        List<String> updTagsOfPostInCache = postCacheService.filterByTagsInCache(updTags);
        List<String> delTags = postHashTagParser.getDeletedHashTags(primalTags, updTags);
        List<String> newTags = postHashTagParser.getNewHashTags(primalTags, updTags);
        delTags = postCacheService.filterByTagsInCache(delTags);
        newTags = postCacheService.filterByTagsInCache(newTags);

        boolean toDeletePostFromCache = newTags.isEmpty() && updTagsOfPostInCache.isEmpty();

        if (!delTags.isEmpty() || !newTags.isEmpty() || postCacheService.postIsInCache(postId)) {
            postCacheService.saveChangesOfPost(post, postId, timestamp, newTags, delTags, toDeletePostFromCache);
        }
    }
}
