package faang.school.postservice.dto.post;

import faang.school.postservice.model.enums.PostStatus;

/**
 * DTO с параметрами фильтрации постов
 *
 * @param userId ID пользователя-автора
 * @param projectId ID проекта-автора
 * @param status перечисления для определения статуса поста (DRAFT, PUBLISHED)
 * @param includeDeleted флаг, показывающий нужно ли включать удаленные посты
 *
 * @author Linempy
 * @since 01.08.2025
 */
public record PostFilterDto(
    Long userId,
    Long projectId,
    PostStatus status,
    Boolean includeDeleted
) {
    public String getSortFieldName() {
        return status == PostStatus.DRAFT ? "createdAt" : "publishedAt";
    }
}