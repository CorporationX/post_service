package faang.school.postservice.service.comment;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.dto.comment.CommentCreateDto;
import faang.school.postservice.dto.comment.CommentViewDto;
import faang.school.postservice.exception.DataValidationException;
import faang.school.postservice.exception.EntityNotFoundException;
import faang.school.postservice.exception.ModerationException;
import faang.school.postservice.mapper.CommentMapper;
import faang.school.postservice.model.Comment;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.CommentRepository;
import faang.school.postservice.repository.PostRepository;
import faang.school.postservice.config.moderation.ModerationConfig;
import faang.school.postservice.service.util.CommentModerationAsyncHandler;
import faang.school.postservice.validation.CommentValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.ListUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

/**
 * Сервис для работы с комментариями к постам.
 * Содержит методы для создания, обновления, удаления и получения комментариев.
 *
 * <p>Основные функции:</p>
 * <ul>
 *   <li>{@link #createComment(Long, CommentCreateDto)} - создает новый комментарий</li>
 *   <li>{@link #updateComment(Long, Long, CommentCreateDto)} - обновляет существующий комментарий</li>
 *   <li>{@link #getCommentsByPostId(Long)} - получает все комментарии для указанного поста</li>
 *   <li>{@link #deleteComment(Long, Long)} - удаляет комментарий по его ID</li>
 *   <li>{@link #moderateUnverifiedComment()} - запускает процесс модерации неподтвержденных комментариев</li>
 * </ul>
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class CommentService {
    private final CommentRepository commentRepository;
    private final PostRepository postRepository;
    private final CommentMapper commentMapper;
    private final UserServiceClient userServiceClient;
    private final CommentValidator commentValidator;
    private final ModerationConfig commentModerationConfig;
    private final CommentModerationAsyncHandler commentModerationAsyncHandler;

    /**
     * Создает новый комментарий к указанному посту.
     *
     * @param postId           ID поста для комментария
     * @param commentCreateDto DTO с данными для создания комментария
     * @return созданный комментарий в формате CommentViewDto
     * @throws EntityNotFoundException если пост не найден или пользователь не существует
     * @see CommentValidator#validateUserById(Long)
     */
    @Transactional
    public CommentViewDto createComment(Long postId, CommentCreateDto commentCreateDto) {
        log.debug("Creating comment for post with ID: {}", postId);

        commentValidator.validatePostExists(postId);
        commentValidator.validateUserById(commentCreateDto.getAuthorId());

        Comment comment = commentMapper.toEntity(commentCreateDto);
        comment.setPost(getPostById(postId));
        comment.setCreatedAt(LocalDateTime.now());

        Comment savedComment = commentRepository.save(comment);
        log.debug("Comment with ID: {} successfully created", savedComment.getId());

        return commentMapper.toViewDto(savedComment);
    }

    /**
     * Обновляет существующий комментарий.
     *
     * @param postId           ID поста, к которому принадлежит комментарий
     * @param commentId        ID комментария для обновления
     * @param commentCreateDto DTO с новыми данными комментария
     * @return обновленный комментарий в формате CommentViewDto
     * @throws EntityNotFoundException если комментарий не найден или пользователь не существует
     * @throws DataValidationException если комментарий не принадлежит указанному посту
     * @see CommentValidator#validateCommentBelongsToPost(Comment, Long)
     */
    @Transactional
    public CommentViewDto updateComment(Long postId, Long commentId, CommentCreateDto commentCreateDto) {
        log.debug("Updating comment with ID: {} for post with ID: {}", commentId, postId);
        Comment comment = getCommentById(commentId);
        commentValidator.validateCommentBelongsToPost(comment, postId);

        comment.setContent(commentCreateDto.getContent());
        comment.setUpdatedAt(LocalDateTime.now());

        Comment updatedComment = commentRepository.save(comment);
        log.debug("Comment with ID: {} successfully updated", updatedComment.getId());

        return commentMapper.toViewDto(updatedComment);
    }

    /**
     * Получает все комментарии для указанного поста.
     *
     * @param postId ID поста для получения комментариев
     * @return список комментариев в формате CommentViewDto
     */
    @Transactional
    public List<CommentViewDto> getCommentsByPostId(Long postId) {
        log.debug("Retrieving comments for post with ID: {}", postId);
        commentValidator.validatePostExists(postId);

        List<Comment> comments = commentRepository.findAllByPostId(postId);
        log.info("Found {} comments for post with ID: {}", comments.size(), postId);

        return comments.stream()
                .map(commentMapper::toViewDto)
                .toList();
    }


    /**
     * Удаляет комментарий по его ID.
     *
     * @param postId    ID поста, к которому принадлежит комментарий
     * @param commentId ID комментария для удаления
     * @throws EntityNotFoundException если комментарий не найден или пользователь не существует
     * @throws DataValidationException если комментарий не принадлежит указанному посту
     * @see CommentValidator#validateCommentBelongsToPost(Comment, Long)
     */
    @Transactional
    public void deleteComment(Long postId, Long commentId) {
        log.debug("Deleting comment with ID: {} for post with ID: {}", commentId, postId);
        Comment comment = getCommentById(commentId);
        commentValidator.validateCommentBelongsToPost(comment, postId);

        commentRepository.delete(comment);
        log.debug("Comment with ID: {} successfully deleted", commentId);
    }

    /**
     * Запускает процесс модерации неподтвержденных комментариев.
     * Комментарии обрабатываются пакетами, чтобы избежать перегрузки системы.
     *
     * @throws ModerationException если произошла ошибка во время процесса модерации
     */
    public void moderateUnverifiedComment() {
        log.info("Moderation of unverified posts started");
        List<Comment> unverifiedComments = commentRepository.findAllByVerifiedAtIsNull();

        if (unverifiedComments.isEmpty()) {
            log.info("No unverified comments found");
            return;
        }

        List<List<Comment>> batches = ListUtils.partition(unverifiedComments,
                commentModerationConfig.getBatchSize());

        List<CompletableFuture<Void>> moderationTasks = batches.stream()
                .map(commentModerationAsyncHandler::checkForProfanity)
                .toList();

        try {
            CompletableFuture.allOf(moderationTasks.toArray(new CompletableFuture[0]))
                    .get(1, TimeUnit.HOURS);
            log.info("Moderation completed successfully. Total comments processed: {}",
                    unverifiedComments.size());
        } catch (Exception exception) {
            log.error("Error during moderation process", exception);
            throw new ModerationException("Failed to complete moderation process");
        }
    }

    /**
     * Получает комментарий по его идентификатору.
     *
     * @param commentId ID комментария
     * @return найденный комментарий
     * @throws EntityNotFoundException если комментарий не найден
     */
    public Comment getCommentById(Long commentId) {
        return commentRepository.findById(commentId)
                .orElseThrow(() -> {
                    log.error("Comment with ID {} not found", commentId);
                    return new EntityNotFoundException(String.format("Comment with ID %d not found", commentId));
                });
    }

    /**
     * Получает пост по его идентификатору.
     *
     * @param postId ID поста
     * @return найденный пост
     * @throws EntityNotFoundException если пост не найден
     */
    public Post getPostById(Long postId) {
        return postRepository.findById(postId)
                .orElseThrow(() -> {
                    log.error("Post with ID {} not found", postId);
                    return new EntityNotFoundException(String.format("Post with ID %d not found", postId));
                });
    }

    /**
     * Обновляет сущность комментария и возвращает её DTO-представление.
     * Используется для случаев, когда обновление происходит не через стандартный DTO.
     *
     * @param comment сущность комментария для обновления
     * @return DTO-представление обновленного комментария
     */
    public CommentViewDto updateCommentEntity(Comment comment) {
        Comment updatedComment = commentRepository.save(comment);
        return commentMapper.toViewDto(updatedComment);
    }
}