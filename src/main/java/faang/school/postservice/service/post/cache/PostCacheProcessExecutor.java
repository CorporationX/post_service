package faang.school.postservice.service.post.cache;

import faang.school.postservice.dto.post.serializable.PostCacheDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class PostCacheProcessExecutor {
    private final PostCacheOperations postCacheOperations;
    private final PostCacheService postCacheService;

    @Transactional(propagation = Propagation.MANDATORY)
    public void executeNewPostProcess(PostCacheDto post) {
        log.info("New post process, post with id: {}", post.getId());
        List<String> newTags = post.getHashTags();
        if (!newTags.isEmpty()) {
            postCacheOperations.addPostToCache(post, newTags);
        }
    }

    @Transactional(propagation = Propagation.MANDATORY)
    public void executeUpdatePostProcess(PostCacheDto post, List<String> primalTags) {
        log.info("Update post in cache process, post with id: {}", post.getId());
        List<String> updTags = post.getHashTags();

        if (primalTags.isEmpty() && !updTags.isEmpty()) {
            postCacheOperations.addPostToCache(post, updTags);
        } else if (!primalTags.isEmpty() && updTags.isEmpty()) {
            postCacheOperations.deletePostOfCache(post, primalTags);
        } else if (!primalTags.isEmpty()) {
            postCacheOperations.updatePostOfCache(post, primalTags, updTags);
        }
    }

    @Transactional(propagation = Propagation.MANDATORY)
    public void executeDeletePostProcess(PostCacheDto post, List<String> primalTags) {
        log.info("Delete post in cache process, post with id: {}", post.getId());
        if (!primalTags.isEmpty()) {
            postCacheOperations.deletePostOfCache(post, primalTags);
        }
    }

    @Async("postCacheServicePool")
    public void executeAddListOfPostsToCache(List<PostCacheDto> posts, String tagToFind) {
        log.info("Add to cache list of posts by tag: {}", tagToFind);
        postCacheService.savePostsByTag(tagToFind, posts);
    }
}
