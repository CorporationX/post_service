package faang.school.postservice.service;

import faang.school.postservice.dto.comment.CommentDto;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CommentService {
    CommentServiceValidator commentServiceValidator;

    public void createComment(long id, CommentDto commentDto) {
        commentServiceValidator.validateForCreateComment(commentDto);
    }

    public void updateComment(long id, CommentDto commentDto) {
        commentServiceValidator.validateForUpdateComment(id, commentDto);
    }

    public List<CommentDto> getAllComments(long id) {
        return commentServiceValidator.validateForGetAllComments(id);
    }

    public void deleteComment(long id, CommentDto commentDto) {
        commentServiceValidator.validateForDeleteComment(id, commentDto);
    }
}
