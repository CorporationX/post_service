package faang.school.postservice.service.comment;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.dto.user.UserDto;
import faang.school.postservice.exception.DataValidationException;
import faang.school.postservice.model.Comment;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.CommentRepository;
import faang.school.postservice.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CommentService {
    private final UserServiceClient userServiceClient;
    private final CommentRepository commentRepository;
    private final PostRepository postRepository;
    private final CommentServiceHandler commentServiceHandler;

    @Transactional
    public Comment createComment(Comment comment) {
        UserDto user = userServiceClient.getUser(comment.getAuthorId());
        commentServiceHandler.userExistsByIdValidation(user.getId());

        Long postId = comment.getPost().getId();
        Post post = getPostById(postId);

        comment.setAuthorId(user.getId());
        post.getComments().add(comment);
        return commentRepository.save(comment);
    }

    @Transactional
    public Comment updateComment(Comment comment) {
        UserDto user = userServiceClient.getUser(comment.getAuthorId());
        commentServiceHandler.userExistsByIdValidation(user.getId());

        Comment updatedComment = getCommentById(comment.getId());
        commentServiceHandler.editCommentByAuthorValidation(user, updatedComment);

        updatedComment.setContent(comment.getContent());
        return commentRepository.save(updatedComment);
    }

    @Transactional
    public void deleteComment(long commentId) {
        commentServiceHandler.commentExistsByIdValidation(commentId);
        commentRepository.deleteById(commentId);
    }

    @Transactional(readOnly = true)
    public List<Comment> findAllComments(long postId) {
        commentServiceHandler.postExistsByIdValidation(postId);
        return commentRepository.findAllByPostIdOrderByUpdatedAtDesc(postId);
    }

    private Post getPostById(Long postId) {
        return postRepository.findById(postId)
                .orElseThrow(() -> new DataValidationException("Post with ID: " + postId + " not found."));
    }

    private Comment getCommentById(Long commentId) {
        return commentRepository.findById(commentId)
                .orElseThrow(() -> new DataValidationException("Comment with ID: " + commentId + " not found."));
    }
}
