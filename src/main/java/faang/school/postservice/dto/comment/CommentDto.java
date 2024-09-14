package faang.school.postservice.dto.comment;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CommentDto {
    private Long id;

    @NotBlank
    @Size(min = 1, max = 4096, message = "The length of the content is not in the range from 1 to 4096 (inclusive)")
    private String content;

    @NotNull
    private Long authorId;

    @NotNull
    private Long postId;
}
