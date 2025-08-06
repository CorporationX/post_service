package faang.school.postservice.dto.post;

import jakarta.validation.constraints.Size;

/**
 * DTO для обновления содержимого поста.
 * <p>
 * Используется для операций частичного обновления поста.
 * </p>
 *
 * @param content Новое содержимое поста. Должно быть не пустым и не превышать 4096 символов.
 *
 * @author Linempy
 * @since 25.07.2025
 */
public record PostUpdateDto(
        @Size(min = 1, max = 4096)
        String content
) {
}