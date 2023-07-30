package faang.school.postservice.service.comment;

import faang.school.postservice.dto.comment.CommentDto;
import faang.school.postservice.exeption.DataValidationException;
import faang.school.postservice.model.Comment;
import faang.school.postservice.repository.CommentRepository;
import faang.school.postservice.validator.comment.CommentValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CommentService {
    private final CommentRepository commentRepository;
    private final CommentValidator commentValidator;

    public void createComment(CommentDto commentDto) {

    }

    public void updateComment(CommentDto commentDto) {

    }

    public List<CommentDto> getAllComments(long postId) {
        return null;
    }

    public void deleteComment(long commentId) {
        commentRepository.deleteById(commentId);
    }

    public Comment getCommentById(long commentId) {
        return commentRepository.findById(commentId)
                .orElseThrow(() -> new DataValidationException("Comment was not found"));
    }
}
