package faang.school.postservice.dto.post;

import jakarta.validation.constraints.Size;

/**
 * PostUpdateDto — описание класса.
 * <p>
 * TODO: добавить описание назначения и поведения класса.
 * </p>
 *
 * @author Linempy
 * @since 25.07.2025
 */
public record PostUpdateDto(
        @Size(max = 4096)
        String content
) {
}