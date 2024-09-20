package faang.school.postservice.service.post.cache;

import faang.school.postservice.dto.post.serializable.PostCacheDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Slf4j
@Service
@RequiredArgsConstructor
public class PostCacheService {
    private final PostCacheOperations postCacheOperations;

    public void newPostProcess(PostCacheDto post) {
        log.info("New post process, post with id: {}", post.getId());
        List<String> newTags = post.getHashTags();
        if (!newTags.isEmpty()) {
            postCacheOperations.addPostToCache(post, newTags);
        }
    }

    public void deletePostProcess(PostCacheDto post, List<String> primalTags) {
        log.info("Delete post in cache process, post with id: {}", post.getId());
        if (!primalTags.isEmpty()) {
            postCacheOperations.deletePostOfCache(post, primalTags);
        }
    }

    public void updatePostProcess(PostCacheDto post, List<String> primalTags) {
        log.info("Update post in cache process, post with id: {}", post.getId());
        List<String> updTags = post.getHashTags();

        if (primalTags.isEmpty() && !updTags.isEmpty()) {
            postCacheOperations.addPostToCache(post, updTags);
        }
        else if (!primalTags.isEmpty() && updTags.isEmpty()) {
            postCacheOperations.deletePostOfCache(post, primalTags);
        }
        else if (!primalTags.isEmpty()) {
            postCacheOperations.updatePostOfCache(post, primalTags, updTags);
        }
    }

    public void addListOfPostsToCache(List<PostCacheDto> posts, String tagToFind) {
        log.info("Add to cache list of posts");
        posts.forEach(post -> postCacheOperations.addPostToCacheByTag(post, post.getHashTags(), tagToFind));
    }

    public List<PostCacheDto> findInRangeByHashTag(String hashTag, int start, int end) {
        Set<String> postIds = postCacheOperations.findIdsByHashTag(hashTag, start, end);
        return postCacheOperations.findAllByIds(new ArrayList<>(postIds));
    }
}
