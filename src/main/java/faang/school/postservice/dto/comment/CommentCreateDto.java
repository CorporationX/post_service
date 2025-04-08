package faang.school.postservice.dto.comment;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class CommentCreateDto {

    @NotBlank
    @Size(min = 1, max = 4096)
    private String content;

    @NotNull
    @Min(1)
    private Long authorId;

    @NotNull
    @Min(1)
    private Long postId;

    private String largeImageFileKey;

    private String smallImageFileKey;
}
