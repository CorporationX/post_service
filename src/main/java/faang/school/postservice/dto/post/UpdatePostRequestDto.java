package faang.school.postservice.dto.post;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Builder;

import java.time.LocalDateTime;

/**
 * DTO для обновления поста. Публикация выполняется отдельным действием.
 */
@Builder
public record UpdatePostRequestDto(
        @NotBlank(message = "content must not be empty")
        @Size(min = 1, max = 4096, message = "Content must be between 1 and 4096 characters")
        String content,
        LocalDateTime scheduledAt
) {

    public UpdatePostRequestDto(String content) {
        this(content, null);
    }

}