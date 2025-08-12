package faang.school.postservice.dto.post;

import java.time.LocalDateTime;

/**
 * DTO представляющий публикацию.
 *
 * @param id          Идентификатор публикации.
 * @param content     Текстовое содержимое публикации.
 * @param authorId    Идентификатор автора, создавшего публикацию.
 * @param projectId   Идентификатор проекта, который опубликовал публикацию.
 * @param published   Флаг, указывающий, опубликована ли публикация.
 * @param deleted     Флаг, указывающий, удалена ли публикация (например, мягкое удаление).
 * @param publishedAt Дата и время публикации (фактической или планируемой).
 * @param scheduledAt Дата и время, на которое запланирована публикация.
 * @param createdAt   Дата и время создания публикации.
 * @param updatedAt   Дата и время последнего обновления публикации.
 * @author Myrza
 * @since 24.07.2025
 */
public record PostViewDto(
        Long id,
        String content,
        Long authorId,
        Long projectId,
        Boolean published,
        Boolean deleted,
        LocalDateTime publishedAt,
        LocalDateTime scheduledAt,
        LocalDateTime createdAt,
        LocalDateTime updatedAt,
        Long likeCount
) {
}
