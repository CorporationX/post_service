package faang.school.postservice.service.comment;

import faang.school.postservice.dto.comment.CommentDto;
import faang.school.postservice.dto.comment.SaveCommentDto;

public interface CommentService {
    CommentDto update(Long commentId, Long authorId, SaveCommentDto dto);
    void delete(Long commentId, Long userId);
}
