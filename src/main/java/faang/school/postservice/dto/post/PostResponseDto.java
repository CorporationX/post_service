package faang.school.postservice.dto.post;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import lombok.Builder;
import java.time.LocalDateTime;

/**
 * DTO для передачи данных о постах между слоями приложения.
 * Ровно один из полей authorId или projectId должен быть заполнен.
 * Content не может быть пустым
 */
@Builder
public record PostResponseDto(
        Long id,
        @Positive(message = "authorId должен быть > 0") Long authorId,
        @Positive(message = "projectId должен быть > 0") Long projectId,
        @NotBlank(message = "content не должен быть пустым") String content,
        boolean published,
        boolean deleted,
        LocalDateTime createdAt,
        LocalDateTime updatedAt,
        LocalDateTime publishedAt
) {
}
