package faang.school.postservice.service.comment;

import faang.school.postservice.dto.comment.CommentCreateDto;
import faang.school.postservice.dto.comment.CommentUpdateDto;
import faang.school.postservice.dto.comment.CommentViewDto;

import java.util.List;

/**
 * Интерфейс сервиса для работы с комментариями к постам
 * <p>
 * Определяет контракт для операций создания, удаления, обновления
 * и получения комментариев. Все методы должны быть реализованы
 * в классах-реализациях этого интерфейса.
 * </p>
 *
 * @author bozya
 * @since 21.08.2025
 */
public interface CommentService {

    /**
     * Создает новый комментарий к указанному посту.
     *
     * @param createDto DTO с данными для создания комментария
     * @param postId идентификатор поста, к которому добавляется комментарий
     * @return DTO созданного комментария
     */
    CommentViewDto create(CommentCreateDto createDto, Long postId);

    /**
     * Удаляет комментарий по идентификаторам поста и комментария.
     *
     * @param postId идентификатор поста
     * @param commentId идентификатор комментария
     */
    void delete(Long postId, Long commentId);

    /**
     * Обновляет существующий комментарий.
     *
     * @param postId идентификатор поста
     * @param commentId идентификатор комментария
     * @param updateDto DTO с обновленными данными комментария
     * @return DTO обновленного комментария
     */
    CommentViewDto update(Long postId, Long commentId, CommentUpdateDto updateDto);

    /**
     * Получает все комментарии для указанного поста.
     *
     * @param postId идентификатор поста
     * @return список DTO комментариев для указанного поста
     */
    List<CommentViewDto> getAllCommentByPostId(Long postId);


}