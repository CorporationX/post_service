package faang.school.postservice.dto.like;

import jakarta.validation.constraints.NotNull;

/**
 * DTO для создания лайка под сущностями поста и комментария
 *
 * @param commentId идентификатор комментария
 * @param userId идентификатор пользователя, поставившего лайк
 *
 * @author Linempy
 * @since 12.12.2025
 */
public record LikeCommentCreateDto(
        @NotNull
        Long commentId,
        @NotNull
        Long userId
){
}
