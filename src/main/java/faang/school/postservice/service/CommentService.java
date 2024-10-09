package faang.school.postservice.service;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.config.kafka.producer.KafkaCommentProducer;
import faang.school.postservice.dto.comment.CommentDto;
import faang.school.postservice.dto.comment.CommentEvent;
import faang.school.postservice.dto.user.UserDto;
import faang.school.postservice.mapper.comment.CommentMapper;
import faang.school.postservice.model.Comment;
import faang.school.postservice.model.Post;
import faang.school.postservice.config.redis.publisher.CommentEventPublisher;
import faang.school.postservice.repository.CommentRepository;
import faang.school.postservice.service.redis.UserRedisService;
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
    private final CommentEventPublisher commentEventPublisher;
    private final KafkaCommentProducer kafkaCommentProducer;
    private final UserRedisService userRedisService;

    @Transactional
    public void createComment(CommentDto commentDto) {
        UserDto author = validateUserById(commentDto.getAuthorId());
        Post post = postService.getPost(commentDto.getPostId());
        Comment comment = commentRepository.save(commentMapper.toEntity(commentDto));
        post.getComments().add(comment);
        commentEventPublisher.publish(createCommentEvent(comment, post));
        userRedisService.saveUser(author);
        sendMessage(comment);
    }

    @Transactional
    public void updateComment(CommentDto commentDto) {
        Comment comment = findById(commentDto.getId());
        comment.setContent(commentDto.getContent());
        commentRepository.save(comment);
    }

    @Transactional(readOnly = true)
    public List<CommentDto> getAllByPostId(Long postId) {
        Post post = postService.getPost(postId);
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

    private UserDto validateUserById(Long userId) {
        return userServiceClient.getUser(userId);
    }

    private CommentEvent createCommentEvent(Comment comment, Post post) {
        return CommentEvent.builder()
                .commentAuthorId(comment.getAuthorId())
                .postAuthorId(post.getAuthorId())
                .commentId(comment.getId())
                .postId(post.getId())
                .content(comment.getContent())
                .build();
    }

    private void sendMessage(Comment comment) {
        kafkaCommentProducer.sendMessage(commentMapper.toDto(comment));
    }

}