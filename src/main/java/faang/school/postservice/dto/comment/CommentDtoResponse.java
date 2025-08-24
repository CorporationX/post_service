package faang.school.postservice.dto.comment;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Schema(description = "Возвращаемая сущность комментария")
public class CommentDtoResponse {

    @Schema(description = "ID комментария")
    private Long commentId;

    @Schema(description = "ID поста")
    private Long postId;

    @Schema(description = "ID автора")
    private Long authorId;

    @Schema(description = "текст комментария")
    private String content;

    @Schema(description = "Время создания", example = "01.01.2025 00:00:00" )
    private String createData;

    @Schema(description = "Время обновления", example = "01.01.2025 00:00:00")
    private String updateData;
}
