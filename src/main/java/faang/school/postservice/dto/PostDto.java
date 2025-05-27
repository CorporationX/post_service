package faang.school.postservice.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PostDto {
    private Long id;
    @NotNull(message = "post content is empty")
    private String content;
    @NotNull(message = "post author is empty")
    private Long authorId;
    private Long projectId;
    private Long likePostCount;
    private Long likeCommentCount;
}
