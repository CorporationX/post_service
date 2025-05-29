package faang.school.postservice.validation;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.exception.DataValidationException;
import faang.school.postservice.model.Comment;
import faang.school.postservice.model.Like;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.LikeRepository;
import faang.school.postservice.service.post.PostService;
import faang.school.postservice.service.comment.CommentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

/**
 * Компонент для валидации лайков на посты и комментарии.
 * <p>
 * Основные проверки:
 *  <ul>
 *   <li>{@link #validateForAddingPostLike(long, long)} - проверка возможности поставить лайк посту</li>
 *   <li>{@link #validateForRemovingPostLike(long, long)} - проверка возможности убрать лайк с поста</li>
 *   <li>{@link #validateForAddingCommentLike(long, long)} - проверка возможности поставить лайк комментарию</li>
 *   <li>{@link #validateForRemovingCommentLike(long, long)} - проверка возможности убрать лайк с комментария</li>
 *  </ul>
 * <p>
 * Включает проверки:
 * <ul>
 *   <li>Существования сущностей (пост/комментарий/пользователь)</li>
 *   <li>Отсутствия дублирования лайков</li>
 *   <li>Наличия лайков перед удалением</li>
 * </ul>
 * </p>
 *
 * @author gulnaz21
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class LikeValidator {
    private final LikeRepository likeRepository;
    private final PostService postService;
    private final CommentService commentService;
    private final UserServiceClient userServiceClient;

    /**
     * Проверяет условия для добавления лайка на пост.
     *
     * @param postId Идентификатор поста.
     * @param userId Идентификатор пользователя.
     */
    public void validateForAddingPostLike(long postId, long userId) {
        validateUserExistence(userId);
        validateUserDidNotLikePost(postId, userId);
        validateUserDidNotLikePostComments(postId, userId);
    }

    /**
     * Проверяет условия для удаления лайка с поста.
     *
     * @param postId Идентификатор поста.
     * @param userId Идентификатор пользователя.
     */
    public void validateForRemovingPostLike(long postId, long userId) {
        validatePostExistence(postId);
        validateUserExistence(userId);
        validatePostLikeExists(postId, userId);
    }

    /**
     * Проверяет условия для добавления лайка на комментарий.
     *
     * @param commentId Идентификатор комментария.
     * @param userId    Идентификатор пользователя.
     */
    public void validateForAddingCommentLike(long commentId, long userId) {
        validateUserExistence(userId);
        validateUserDidNotLikeComment(commentId, userId);
        validateUserDidNotLikePostOfComment(commentId, userId);
    }

    /**
     * Проверяет условия для удаления лайка с комментария.
     *
     * @param commentId Идентификатор комментария.
     * @param userId    Идентификатор пользователя.
     */
    public void validateForRemovingCommentLike(long commentId, long userId) {
        validateCommentExistence(commentId);
        validateUserExistence(userId);
        validateCommentLikeExists(commentId, userId);
    }

    /**
     * Проверяет существование поста.
     *
     * @param postId Идентификатор поста.
     */
    private void validatePostExistence(long postId) {
        postService.getPostEntity(postId);
    }

    /**
     * Проверяет существование пользователя.
     *
     * @param userId Идентификатор пользователя.
     */
    private void validateUserExistence(long userId) {
        userServiceClient.getUser(userId);
    }

    /**
     * Проверяет существование комментария.
     *
     * @param commentId Идентификатор комментария.
     */
    private void validateCommentExistence(long commentId) {
        commentService.getCommentById(commentId);
    }

    /**
     * Проверяет, что пользователь не ставил лайк на пост.
     *
     * @param postId Идентификатор поста.
     * @param userId Идентификатор пользователя.
     * @throws DataValidationException Если лайк на пост уже существует.
     */
    private void validateUserDidNotLikePost(long postId, long userId) {
        likeRepository.findByPostIdAndUserId(postId, userId)
                .ifPresent(like -> {
                    log.error("Ошибка: Пользователь с ID = {} уже ставил лайк на пост с ID = {}", userId, postId);
                    throw new DataValidationException(
                            String.format("User with ID %d has already liked post with ID %d", userId, postId));
                });
    }

    /**
     * Проверяет, что пользователь не ставил лайк на пост, к которому относится комментарий.
     *
     * @param commentId Идентификатор комментария.
     * @param userId    Идентификатор пользователя.
     * @throws DataValidationException Если лайк на пост уже существует.
     */
    private void validateUserDidNotLikePostOfComment(long commentId, long userId) {
        Comment comment = commentService.getCommentById(commentId);
        Long postId = comment.getPost().getId();
        validateUserDidNotLikePost(postId, userId);
    }

    /**
     * Проверяет, что пользователь не ставил лайки на комментарии к посту.
     *
     * @param postId Идентификатор поста.
     * @param userId Идентификатор пользователя.
     * @throws DataValidationException Если лайк на комментарий уже существует.
     */
    private void validateUserDidNotLikePostComments(long postId, long userId) {
        Post post = postService.getPostEntity(postId);
        List<Comment> comments = post.getComments();
        for (Comment comment : comments) {
            validateUserDidNotLikeComment(comment.getId(), userId);
        }
    }

    /**
     * Проверяет, что пользователь не ставил лайк на комментарий.
     *
     * @param commentId Идентификатор комментария.
     * @param userId    Идентификатор пользователя.
     * @throws DataValidationException Если лайк на комментарий уже существует.
     */
    private void validateUserDidNotLikeComment(long commentId, long userId) {
        likeRepository.findByCommentIdAndUserId(commentId, userId)
                .ifPresent(like -> {
                    log.error("Ошибка: Пользователь с ID = {} уже ставил лайк на комментарий с ID = {}", userId, commentId);
                    throw new DataValidationException(
                            String.format("User with ID %d has already liked comment with ID %d", userId, commentId));
                });
    }

    /**
     * Проверяет, что пользователь с указанным ID поставил лайк на пост с указанным ID.
     *
     * @param postId Идентификатор поста, для которого проверяется наличие лайка.
     * @param userId Идентификатор пользователя, который должен был поставить лайк.
     * @throws DataValidationException Если лайк от указанного пользователя на указанный пост не найден.
     */
    private void validatePostLikeExists(long postId, long userId) {
        Optional<Like> like = likeRepository.findByPostIdAndUserId(postId, userId);
        if (like.isEmpty()) {
            log.error("Ошибка: Пользователь с ID = {} еще не ставил лайк на пост с ID = {}", userId, postId);
            throw new DataValidationException(
                    String.format("User with ID %d has not liked post with ID %d", userId, postId));
        }
    }

    /**
     * Проверяет, что пользователь с указанным ID поставил лайк на комментарий с указанным ID.
     *
     * @param commentId Идентификатор комментария, для которого проверяется наличие лайка.
     * @param userId    Идентификатор пользователя, который должен был поставить лайк.
     * @throws DataValidationException Если лайк от указанного пользователя на указанный комментарий не найден.
     */
    private void validateCommentLikeExists(long commentId, long userId) {
        Optional<Like> like = likeRepository.findByCommentIdAndUserId(commentId, userId);
        if (like.isEmpty()) {
            log.error("Ошибка: Пользователь с ID = {} еще не ставил лайк на комментарий с ID = {}", userId, commentId);
            throw new DataValidationException(
                    String.format("User with ID %d has not liked comment with ID %d", userId, commentId));
        }
    }
}
