package faang.school.postservice.validator;

import faang.school.postservice.dto.comment.CommentDto;
import faang.school.postservice.model.Comment;
import faang.school.postservice.repository.CommentRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class CommentValidator {

    private final CommentRepository commentRepository;

    public Comment getCommentById(Long commentId) {
        return commentRepository.findById(commentId).orElseThrow(
                () -> {
                    log.warn("Comment with id {} not found", commentId);
                    return new EntityNotFoundException("Comment not found");
                }
        );
    }

    public void validateCommentDto(CommentDto commentDto) {
        if(commentDto.getContent() == null && commentDto.getContent().isBlank()) {
            throw new IllegalArgumentException("Comment cannot be empty or null");
        }
        if (commentDto.getContent().length() > 4096) {
            throw new IllegalArgumentException("Comment needs to be shortened");
        }
    }

    public void validateListComments(long postId) {
        List<Comment> comments = commentRepository.findAllByPostId(postId);

        if(comments.isEmpty()) {
            throw new IllegalArgumentException("Comments not found for post with ID" + postId);
        }
    }
}
