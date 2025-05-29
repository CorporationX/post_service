package faang.school.postservice.dto.comment;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Schema(description = "Передаваемый комментарий при обновлении")
public class CommentUpdateDto {

    @Schema(description = "ID комментария в базе")
    @NotNull
    private Long commentId;

    @Schema(description = "Новый текст комментария")
    @NotBlank
    private String content;
}
