package faang.school.postservice.service.comment;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.config.context.UserContext;
import faang.school.postservice.dto.user.UserDto;
import faang.school.postservice.model.Comment;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.CommentRepository;
import faang.school.postservice.repository.PostRepository;
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
    private final UserContext userContext;
    private final PostRepository postRepository;

    @Transactional
    public Comment createComment(long postId, String content) {
        long authorId = userContext.getUserId();
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new EntityNotFoundException("the post was not found in the database"));
        UserDto userDto = userServiceClient.getUser(authorId);

        commentValidation.validateLengthContentComment(content);
        commentValidation.checkAuthorEqualsUser(authorId, userDto.id());

        Comment comment = Comment.builder()
                .authorId(authorId)
                .post(post)
                .content(content)
                .build();

        return commentRepository.save(comment);
    }

    @Transactional
    public Comment updateComment(long commentId, String newContent) {

        Comment comment = getComment(commentId);
        long authorId = userContext.getUserId();

        commentValidation.validateLengthContentComment(newContent);
        commentValidation.checkContentNotEquals(comment.getContent(), newContent);
        commentValidation.checkAuthorEqualsUser(authorId, comment.getAuthorId());

        comment.setContent(newContent);
        return commentRepository.save(comment);
    }

    @Transactional(readOnly = true)
    public List<Comment> getAllComments(long postId) {
        List<Comment> comments = commentRepository.findAllByPostId(postId);
        if (comments.isEmpty()) {
            commentValidation.validatePostExists(postId);
        }
        return comments;
    }

    @Transactional
    public void deleteComment(long commentId) {
        long userId = userContext.getUserId();
        Comment comment = getComment(commentId);

        commentValidation.checkAuthorEqualsUser(userId, comment.getAuthorId());

        commentRepository.deleteById(commentId);
    }

    @Transactional(readOnly = true)
    public Comment getComment(long commentId) {
        return commentRepository.findById(commentId).orElseThrow(
                () -> new EntityNotFoundException("the comment was not found in the database"));
    }
}