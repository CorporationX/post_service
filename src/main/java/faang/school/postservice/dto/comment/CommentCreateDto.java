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
@Schema(description = "Передаваемая DTO при создании")
public class CommentCreateDto {

    @NotNull
    @Schema(description = "ID поста")
    private Long postId;

    @NotBlank
    @Schema(description = "текст комментария")
    private String content;
}
