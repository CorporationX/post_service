package faang.school.postservice.service;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.dto.CommentDto;
import faang.school.postservice.mapper.CommentMapper;
import faang.school.postservice.model.Comment;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.CommentRepository;
import faang.school.postservice.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class CommentService {
    private final CommentRepository commentRepository;
    private final CommentMapper commentMapper;
    private final PostRepository postRepository;
    private final UserServiceClient userServiceClient;

    public CommentDto createComment(Long postId, CommentDto commentDto) {
        log.info("Creating comment to post ID: {} from user ID: {}", postId, commentDto.getAuthorId());

        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("Post not found with ID:" + postId));

        userServiceClient.getUser(commentDto.getAuthorId());

        Comment comment = Comment.builder()
                .content(commentDto.getContent())
                .authorId(commentDto.getAuthorId())
                .post(post)
                .createdAt(LocalDateTime.now())
                .build();

        comment = commentRepository.save(comment);
        log.info("Comment created with ID: {}", comment.getId());
        return commentMapper.toDto(comment);
    }

    public CommentDto updateComment(Long commentId, CommentDto commentDto) {
        log.info("Updating comment with ID: {}", commentId);
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new IllegalArgumentException("Comment not found with ID: " + commentId));

        if (commentDto.getContent() == null && commentDto.getContent().isBlank()) {
            throw new IllegalArgumentException("Comment cannot be Empty or null");
        }

        comment.setContent(commentDto.getContent());
        comment.setUpdatedAt(LocalDateTime.now());
        comment = commentRepository.save(comment);
        log.info("Comment is updated with ID: {} successfully", commentId);
        return commentMapper.toDto(comment);
    }

    public List<CommentDto> getAllComments(Long postId) {
        log.info("Request to retrieve all comments for post ID: {}", postId);
        List<Comment> comments = commentRepository.findAllByPostId(postId);
        if (comments.isEmpty()) {
            throw new IllegalArgumentException("Comments not found for post with ID:" + postId);
        }
        return comments.stream()
                .sorted(Comparator.comparing(Comment::getCreatedAt).reversed())
                .map(commentMapper::toDto)
                .collect(Collectors.toList());
    }
    public void deleteCommentById(Long commentId) {
        log.info("Request to delete comment ID: {}", commentId);
        if (!commentRepository.existsById(commentId)) {
            throw new IllegalArgumentException("Comment not found with ID: " + commentId);
        }
        commentRepository.deleteById(commentId);
        log.info("Comment with ID: {} is deleted", commentId);
    }
}
