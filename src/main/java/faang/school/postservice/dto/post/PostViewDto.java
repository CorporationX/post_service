package faang.school.postservice.dto.post;

import java.time.LocalDateTime;

/**
 * DTO для отображения информации о посте.
 * <p>
 * Содержит основные данные поста для отображения в UI или API ответах.
 * Используется как возвращаемый тип для операций чтения.
 * </p>
 *
 * @param content Содержимое поста (текст). Не может быть null.
 * @param authorId ID автора поста. Может быть null
 * @param projectId ID проекта, к которому относится пост. Может быть null для личных постов.
 * @param published Флаг публикации (true - опубликован, false - черновик)
 * @param deleted Флаг удаления (true - помечен как удаленный)
 * @param updateAt Дата последнего обновления. Может быть null для новых постов.
 *
 * @author Linempy
 * @since 25.07.2025
 */
public record PostViewDto(
        String content,
        Long authorId,
        Long projectId,
        boolean published,
        boolean deleted,
        LocalDateTime updateAt
) {
}