package faang.school.postservice.service;

import faang.school.postservice.model.Comment;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.CommentRepository;
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

    @Transactional
    public void createComment(Long postId, Comment comment) {
        commentValidator.validate(comment);
        Post post = postService.getById(postId);
        comment.setPost(post);
        commentRepository.save(comment);
    }

    @Transactional
    public void updateComment(Long commentId, Comment entity) {
        Comment comment = commentRepository.findById(commentId).orElseThrow();
        comment.setContent(entity.getContent());
    }

    public Collection<Comment> getAllCommentsByPostId(Long postId) {
        return commentRepository.findAllByPostId(postId).stream()
                .sorted((c1, c2) -> c2.getCreatedAt().compareTo(c1.getCreatedAt()))
                .toList();
    }

    public void delete(Long commentId) {
        commentRepository.deleteById(commentId);
    }
}
