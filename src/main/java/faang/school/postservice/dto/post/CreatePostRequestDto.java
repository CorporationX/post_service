package faang.school.postservice.dto.post;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Builder;

/**
 * DTO для создания поста.
 * Валидация: контент обязателен, автором должен быть ИЛИ пользователь, ИЛИ проект (ровно один).
 */
@Builder
public record CreatePostRequestDto(
        @NotBlank(message = "content must not be empty")
        @Size(min = 1, max = 4096, message = "Content must be between 1 and 4096 characters")
        String content,
        @Positive(message = "authorId should be > 0")
        Long authorId,
        @Positive(message = "projectId should be > 0")
        Long projectId
) {
    @AssertTrue(message = "Must be sent only one field: authorId or projectId")
    @Schema(hidden = true)
    public boolean isExactlyOneAuthor() {
        return (authorId != null) ^ (projectId != null);
    }
}