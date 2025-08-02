package faang.school.postservice.dto.post;

import jakarta.annotation.Nullable;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * DTO для создания нового поста.
 * <p>
 * Содержит данные, необходимые для создания поста. Валидируется перед использованием.
 * </p>
 *
 * @param content   Текст поста (обязательное поле)
 * @param authorId  ID автора-пользователя (обязательно ИЛИ projectId)
 * @param projectId ID проекта-автора (обязательно ИЛИ authorId)
 *
 * @author Linempy
 * @since 25.07.2025
 */
public record PostCreateDto(
        @NotBlank
        @Size(max = 4096)
        String content,
        @Nullable
        @Min(1)
        Long authorId,
        @Nullable
        @Min(1)
        Long projectId
) {
    @AssertTrue(message = "Укажите authorId ИЛИ projectId, но не оба")
    boolean validate() {
        return (authorId == null && projectId != null)
                || (authorId != null && projectId == null);
    }
}

