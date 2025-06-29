package faang.school.postservice.service;

import faang.school.postservice.client.UserServiceClient;

import faang.school.postservice.exception.CommentNotFoundException;
import faang.school.postservice.exception.UserNotFoundException;
import faang.school.postservice.model.Comment;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.CommentRepository;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CommentService {

    private final CommentRepository commentRepository;
    private final PostService postService;
    private final UserServiceClient userServiceClient;
    private final CommentValidator commentValidator;

    @Transactional(readOnly = true)
    public List<Comment> getCommentsByPostId(Long postId) {
        postService.getPostById(postId);
        return commentRepository.findAllByPostId(postId).stream()
                .sorted(Comparator.comparing(Comment::getCreatedAt).reversed())
                .toList();
    }

    @Transactional
    public Comment createComment(Comment comment, Long postId, Long authorId) {
        Post post = postService.getPostById(postId);
        try {
            userServiceClient.getUser(authorId);
        } catch (FeignException e) {
            throw new UserNotFoundException("User with id = " + authorId + " was not found");
        }
        comment.setPost(post);
        comment.setAuthorId(authorId);
        return commentRepository.save(comment);
    }

    @Transactional
    public Comment updateComment(Long commentId, Comment updatedComment, Long userId) {
        Comment existing = commentRepository.findById(commentId)
                .orElseThrow(() -> new CommentNotFoundException("There is no comment with id: " + commentId));
        commentValidator.validateAuthor(existing, userId);
        commentValidator.validateCommentUpdate(updatedComment);
        existing.setContent(updatedComment.getContent());
        return commentRepository.save(existing);
    }

    @Transactional
    public Comment deleteComment(Long commentId, Long userId) {
        Comment existing = commentRepository.findById(commentId)
                .orElseThrow(() -> new CommentNotFoundException("There is no comment with id: " + commentId));
        commentValidator.validateAuthor(existing, userId);
        commentRepository.delete(existing);
        return existing;
    }
}