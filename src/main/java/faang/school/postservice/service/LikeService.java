package faang.school.postservice.service;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.config.context.UserContext;
import faang.school.postservice.dto.event.LikeEvent;
import faang.school.postservice.exception.ConcurrentLikeException;
import faang.school.postservice.exception.DuplicateEntityException;
import faang.school.postservice.exception.EntityNotFoundException;
import faang.school.postservice.model.Comment;
import faang.school.postservice.model.Like;
import faang.school.postservice.model.Post;
import faang.school.postservice.publisher.LikeEventPublisher;
import faang.school.postservice.repository.CommentRepository;
import faang.school.postservice.repository.LikeRepository;
import faang.school.postservice.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantLock;

@Service
@RequiredArgsConstructor
@Slf4j
public class LikeService {

    private static final String LIKE_ENTITY_NAME = "like";
    private static final String POST_ENTITY_NAME = "post";
    private static final String COMMENT_ENTITY_NAME = "comment";
    private static final String NOT_FOUND_ENTITY_MESSAGE = "%s with id: %d not found";
    private static final String NOT_FOUND_SUB_ENTITY_MESSAGE = "%s on %s with id: %d not found";
    private static final String EXISTS_ENTITY_MESSAGE = "%s already exists on %s with id %d";

    private final Map<Long, ReentrantLock> locksUsers = new ConcurrentHashMap<>();
    private final LikeRepository likeRepository;
    private final PostRepository postRepository;
    private final CommentRepository commentRepository;
    private final UserContext userContext;
    private final UserServiceClient userClient;
    private final LikeEventPublisher likeEventPublisher;

    public void putLikeOnPost(Long postId) {
        Long userId = getContextUser();
        ReentrantLock userLock = getUserLock(userId);
        userLock.lock();
        try {
            validateEntityId(postId);
            Post post = postRepository.findById(postId).orElseThrow(() ->
                    new EntityNotFoundException(NOT_FOUND_ENTITY_MESSAGE, POST_ENTITY_NAME, postId));

            if (!isLikeOnPostEmpty(postId, userId)) {
                throw new DuplicateEntityException(EXISTS_ENTITY_MESSAGE,
                        LIKE_ENTITY_NAME, POST_ENTITY_NAME, postId);
            }
            boolean isUserNotPutLike = post.getComments().stream()
                    .allMatch(comment -> comment.getLikes().stream()
                            .noneMatch(like -> like.getUserId().equals(userId)));
            checkUserPuttingLike(isUserNotPutLike, COMMENT_ENTITY_NAME, POST_ENTITY_NAME, postId);

            addLikeOnDatabase(userId, post, null);
            printMessageAddLike(postId);
        } finally {
            userLock.unlock();
            Like like = likeRepository.findByPostIdAndUserId(postId, userId)
                    .orElseThrow(() -> new EntityNotFoundException("Лайк не найден"));
            likeEventPublisher.publish(new LikeEvent(like.getId(), userId, postId));
        }
    }

    public void removeLikeAtPost(Long postId) {
        long userId = getContextUser();
        ReentrantLock userLock = getUserLock(userId);
        userLock.lock();
        try {
            validateEntityId(postId);

            if (isLikeOnPostEmpty(postId, userId)) {
                throw new EntityNotFoundException(NOT_FOUND_SUB_ENTITY_MESSAGE,
                        LIKE_ENTITY_NAME, POST_ENTITY_NAME, postId);
            }
            likeRepository.deleteByPostIdAndUserId(postId, userId);
            printMessageRemoveLike(postId);
        } finally {
            userLock.unlock();
        }
    }

    public void putLikeOnComment(Long commentId) {
        Long userId = getContextUser();
        ReentrantLock userLock = getUserLock(userId);
        userLock.lock();
        try {
            validateEntityId(commentId);
            Comment comment = commentRepository.findById(commentId).orElseThrow(() ->
                    new EntityNotFoundException(NOT_FOUND_ENTITY_MESSAGE, COMMENT_ENTITY_NAME, commentId));

            if (!isLikeOnCommentEmpty(commentId, userId)) {
                throw new DuplicateEntityException(EXISTS_ENTITY_MESSAGE,
                        LIKE_ENTITY_NAME, COMMENT_ENTITY_NAME, commentId);
            }
            boolean isUserNotPutLike = comment.getPost().getLikes().stream()
                    .noneMatch(like -> like.getUserId().equals(userId));
            checkUserPuttingLike(isUserNotPutLike, POST_ENTITY_NAME, COMMENT_ENTITY_NAME, commentId);

            addLikeOnDatabase(userId, null, comment);
            printMessageAddLike(commentId);
        } finally {
            userLock.unlock();
        }
    }

    public void removeLikeAtComment(Long commentId) {
        long userId = getContextUser();
        ReentrantLock userLock = getUserLock(userId);
        userLock.lock();
        try {
            validateEntityId(commentId);

            if (isLikeOnCommentEmpty(commentId, userId)) {
                throw new EntityNotFoundException(NOT_FOUND_SUB_ENTITY_MESSAGE,
                        LIKE_ENTITY_NAME, COMMENT_ENTITY_NAME, commentId);
            }
            likeRepository.deleteByCommentIdAndUserId(commentId, userId);
            printMessageRemoveLike(commentId);
        } finally {
            userLock.unlock();
        }
    }

    private void validateEntityId(Long entityId) {
        Objects.requireNonNull(entityId, "Invalid like target id value");
    }

    private void printMessageAddLike(Long postId) {
        log.debug("Added like on {} with id: {}", POST_ENTITY_NAME, postId);
    }

    private void printMessageRemoveLike(Long commentId) {
        log.debug("Removed like on {} with id: {}", COMMENT_ENTITY_NAME, commentId);
    }

    private void checkUserPuttingLike(boolean isUserNotPutLike, String subEntityName,
                                      String entityName, Long entityId) {
        if (!isUserNotPutLike) {
            throw new ConcurrentLikeException(
                    "Like on %s this %s with id: %d already exists", subEntityName, entityName, entityId);
        }
    }

    private void addLikeOnDatabase(Long userId, Post post, Comment comment) {
        likeRepository.save(Like.builder()
                .userId(userId)
                .post(post)
                .comment(comment)
                .build());
    }

    private ReentrantLock getUserLock(Long userId) {
        return locksUsers.computeIfAbsent(userId, key -> new ReentrantLock());
    }

    private Long getContextUser() {
        return userClient.getUser(userContext.getUserId()).id();
    }

    private boolean isLikeOnPostEmpty(Long postId, Long userId) {
        return likeRepository.findByPostIdAndUserId(postId, userId).isEmpty();
    }

    private boolean isLikeOnCommentEmpty(Long commentId, Long userId) {
        return likeRepository.findByCommentIdAndUserId(commentId, userId).isEmpty();
    }
}
