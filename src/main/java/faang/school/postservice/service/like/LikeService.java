package faang.school.postservice.service.like;

import faang.school.postservice.dto.like.LikeViewDto;
import faang.school.postservice.mapper.LikeMapper;
import faang.school.postservice.model.Comment;
import faang.school.postservice.model.Like;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.LikeRepository;
import faang.school.postservice.service.like.LikeNotificationService;
import faang.school.postservice.service.post.PostService;
import faang.school.postservice.service.comment.CommentService;
import faang.school.postservice.validation.LikeValidator;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * Сервис для работы с лайками.
 * <p>
 * Основные методы:
 * <ul>
 *   <li>{@link #likePost(long, long)} - добавляет лайк на пост</li>
 *   <li>{@link #unlikePost(long, long)} - удаляет лайк с поста</li>
 *   <li>{@link #likeComment(long, long)} - добавляет лайк на комментарий</li>
 *   <li>{@link #unlikeComment(long, long)} - удаляет лайк с комментария</li>
 * </ul>
 *
 * @author gulnaz21
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class LikeService {
    private final LikeRepository likeRepository;
    private final LikeMapper likeMapper;
    private final PostService postService;
    private final LikeValidator likeValidator;
    private final CommentService commentService;
    private final LikeNotificationService likeNotificationService;

    /**
     * Добавляет лайк на пост.
     *
     * @param postId Идентификатор поста.
     * @param userId Идентификатор пользователя.
     * @return DTO с информацией о добавленном лайке.
     */
    @Transactional
    public LikeViewDto likePost(long postId, long userId) {
        log.info("Попытка добавить лайк на пост с ID {} от пользователя с ID {}", postId, userId);

        likeValidator.validateForAddingPostLike(postId, userId);
        Post post = postService.getPostEntity(postId);

        Like like = new Like();
        like.setUserId(userId);
        like.setPost(post);

        Like newLike = likeRepository.save(like);

        likeNotificationService.publishUserLikeEvent(post, userId);
        log.info("Лайк на пост с ID {} от пользователя с ID {} успешно добавлен. ID лайка: {}",
                postId, userId, newLike.getId());

        return likeMapper.toDto(newLike);
    }

    /**
     * Удаляет лайк с поста.
     *
     * @param postId Идентификатор поста.
     * @param userId Идентификатор пользователя.
     */
    public void unlikePost(long postId, long userId) {
        log.info("Попытка удалить лайк с поста с ID {} от пользователя с ID {}", postId, userId);
        likeValidator.validateForRemovingPostLike(postId, userId);

        likeRepository.deleteByPostIdAndUserId(postId, userId);
        log.info("Лайк с поста с ID {} от пользователя с ID {} успешно удален", postId, userId);
    }

    /**
     * Добавляет лайк на комментарий.
     *
     * @param commentId Идентификатор комментария.
     * @param userId    Идентификатор пользователя.
     * @return DTO с информацией о добавленном лайке.
     */
    @Transactional
    public LikeViewDto likeComment(long commentId, long userId) {
        log.info("Попытка добавить лайк на комментарий с ID {} от пользователя с ID {}",
                commentId, userId);
        likeValidator.validateForAddingCommentLike(commentId, userId);
        Comment comment = commentService.getCommentById(commentId);

        Like like = new Like();
        like.setUserId(userId);
        like.setComment(comment);

        Like newLike = likeRepository.save(like);
        log.info("Лайк на комментарий с ID {} от пользователя с ID {} успешно добавлен. ID лайка: {}",
                commentId, userId, newLike.getId());
        return likeMapper.toDto(newLike);
    }

    /**
     * Удаляет лайк с комментария.
     *
     * @param commentId Идентификатор комментария.
     * @param userId    Идентификатор пользователя.
     */
    public void unlikeComment(long commentId, long userId) {
        log.info("Попытка удалить лайк с комментария с ID {} от пользователя с ID {}",
                commentId, userId);
        likeValidator.validateForRemovingCommentLike(commentId, userId);

        likeRepository.deleteByCommentIdAndUserId(commentId, userId);
        log.info("Лайк с комментария с ID {} от пользователя с ID {} успешно удален",
                commentId, userId);
    }
}
