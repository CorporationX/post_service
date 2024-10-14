package faang.school.postservice.service.comment;

import faang.school.postservice.dto.comment.CommentEvent;
import faang.school.postservice.exception.comment.CommentNotFoundException;
import faang.school.postservice.model.Comment;
import faang.school.postservice.model.Post;
import faang.school.postservice.publisher.comment.RedisCommentEventPublisher;
import faang.school.postservice.repository.CommentRepository;
import faang.school.postservice.service.post.PostService;
import faang.school.postservice.validator.CommentValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Collection;

@Service
@RequiredArgsConstructor
public class CommentService {
    private final CommentRepository commentRepository;
    private final PostService postService;
    private final CommentValidator commentValidator;
    private final RedisCommentEventPublisher commentEventPublisher;

    @Transactional
    public Comment createComment(Long postId, Comment comment) {
        commentValidator.validateCreate(postId, comment);
        Post post = postService.findPostById(postId);
        comment.setPost(post);
        Comment savedComment = commentRepository.save(comment);
        if (!post.getAuthorId().equals(comment.getAuthorId())) {
            CommentEvent event = new CommentEvent(
                    postId,
                    comment.getAuthorId(),
                    savedComment.getId(),
                    LocalDateTime.now()
            );
            commentEventPublisher.publishCommentEvent(event);
        }
        return savedComment;
    }

    @Transactional
    public Comment updateComment(Long commentId, Comment comment) {
        var foundComment = getById(commentId);
        commentValidator.validateCommentAuthorId(comment.getAuthorId(), foundComment);
        foundComment.setContent(comment.getContent());
        return commentRepository.save(foundComment);
    }

    public Collection<Comment> getAllCommentsByPostId(Long postId) {
        return commentRepository.findAllByPostId(postId).stream()
                .sorted((c1, c2) -> c2.getCreatedAt().compareTo(c1.getCreatedAt()))
                .toList();
    }

    @Transactional
    public void delete(Long commentId) {
        getById(commentId);
        commentRepository.deleteById(commentId);
    }

    public Comment getById(Long id) {
        return commentRepository.findById(id)
                .orElseThrow(
                        () -> new CommentNotFoundException("Comment not found")
                );
    }
}
