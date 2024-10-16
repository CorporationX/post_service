package faang.school.postservice.service.comment;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.dto.comment.CommentEventDto;
import faang.school.postservice.dto.user.UserDto;
import faang.school.postservice.exception.DataValidationException;
import faang.school.postservice.mapper.comment.CommentEventMapper;
import faang.school.postservice.model.Comment;
import faang.school.postservice.model.Post;
import faang.school.postservice.publis.publisher.CommentEventPublisher;
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
    private final CommentEventPublisher commentEventPublisher;
    private final CommentEventMapper commentEventMapper;

    @Transactional
    public Comment createComment(Comment comment) {
        UserDto user = userServiceClient.getUser(comment.getAuthorId());
        commentServiceHandler.userExistValidation(user.getId());

        Long postId = comment.getPost().getId();
        Post post = getPostById(postId);

        comment.setAuthorId(user.getId());
        post.getComments().add(comment);

        Comment savedComment = commentRepository.save(comment);
        publishCommentEventToNotificationService(savedComment, post);
        return savedComment;
    }

    @Transactional
    public Comment updateComment(Comment comment) {
        UserDto user = userServiceClient.getUser(comment.getAuthorId());
        commentServiceHandler.userExistValidation(user.getId());

        Comment commentToUpdate = getCommentById(comment.getId());
        commentServiceHandler.editCommentByAuthorValidation(user, commentToUpdate);

        commentToUpdate.setContent(comment.getContent());
        return commentRepository.save(commentToUpdate);
    }

    @Transactional
    public void deleteComment(long commentId) {
        commentServiceHandler.commentExistsValidation(commentId);
        commentRepository.deleteById(commentId);
    }

    @Transactional(readOnly = true)
    public List<Comment> findAllComments(long postId) {
        commentServiceHandler.postExistsValidation(postId);
        return commentRepository.findAllByPostIdOrderByCreatedAtDesc(postId);
    }

    public List<Long> getAuthorIdsToBeBanned() {
        return commentRepository.findAuthorIdsToBeBanned();
    }

    private Post getPostById(Long postId) {
        return postRepository.findById(postId)
                .orElseThrow(() -> new DataValidationException("Post with ID: " + postId + " not found."));
    }

    private Comment getCommentById(Long commentId) {
        return commentRepository.findById(commentId)
                .orElseThrow(() -> new DataValidationException("Comment with ID: " + commentId + " not found."));
    }

    private void publishCommentEventToNotificationService(Comment savedComment, Post post) {
        CommentEventDto commentEventDto = commentEventMapper.toEvent(savedComment, post);
        commentEventPublisher.publish(commentEventDto);
    }
}
