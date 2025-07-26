package faang.school.postservice.dto.post;

import jakarta.annotation.Nullable;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * PostCreateDto — описание класса.
 * <p>
 * TODO: добавить описание назначения и поведения класса.
 * </p>
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
                return (authorId == null && projectId != null) ||
                        (authorId != null && projectId == null);
        }
}

//TODO: унести константы в отдельный класс PostDtoConstrains