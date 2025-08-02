package faang.school.postservice.dto.post;

import faang.school.postservice.model.enums.PostStatus;
import faang.school.postservice.model.enums.SortType;

/**
 * DTO с параметрами фильтрации постов
 *
 * @param author класс с информацией об авторе поста
 * @param status перечисления для определения статуса поста (DRAFT, PUBLISHED)
 * @param sort перечисления для определения сортировки (DESC, ASC)
 * @param includeDeleted флаг, показывающий нужно ли включать удаленные посты
 *
 * @author Linempy
 * @since 01.08.2025
 */
public record PostFilterDto(
    AuthorFilter author,
    PostStatus status,
    SortType sort,
    Boolean includeDeleted
) {
    public String getSortFieldName() {
        return status == PostStatus.DRAFT ? "createdAt" : "publishedAt";
    }
}