package faang.school.postservice.service.comment;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.dto.comment.CommentDto;
import faang.school.postservice.dto.user.UserDto;
import faang.school.postservice.event.comment.CommentEvent;
import faang.school.postservice.mapper.comment.CommentMapper;
import faang.school.postservice.model.Comment;
import faang.school.postservice.model.Post;
import faang.school.postservice.model.redis.AuthorCommentInRedis;
import faang.school.postservice.publisher.CommentEventPublisher;
import faang.school.postservice.repository.CommentRepository;
import faang.school.postservice.repository.PostRepository;
import faang.school.postservice.repository.redis.RedisAuthorCommentRepository;
import faang.school.postservice.validation.user.UserValidator;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class CommentService {

    private final CommentRepository commentRepository;
    private final CommentMapper commentMapper;
    private final PostRepository postRepository;
    private final UserValidator userValidator;
    private final CommentEventPublisher commentEventPublisher;
    private final RedisAuthorCommentRepository redisAuthorCommentRepository;
    private final UserServiceClient userServiceClient;

    @Transactional
    public CommentDto createComment(Long userId, Long postId, CommentDto commentDto) {
        userValidator.validateUserExist(userId);
        Comment comment = getComment(userId, postId, commentDto);
        Comment savedComment = commentRepository.save(comment);
        sendAuthorCommentInCashRedis(userId);
        log.info("Saved comment {}", savedComment.getId());
        publishCommentEvent(savedComment);
        return commentMapper.toDto(savedComment);
    }

    private void sendAuthorCommentInCashRedis(long userId) {
        UserDto userDto = userServiceClient.getUser(userId);
        AuthorCommentInRedis authorCommentInRedis = AuthorCommentInRedis.builder()
                .id(userDto.getId())
                .user(userDto)
                .build();
        log.info("Send author comment in redis: {}", authorCommentInRedis);
        redisAuthorCommentRepository.save(authorCommentInRedis);
    }

    @Transactional
    public CommentDto updateComment(Long commentId, CommentDto commentDto) {
        Comment comment = commentRepository.findById(commentId).orElseThrow(()
                -> new EntityNotFoundException(String.format("Comment with id %d not found", commentId)));
        comment.setContent(commentDto.getContent());
        return commentMapper.toDto(comment);
    }

    @Transactional(readOnly = true)
    public List<CommentDto> getCommentsByPostId(Long postId) {
        List<Comment> comments = commentRepository.findAllByPostId(postId).stream()
                .sorted((comm1, comm2) -> comm2.getCreatedAt().compareTo(comm1.getCreatedAt()))
                .collect(Collectors.toList());
        return commentMapper.toDto(comments);
    }

    @Transactional
    public void deleteComment(Long commentId) {
        commentRepository.deleteById(commentId);
    }

    @Transactional(readOnly = true)
    public List<Comment> findCommentsByVerified(boolean verified) {
        return commentRepository.findByVerified(verified);
    }

    private void publishCommentEvent(Comment comment) {
        commentEventPublisher.publish(CommentEvent.builder()
                .authorId(comment.getAuthorId())
                .postAuthorId(comment.getPost().getAuthorId())
                .postId(comment.getPost().getId())
                .commentId(comment.getId())
                .commentContent(comment.getContent())
                .build());
    }

    private Comment getComment(Long userId, Long postId, CommentDto commentDto) {
        Comment comment = commentMapper.toEntity(commentDto);
        comment.setAuthorId(userId);
        comment.setPost(getPost(postId));
        return comment;
    }

    private Post getPost(Long postId) {
        return postRepository.findById(postId).orElseThrow(()
                -> new EntityNotFoundException(String.format("Post with id %d not found", postId)));
    }
}
