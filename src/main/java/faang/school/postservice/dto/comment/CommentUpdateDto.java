package faang.school.postservice.dto.comment;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Передаваемая DTO при обновлении")
public class CommentUpdateDto {

    @NotNull
    @Schema(description = "ID комментария")
    private Long commentId;

    @NotBlank
    @Schema(description = "новый текст комментария")
    private String newContent;
}
