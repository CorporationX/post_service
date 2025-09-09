package faang.school.postservice.cache.comment;

import faang.school.postservice.dto.comment.CommentDto;

import java.util.List;

public interface PostCommentCache {
    void add(CommentDto comment);

    List<CommentDto> getPostComments(long postId);
}
