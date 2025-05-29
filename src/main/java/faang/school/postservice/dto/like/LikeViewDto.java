package faang.school.postservice.dto.like;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * DTO для представления информации о лайке.
 * <p>
 * Лайк может относиться либо к посту, либо к комментарию, поэтому одно из полей
 * commentId/postId всегда должно быть заполнено.
 *
 * @author gulnaz21
 */
@Data
@Schema(description = "Информация о лайке пользователя")
public class LikeViewDto {
    @Schema(description = "Уникальный идентификатор лайка", example = "123", requiredMode = Schema.RequiredMode.REQUIRED)
    private Long id;

    @Schema(description = "ID пользователя, поставившего лайк", example = "456", requiredMode = Schema.RequiredMode.REQUIRED)
    private Long userId;

    @Schema(description = "ID комментария, к которому относится лайк (если null - лайк на пост)", example = "789", nullable = true)
    private Long commentId;

    @Schema(description = "ID поста, к которому относится лайк (если null - лайк на комментарий)", example = "101", nullable = true)
    private Long postId;
}
