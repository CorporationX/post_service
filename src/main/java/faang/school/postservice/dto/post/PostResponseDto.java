package faang.school.postservice.dto.post;

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
        Long authorId,
        Long projectId,
        String content,
        boolean published,
        boolean deleted,
        LocalDateTime createdAt,
        LocalDateTime updatedAt,
        LocalDateTime publishedAt,
        LocalDateTime scheduledAt
) {
}
