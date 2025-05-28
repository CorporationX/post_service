package faang.school.postservice.service.comment;

import faang.school.postservice.model.Comment;
import faang.school.postservice.repository.CommentRepository;
import faang.school.postservice.validation.comment.CommentValidation;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CommentService {
    private final CommentRepository commentRepository;
    private final CommentValidation commentValidation;

    @Transactional
    public Comment createComment(Comment comment) {
        commentValidation.validateLengthContentComment(comment);
        commentValidation.validateAuthorExists(comment);
        commentValidation.validatePostExists(comment);

        return commentRepository.save(comment);
    }

    @Transactional
    public Comment updateComment(Comment updateComment) {
        long commentId = updateComment.getId();
        String content = updateComment.getContent();
        Comment comment = getComment(commentId);
        commentValidation.validateLengthContentComment(updateComment);
        commentValidation.validateAuthorExists(updateComment);
        commentValidation.validatePostExists(comment);
        commentValidation.validateCommentEqualsUpdateComment(comment, updateComment);

        comment.setContent(content);
        return commentRepository.save(comment);
    }

    @Transactional(readOnly = true)
    public List<Comment> getAllComments(long postId) {
        commentValidation.validatePostExistsById(postId);
        return commentRepository.findAllByPostId(postId);
    }

    @Transactional
    public void deleteComment(long commentId) {
        commentValidation.validateCommentExists(commentId);
        commentRepository.deleteById(commentId);
    }

    public Comment getComment(long commentId) {
        return commentRepository.findById(commentId).orElseThrow(
                () -> new EntityNotFoundException("the comment was not found in the database"));
    }
}