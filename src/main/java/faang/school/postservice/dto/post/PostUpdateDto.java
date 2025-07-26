package faang.school.postservice.dto.post;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * PostUpdateDto — неизменяемая структура данных (record).
 * <p>
 * TODO: описать предназначение record и его поля.
 * </p>
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
