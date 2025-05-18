package faang.school.postservice.service.feed.post;

import faang.school.postservice.dto.post.FeedPostDto;

import java.util.List;

public interface FeedPostRedisService {
    boolean isPostAvailableInCache(Long userId, int offset);

    List<FeedPostDto> loadPostsFromCache(Long userId, int offset);

    void cachePostIdForUser(Long userId, Long postId, int offset);

    void cachePostDetails(FeedPostDto feedPostDto);

    void removePostFromCache(Long postId);

    void incrementPostLikes(Long postId);

    void decrementPostLikes(Long postId);

    void incrementPostComments(Long postId);

    void decrementPostComments(Long postId);

    void incrementPostViews(Long postId);

    void preloadUserPosts(Long userId);

    void updateUserPostOffset(Long userId, int value);
}
