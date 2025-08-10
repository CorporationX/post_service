package faang.school.postservice.service.comment;

import faang.school.postservice.dto.comment.CommentCreateDto;
import faang.school.postservice.dto.comment.CommentUpdateDto;
import faang.school.postservice.dto.comment.CommentViewDto;

import java.util.List;

/**
 * CommentService — интерфейс для управления комментариями к постам.
 * <p>
 * Определяет основные операции, связанные с созданием, обновлением,
 * получением и удалением комментариев в рамках конкретного поста.
 * <p>
 * Методы принимают и возвращают DTO для отделения слоя представления от модели данных.
 *
 * @author agent
 * @since 10.08.2025
 */
public interface CommentService {

    /**
     * Создает новый комментарий к посту с указанным идентификатором.
     * <p>
     * Выполняет валидацию автора и поста, маппинг DTO в сущность и сохранение в БД.
     *
     * @param postId идентификатор поста, к которому добавляется комментарий
     * @param dto    DTO с данными нового комментария
     * @return DTO созданного комментария с заполненными полями
     */
    CommentViewDto create(Long postId, CommentCreateDto dto);

    /**
     * Обновляет существующий комментарий с заданным идентификатором под конкретным постом.
     * <p>
     * Обновление ограничивается изменением содержимого комментария.
     * Выполняется проверка авторства и валидация входных данных.
     *
     * @param postId    идентификатор поста, к которому относится комментарий
     * @param commentId идентификатор обновляемого комментария
     * @param dto       DTO с обновленными данными комментария
     * @return DTO обновленного комментария
     */
    CommentViewDto update(Long postId, Long commentId, CommentUpdateDto dto);

    /**
     * Возвращает список всех комментариев для заданного поста,
     * отсортированных от самого позднего к самому раннему.
     *
     * @param postId идентификатор поста
     * @return список DTO комментариев, связанных с постом
     */
    List<CommentViewDto> getAllByPostId(Long postId);

    /**
     * Удаляет комментарий по заданному идентификатору.
     * <p>
     * Проверяется право удаления (например, авторство).
     *
     * @param commentId идентификатор удаляемого комментария
     */
    void delete(Long commentId);
}