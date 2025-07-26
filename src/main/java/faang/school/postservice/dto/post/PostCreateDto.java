package faang.school.postservice.dto.post;

import faang.school.postservice.exception.DataValidationException;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

/**
 * DTO для создания новой публикации.
 *
 * @param content   Текстовое содержимое публикации.
 *                  Максимальная длина — 4096 символов.
 * @param authorId  Идентификатор автора публикации.
 *                  Должен быть положительным числом.
 * @param projectId Идентификатор проекта, который публикует публикацию.
 *                  Должен быть положительным числом.
 * @author Myrza
 * @since 24.07.2025
 */
public record PostCreateDto(
        @NotBlank
        @Size(max = 4096)
        String content,
        @Positive
        Long authorId,
        @Positive
        Long projectId
) {
    public void validate() {
        if (authorId != null && projectId != null) {
            throw new DataValidationException("Автором может быть либо пользователь, либо проект");
        }
        if (authorId == null && projectId == null) {
            throw  new DataValidationException("Не указан автор публикации");
        }
    }
}
