package faang.school.postservice.dto.comment;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class CreateCommentDto {

    @Size(max = 4096)
    @NotBlank
    private String content;

    @NotNull
    @Positive
    private Long authorId;

    @NotNull
    @Positive
    private Long postId;
}
