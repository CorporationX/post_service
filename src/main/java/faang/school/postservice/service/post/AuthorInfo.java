package faang.school.postservice.service.post;

/**
 * Вспомогательная DTO-запись, содержащая информацию об авторе контента.
 * Используется для передачи данных между слоями приложения при работе с постами,
 * когда автор может быть либо пользователем, либо проектом.
 *
 * <p>Характеристики:</p>
 * <ul>
 *   <li>Всегда содержит ровно один не-null идентификатор (либо authorId, либо projectId)</li>
 *   <li>Используется исключительно внутри сервисного слоя</li>
 * </ul>
 *
 * @param authorId   идентификатор пользователя-автора (может быть null)
 * @param projectId  идентификатор проекта-автора (может быть null)
 * @see PostService
 *
 * @author Linempy
 * @since 26.07.2025
 */
public record AuthorInfo(Long authorId, Long projectId) {

}