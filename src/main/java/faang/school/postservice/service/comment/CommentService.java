package faang.school.postservice.service.comment;

import faang.school.postservice.annotations.PublishEvent;
import faang.school.postservice.dto.comment.CommentEvent;
import faang.school.postservice.exception.comment.CommentNotFoundException;
import faang.school.postservice.model.Comment;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.CommentRepository;
import faang.school.postservice.service.post.PostService;
import faang.school.postservice.validator.CommentValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;

@Service
@RequiredArgsConstructor
public class CommentService {
    private final CommentRepository commentRepository;
    private final PostService postService;
    private final CommentValidator commentValidator;

    @PublishEvent(eventType = CommentEvent.class)
    @Transactional
    public Comment createComment(Long postId, Comment comment) {
        commentValidator.validateCreate(postId, comment);
        Post post = postService.findPostById(postId);
        comment.setPost(post);
        Comment savedComment = commentRepository.save(comment);
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
