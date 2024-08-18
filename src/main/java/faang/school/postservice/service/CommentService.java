package faang.school.postservice.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.dto.comment.CommentDto;
import faang.school.postservice.dto.comment.CommentEvent;
import faang.school.postservice.mapper.CommentMapper;
import faang.school.postservice.model.Comment;
import faang.school.postservice.model.Post;
import faang.school.postservice.publisher.CommentEventPublisher;
import faang.school.postservice.repository.CommentRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class CommentService {
    private final UserServiceClient userServiceClient;
    private final CommentRepository commentRepository;
    private final PostService postService;
    private final CommentMapper commentMapper;
    private final ObjectMapper objectMapper;
    private final CommentEventPublisher commentEventPublisher;

    @Transactional
    public void createComment(CommentDto commentDto) {
        validateUserById(commentDto.getAuthorId());
        Post post = postService.findById(commentDto.getPostId());
        Comment comment = commentRepository.save(commentMapper.toEntity(commentDto));
        post.getComments().add(comment);
        commentEventPublisher.publish(createCommentEventMessage(comment, post));
    }

    @Transactional
    public void updateComment(CommentDto commentDto) {
        Comment comment = findById(commentDto.getId());
        comment.setContent(commentDto.getContent());
        commentRepository.save(comment);
    }

    @Transactional(readOnly = true)
    public List<CommentDto> getAllByPostId(Long postId) {
        Post post = postService.findById(postId);
        return commentMapper.toDtos(post.getComments()
                .stream()
                .sorted(Comparator.comparing(Comment::getCreatedAt).reversed())
                .toList());
    }

    @Transactional
    public void deleteComment(Long commentId) {
        commentRepository.deleteById(commentId);
    }

    public Comment findById(Long commentId) {
        return commentRepository
                .findById(commentId)
                .orElseThrow(() -> {
                    log.info("Comment not found, method getValidationCommentById");
                    return new EntityNotFoundException("Comment not found");
                });
    }

    private void validateUserById(Long userId) {
        userServiceClient.getUser(userId);
    }

    private String createCommentEventMessage(Comment comment, Post post) {
        CommentEvent commentEvent = CommentEvent.builder()
                .commentAuthorId(comment.getAuthorId())
                .postAuthorId(post.getAuthorId())
                .commentId(comment.getId())
                .postId(post.getId())
                .content(comment.getContent())
                .build();
        try {
            return objectMapper.writeValueAsString(commentEvent);
        } catch (JsonProcessingException e) {
            log.error("Error while creating comment event message", e);
            throw new RuntimeException(e);
        }
    }
}