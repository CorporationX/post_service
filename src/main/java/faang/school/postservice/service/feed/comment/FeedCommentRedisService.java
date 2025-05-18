package faang.school.postservice.service.feed.comment;

import faang.school.postservice.dto.comment.FeedCommentDto;

import java.util.List;

public interface FeedCommentRedisService {
    boolean isCommentAvailableInCache(Long postId, int offset);

    List<FeedCommentDto> loadCommentsFromCache(Long postId, int offset);

    void cacheCommentIdForPost(Long postId, Long commentId, int offset);

    void incrementCommentLikes(Long commentId);

    void decrementCommentLikes(Long commentId);

    void cacheCommentDetails(FeedCommentDto feedCommentDto);

    void removeCommentFromCache(Long commentId);

    void preloadPostComments(Long postId);

    void updatePostCommentsOffset(Long postId, int value);
}
