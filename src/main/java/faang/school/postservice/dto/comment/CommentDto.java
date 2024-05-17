package faang.school.postservice.dto.comment;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class CommentDto {
    private Long id;

    @NotNull
    @NotBlank
    @Size(max = 4096)
    private String content;

    @NotNull
    private Long authorId;
}
