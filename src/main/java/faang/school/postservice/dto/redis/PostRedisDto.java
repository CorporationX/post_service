package faang.school.postservice.dto.redis;

import java.time.LocalDateTime;

/**
 * DTO для сохранения в репозиторий Redis
 *
 * @param id идентификатор поста
 * @param content содержание поста
 * @param authorId ID автора поста. Может быть null
 * @param projectId ID проекта, к которому относится пост. Может быть null для личных постов.
 * @param likeCount количество лайков у поста
 * @param commentCount количество комментариев у поста
 * @param publishedAt дата публикации
 *
 * @author Linempy
 * @since 27.09.2025
 */
public record PostRedisDto(
        Long id,
        String content,
        Long authorId,
        Long projectId,
        Long likeCount,
        Long commentCount,
        LocalDateTime publishedAt
) {
}