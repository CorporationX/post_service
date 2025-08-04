package faang.school.postservice.dto.post;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * DTO для обновления существующего поста.
 * <p>
 * Содержит данные, которые могут быть изменены при редактировании поста.
 * Используются аннотации валидации для проверки корректности входных данных.
 *
 * @param content Новое содержимое публикации (макс. 4096 символов).
 * @author Myrza
 * @since 24.07.2025
 */
public record PostUpdateDto(
        @NotBlank
        @Size(max = 4096)
        String content
) {
}
