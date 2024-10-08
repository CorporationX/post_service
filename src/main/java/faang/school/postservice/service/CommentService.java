package faang.school.postservice.service;


import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.config.context.UserContext;
import faang.school.postservice.dto.comment.CommentDto;
import faang.school.postservice.dto.event.CommentAchievementEvent;
import faang.school.postservice.dto.event.CommentEvent;
import faang.school.postservice.dto.event.kafka.CommentCreatedEvent;
import faang.school.postservice.dto.user.UserDto;
import faang.school.postservice.mapper.CommentAchievementMapper;
import faang.school.postservice.mapper.CommentEventMapper;
import faang.school.postservice.mapper.CommentMapper;
import faang.school.postservice.model.Comment;
import faang.school.postservice.model.chache.UserCache;
import faang.school.postservice.producer.KafkaProducer;
import faang.school.postservice.redisPublisher.CommentAchievementEventPublisher;
import faang.school.postservice.redisPublisher.CommentEventPublisher;
import faang.school.postservice.repository.CommentRepository;
import faang.school.postservice.repository.PostRepository;
import faang.school.postservice.repository.cache.UserCacheRepository;
import faang.school.postservice.validator.CommentValidator;

import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class CommentService {
    private final CommentRepository commentRepository;
    private final CommentMapper commentMapper;
    private final UserContext userContext;
    private final CommentValidator commentValidator;
    private final PostRepository postRepository;
    private final CommentEventPublisher commentEventPublisher;
    private final CommentAchievementEventPublisher commentAchievementEventPublisher;
    private final CommentAchievementMapper commentAchievementMapper;
    private final UserCacheRepository userCacheRepository;
    private final UserServiceClient userServiceClient;
    private final KafkaProducer kafkaProducer;
    @Value("${spring.user.cache.ttl}")
    private long userTtl;

    @Transactional
    public CommentDto createComment(CommentDto commentDto) {
        long userId = userContext.getUserId();
        commentValidator.existUser(userId);
        UserDto user = userServiceClient.getUser(userId);
        commentDto.setAuthorId(user.getId());
        commentValidator.existPost(commentDto.getPostId());
        Comment comment = commentMapper.dtoToEntity(commentDto);
        comment.setPost(postRepository.findById(commentDto.getPostId()).get());
        Comment savedComment = commentRepository.save(comment);
        CommentEvent commentEvent = CommentEvent.builder()
                .commentAuthorId(savedComment.getAuthorId())
                .commentId(savedComment.getId())
                .createdAt(savedComment.getCreatedAt())
                .postAuthorId(savedComment.getPost().getAuthorId())
                .build();
        commentEventPublisher.publish(commentEvent);
        publishCommentAchievementEvent(commentDto);
        userCacheRepository.save(UserCache.builder()
                .id(user.getId())
                .name(user.getUsername())
                .ttl(userTtl)
                .build());
        kafkaProducer.sendEvent(CommentCreatedEvent.builder()
                .commentId(savedComment.getId())
                .authorId(savedComment.getAuthorId())
                .postId(savedComment.getPost().getId())
                .build());
        return commentMapper.entityToDto(savedComment);
    }

    @Transactional
    public CommentDto updateComment(CommentDto commentDto) {
        commentValidator.existComment(commentDto.getId());
        Comment comment = commentRepository.findById(commentDto.getId()).orElseThrow();
        commentValidator.checkUserIsAuthorComment(comment, userContext.getUserId());
        comment.setContent(commentDto.getContent());
        return commentMapper.entityToDto(commentRepository.save(comment));
    }

    @Transactional
    public void deleteComment(long commentId) {
        commentValidator.existComment(commentId);
        Comment comment = commentRepository.findById(commentId).orElseThrow();
        commentValidator.checkUserIsAuthorComment(comment, userContext.getUserId());
        commentRepository.delete(comment);
    }

    @Transactional(readOnly = true)
    public List<CommentDto> findAllByPostId(long postId) {
        commentValidator.existPost(postId);
        List<Comment> comments = commentRepository.findAllByPostId(postId);
        if (comments.isEmpty()) {
            String msg = "Post with id:%d has no comments";
            log.error(String.format(msg, postId));
            throw new EntityNotFoundException(String.format(msg, postId));
        }
        return comments.stream()
                .sorted(Comparator.comparing(Comment::getCreatedAt).reversed())
                .map(commentMapper::entityToDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public Comment getComment(long commentId) {
        return commentRepository.findById(commentId)
                .orElseThrow(() -> new IllegalArgumentException("Comment with the same id does not exist"));
    }

    private void publishCommentAchievementEvent(CommentDto commentDto) {
        commentAchievementEventPublisher.publish(commentAchievementMapper.commentDtoToCommentAchievementEvent(commentDto));
    }
}