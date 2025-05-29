package faang.school.postservice.dto.comment;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Schema(description = "Передаваемый комментарий при создании")
public class CommentCreateDto {

    @Schema(description = "ID поста, который комментируют")
    @NotBlank
    private Long postId;

    @Schema(description = "ID автора комментария")
    @NotBlank
    private Long authorId;

    @Schema(description = "Текст комментария")
    @NotBlank
    private String content;
}
