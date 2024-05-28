package faang.school.postservice.dto.comment;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CreateCommentDto {
    private Long id;

    @NotNull(message = "content can't be empty")
    @Max(value = 4096, message = "content can't be more 4096 symbols")
    private String content;

    @NotNull(message = "authorId can't be empty")
    private Long authorId;

    @NotNull(message = "postId can't be empty")
    private Long postId;
}
