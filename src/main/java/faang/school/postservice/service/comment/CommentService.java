package faang.school.postservice.service.comment;

import faang.school.postservice.dto.comment.CommentDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Сервис для управления комментариями.
 * Предоставляет методы для создания, обновления, удаления и поиска комментариев к постам.
 */
public interface CommentService {

    /**
     * Создаёт новый комментарий к посту
     * <p>
     * Условия:
     * <ul>
     *     <li>Комментарий должен содержать непустой контент (максимум 4096 символов) —
     *     в противном случае выбрасывается {@code DataValidationException}.</li>
     *     <li>Комментарий должен быть привязан к существующему посту —
     *     в противном случае выбрасывается {@code EntityNotFoundException}.</li>
     *     <li>Автор комментария должен быть существующим пользователем —
     *     в противном случае выбрасывается {@code EntityNotFoundException}.</li>
     * </ul>
     *
     * @param commentDto объект {@link CommentDto}, содержащий информацию о комментарии
     * @return объект {@link CommentDto}, содержащий информацию о созданном комментарии
     */
    CommentDto createComment(CommentDto commentDto);

    /**
     * Обновляет существующий комментарий
     * <p>
     * Условия:
     * <ul>
     *     <li>Комментарий должен существовать —
     *     в противном случае выбрасывается {@code EntityNotFoundException}.</li>
     *     <li>Обновить комментарий может только его автор —
     *     в противном случае выбрасывается {@code DataValidationException}.</li>
     *     <li>Обновлённый контент не может быть пустым (максимум 4096 символов) —
     *     в противном случае выбрасывается {@code DataValidationException}.</li>
     * </ul>
     *
     * @param commentId  идентификатор комментария, который обновляется
     * @param commentDto объект {@link CommentDto}, содержащий обновлённую информацию о комментарии
     * @return объект {@link CommentDto}, содержащий информацию об обновлённом комментарии
     */
    CommentDto updateComment(Long commentId, CommentDto commentDto);

    /**
     * Удаляет комментарий
     * <p>
     * Условия:
     * <ul>
     *     <li>Комментарий должен существовать —
     *     в противном случае выбрасывается {@code EntityNotFoundException}.</li>
     *     <li>Удалить комментарий может только его автор —
     *     в противном случае выбрасывается {@code DataValidationException}.</li>
     * </ul>
     *
     * @param commentId идентификатор комментария, который удаляется
     */
    void deleteComment(Long commentId);

    /**
     * Получает комментарий по его идентификатору
     * <p>
     * Условия:
     * <ul>
     *     <li>Комментарий должен существовать —
     *     в противном случае выбрасывается {@code EntityNotFoundException}.</li>
     * </ul>
     *
     * @param commentId идентификатор комментария
     * @return объект {@link CommentDto}, содержащий информацию о комментарии
     */
    CommentDto getCommentById(Long commentId);

    /**
     * Получает список комментариев к посту с пагинацией
     * <p>
     * Условия:
     * <ul>
     *     <li>Пост должен существовать —
     *     в противном случае выбрасывается {@code EntityNotFoundException}.</li>
     * </ul>
     *
     * @param postId   идентификатор поста, комментарии к которому запрашиваются
     * @param pageable параметры пагинации и сортировки
     * @return объект {@link Page}{@code <}{@link CommentDto}{@code >}, содержащий страницу комментариев
     */
    Page<CommentDto> getCommentsByPostId(Long postId, Pageable pageable);
}
