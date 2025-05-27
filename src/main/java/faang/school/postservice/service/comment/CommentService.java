package faang.school.postservice.service.comment;

import faang.school.postservice.model.Comment;
import faang.school.postservice.repository.CommentRepository;
import faang.school.postservice.repository.PostRepository;
import faang.school.postservice.validation.comment.CommentValidation;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CommentService {
    private final CommentRepository commentRepository;
    private final CommentValidation commentValidation;
    private final PostRepository postRepository;

    public void createComment(Comment comment) {
        commentValidation.validateLengthContentComment(comment);
        commentValidation.validateAuthorExists(comment);
        commentValidation.validatePostExists(comment);
        commentRepository.save(comment);
    }

    public void updateComment(Comment updateComment) {
        commentValidation.validateLengthContentComment(updateComment);
        commentValidation.validatePostExists(updateComment);
        commentValidation.validateAuthorExists(updateComment);

        Comment comment = getComment(updateComment.getId());
        commentValidation.validateCommentEqualsUpdateComment(comment, updateComment);

        comment.setContent(updateComment.getContent());
        commentRepository.save(comment);
    }

    public List<Comment> getAllComment(long postId) {
        if (!postRepository.existsById(postId)) {
            throw new EntityNotFoundException(("the post does not exists %d".formatted(postId)));
        }
        return commentRepository.findAllByPostId(postId);
    }

    public void deleteComment(long commentId) {
        if (!commentRepository.existsById(commentId)) {
            throw new EntityNotFoundException(("the comment does not exists %d".formatted(commentId)));
        }
        commentRepository.deleteById(commentId);
    }

    public Comment getComment(long commentId) {
        return commentRepository.findById(commentId).orElseThrow(
                () -> new EntityNotFoundException("the comment was not found in the database"));
    }
}