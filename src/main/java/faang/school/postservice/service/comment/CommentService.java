package faang.school.postservice.service.comment;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.dto.user.UserDto;
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
    private final UserServiceClient userServiceClient;
    private final CommentRepository commentRepository;
    private final CommentValidation commentValidation;

    @Transactional
    public Comment createComment(Comment comment) {
        long authorId = comment.getAuthorId();
        long postId = comment.getPost().getId();
        String content = comment.getContent();
        UserDto userDto = userServiceClient.getUser(authorId);

        commentValidation.validateLengthContentComment(content);
        commentValidation.checkAuthorEqualsUser(authorId, userDto.id());
        commentValidation.validatePostExists(postId);

        return commentRepository.save(comment);
    }

    @Transactional
    public Comment updateComment(Comment updateComment) {
        long commentId = updateComment.getId();
        String newContent = updateComment.getContent();
        Comment comment = getComment(commentId);

        commentValidation.validateLengthContentComment(newContent);
        commentValidation.checkContentNotEquals(comment.getContent(), newContent);

        comment.setContent(newContent);
        return commentRepository.save(comment);
    }

    @Transactional(readOnly = true)
    public List<Comment> getAllComments(long postId) {
        commentValidation.validatePostExists(postId);

        List<Comment> comments = commentRepository.findAllByPostId(postId);
        if (null == comments) {
            throw new NullPointerException("List comments cannot be Null");
        }
        return comments;
    }

    @Transactional
    public void deleteComment(long commentId, long authorId) {
        commentValidation.validateCommentExists(commentId);
        Comment comment = getComment(commentId);
        commentValidation.checkAuthorEqualsUser(authorId, comment.getAuthorId());

        commentRepository.deleteById(commentId);
    }

    @Transactional(readOnly = true)
    public Comment getComment(long commentId) {
        return commentRepository.findById(commentId).orElseThrow(
                () -> new EntityNotFoundException("the comment was not found in the database"));
    }
}