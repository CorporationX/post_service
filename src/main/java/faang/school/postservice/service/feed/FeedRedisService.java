package faang.school.postservice.service.feed;

import faang.school.postservice.dto.comment.FeedCommentDto;
import faang.school.postservice.dto.post.FeedPostDto;

import java.util.List;

public interface FeedRedisService {
    boolean isPostAvailableInCache(Long userId, int offset);

    boolean isCommentAvailableInCache(Long postId, int offset);

    List<FeedPostDto> loadPostsFromCache(Long userId, int offset);

    List<FeedCommentDto> loadCommentsFromCache(Long postId, int offset);

    void cachePostIdForUser(Long userId, Long postId, int offset);

    void cacheCommentIdForPost(Long postId, Long commentId, int offset);

    void cachePostDetails(FeedPostDto feedPostDto);

    void removePostFromCache(Long postId);

    void incrementPostLikes(Long postId);

    void decrementPostLikes(Long postId);

    void incrementCommentLikes(Long commentId);

    void decrementCommentLikes(Long commentId);

    void incrementPostComments(Long postId);

    void decrementPostComments(Long postId);

    void cacheCommentDetails(FeedCommentDto feedCommentDto);

    void removeCommentFromCache(Long commentId);

    void incrementPostViews(Long postId);

    void preloadUserPosts(Long userId);

    void preloadPostComments(Long postId);

    void updateUserPostOffset(Long userId, int value);

    void updatePostCommentsOffset(Long postId, int value);
}
