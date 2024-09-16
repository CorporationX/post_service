package faang.school.postservice.service;

import faang.school.postservice.dto.comment.CommentDto;

import java.util.List;

public interface CommentService {

    CommentDto addComment(CommentDto comment);

    void updateContent(long commentId, String content);

    List<CommentDto> getCommentsByPostId(long postId);

    void deleteComment(long commentId);
}
