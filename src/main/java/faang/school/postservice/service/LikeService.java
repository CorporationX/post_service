package faang.school.postservice.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.config.context.UserContext;
import faang.school.postservice.dto.like.LikeDto;
import faang.school.postservice.dto.like.LikeEvent;
import faang.school.postservice.dto.post.PostDto;
import faang.school.postservice.dto.user.UserDto;
import faang.school.postservice.exception.UserAlreadyLikedException;
import faang.school.postservice.mapper.LikeMapper;
import faang.school.postservice.mapper.PostMapper;
import faang.school.postservice.model.Comment;
import faang.school.postservice.model.Like;
import faang.school.postservice.model.Post;
import faang.school.postservice.model.outbox.EventStatus;
import faang.school.postservice.model.outbox.EventType;
import faang.school.postservice.model.outbox.OutboxEvent;
import faang.school.postservice.repository.LikeRepository;
import faang.school.postservice.service.outbox.OutboxEventService;
import faang.school.postservice.validator.CommentValidator;
import faang.school.postservice.validator.PostValidator;
import faang.school.postservice.validator.UserValidator;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class LikeService {

    private static final int BATCH_SIZE = 100;

    private final UserServiceClient userServiceClient;
    private final UserContext userContext;
    private final UserValidator userValidator;
    private final PostValidator postValidator;
    private final CommentValidator commentValidator;
    private final LikeRepository likeRepository;
    private final LikeMapper likeMapper;
    private final PostMapper postMapper;
    private final LikeEventPublisher likeEventPublisher;
    private final DeleteLikeEventPublisher deleteLikeEventPublisher;
    private final PostService postService;
    private final LikeEventPublisher likeEventPublisher;
    private final DeleteLikeEventPublisher deleteLikeEventPublisher;
    private final PostService postService;
    private final OutboxEventService outboxEventService;

    public List<UserDto> getAllUsersWhoLikedPost(Long postId) {
        Post post = postValidator.getPostById(postId);
        List<Long> userIds = post.getLikes().stream()
                .map(Like::getUserId)
                .toList();

        return getUsers(userIds);
    }

    public List<UserDto> getAllUsersWhoLikedComment(Long commentId) {
        Comment comment = commentValidator.getCommentById(commentId);
        List<Long> userIds = comment.getLikes().stream()
                .map(Like::getUserId)
                .toList();

        return getUsers(userIds);
    }

    @Transactional
    public LikeDto likePost(Long postId) {
        Long userId = validateAndGetUserId();
        likeRepository.findByPostIdAndUserId(postId, userId).ifPresent(like -> {
            log.warn("User with id %d is already liked post with id %d".formatted(userId, postId));
            throw new UserAlreadyLikedException("User is already liked post");
        });
        Like like = Like.builder()
                .userId(userId)
                .post(postValidator.getPostById(postId))
                .build();
        likeRepository.save(like);

        LikeEvent likeEvent = getLikeEvent(postId, userId);
        OutboxEvent outboxEvent = buildOutboxEvent(likeEvent, EventType.LIKE_CREATED);
        outboxEventService.saveOutboxEvent(outboxEvent);

        return likeMapper.toLikeDto(like);
    }

    private List<UserDto> fetchUsersInBatches(List<Long> userIds) {
        List<UserDto> userDtos = new ArrayList<>();

        for (int i = 0; i < userIds.size(); i += BATCH_SIZE) {
            List<Long> batch = userIds.subList(i, Math.min(i + BATCH_SIZE, userIds.size()));

            userDtos.addAll(userServiceClient.getUsersByIds(batch));
        }
        return userDtos;
    }

    @Transactional
    public LikeDto removeLikeOnPost(Long postId) {
        Long userId = validateAndGetUserId();
        Like like = likeRepository.findByPostIdAndUserId(postId, userId)
                .orElseThrow(() -> {
                    log.warn("Like by user with id %d on post with id %d does not exist"
                            .formatted(userId, postId));
                    return new EntityNotFoundException(
                            "Like by user with id %d on post with id %d does not exist"
                                    .formatted(userId, postId));
                });
        likeRepository.delete(like);
        LikeEvent likeEvent = getLikeEvent(postId, userId);
        OutboxEvent outboxEvent = buildOutboxEvent(likeEvent, EventType.LIKE_DELETED);
        outboxEventService.saveOutboxEvent(outboxEvent);
        return likeMapper.toLikeDto(like);
    }

    @Transactional
    public LikeDto likeComment(Long commentId) {
        Long userId = validateAndGetUserId();
        likeRepository.findByCommentIdAndUserId(commentId, userId).ifPresent(like -> {
            log.warn("User with id %d is already liked comment with id %d".formatted(userId, commentId));
            throw new UserAlreadyLikedException("User is already liked comment");
        });
        Like like = Like.builder().userId(userId).comment(commentValidator.getCommentById(commentId)).build();
        likeRepository.save(like);
        return likeMapper.toLikeDto(like);
    }

    private List<UserDto> getUsers(List<Long> userIds) {
        if (userIds.isEmpty()) {
            return Collections.emptyList();
        }
        return fetchUsersInBatches(userIds);
    }

    @Transactional
    public LikeDto removeLikeOnComment(Long commentId) {
        Long userId = validateAndGetUserId();
        Like like = likeRepository.findByCommentIdAndUserId(commentId, userId)
                .orElseThrow(() -> {
                    log.warn("Like by user with id %d on comment with id %d does not exist"
                            .formatted(userId, commentId));
                       return new EntityNotFoundException(
                        "Like by user with id %d on comment with id %d does not exist"
                                .formatted(userId, commentId));
                });
        likeRepository.delete(like);
        return likeMapper.toLikeDto(like);
    }

    public PostDto countLikesPost(Long postId) {
        return postMapper.toDto(postValidator.getPostById(postId));
    }

    public Long validateAndGetUserId() {
        Long userId = userContext.getUserId();
        userValidator.validateUserExist(userId);
        return userId;
    }

    private String serializePayload(Object obj) {
        try {
            return new ObjectMapper().writeValueAsString(obj);
        } catch (JsonProcessingException e) {
            log.error("Failed to serialize payload", e);
            throw new RuntimeException("Event serialization error", e);
        }
    }

    private OutboxEvent buildOutboxEvent(LikeEvent likeEvent, EventType eventType) {
        String payload = serializePayload(likeEvent);
        return OutboxEvent.builder()
                .type(eventType)
                .payload(payload)
                .status(EventStatus.IN_PROGRESS)
                .build();
    }

    private LikeEvent getLikeEvent(Long postId, Long userId) {
        return LikeEvent.builder()
                .authorPostId(postService.getPost(postId).getAuthorId())
                .authorLikeId(userId)
                .postId(postId)
                .createdAt(LocalDateTime.now())
                .build();
    }
}
