package faang.school.postservice.dto.post;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import lombok.Builder;

/**
 * DTO для создания поста.
 * Валидация: контент обязателен, автором должен быть ИЛИ пользователь, ИЛИ проект (ровно один).
 */
@Builder
public record CreatePostRequestDto(
        @NotBlank(message = "content не должен быть пустым") String content,
        @Positive(message = "authorId должен быть > 0") Long authorId,
        @Positive(message = "projectId должен быть > 0") Long projectId
) {
    @AssertTrue(message = "Должен быть указан ровно один автор: authorId или projectId")
    public boolean isExactlyOneAuthor() {
        return (authorId != null) ^ (projectId != null);
    }
}