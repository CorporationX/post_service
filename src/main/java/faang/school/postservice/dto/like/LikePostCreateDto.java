package faang.school.postservice.dto.like;

import jakarta.validation.constraints.NotNull;

/**
 * DTO для создания лайка под постом
 *
 * @param postId идентификатор поста
 * @param userId идентификатор пользователя, поставившего лайк
 *
 * @author Linempy
 * @since 12.12.2025
 */
public record LikePostCreateDto(
        @NotNull
        Long postId,
        @NotNull
        Long userId
) {
}