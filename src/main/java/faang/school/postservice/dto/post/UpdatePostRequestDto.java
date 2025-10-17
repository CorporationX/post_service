package faang.school.postservice.dto.post;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;

/**
 * DTO для обновления поста. Публикация выполняется отдельным действием.
 */
@Builder
public record UpdatePostRequestDto(
        @NotBlank(message = "content не должен быть пустым") String content
) {
}