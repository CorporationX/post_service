package faang.school.postservice.service;

import faang.school.postservice.model.dto.CommentDto;

import java.util.List;
import java.util.Map;

public interface CommentService {
    CommentDto createComment(CommentDto commentDto, Long userId);

    List<CommentDto> getComment(Long postId);

    void deleteComment(Long commentId);

    CommentDto updateComment(Long commentId, CommentDto commentDto, Long userId);

    int getCommentCount(Long postId);

    List<CommentDto> getRecentComments(Long postId, int numberOfComments);

    Map<Long, List<CommentDto>> getTop3CommentsForPosts(List<Long> postIds);

    Map<Long, List<Long>> getTop3CommentsAuthorIds(List<Long> postIds);

    Map<Long, Integer> getPostIdCommentCountMap(List<Long> postIds);
}
