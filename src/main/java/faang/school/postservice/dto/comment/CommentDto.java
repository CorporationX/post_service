package faang.school.postservice.dto.comment;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Builder
@Data
public class CommentDto {
    private Long id;
    @NotNull(message = "Content can't be cannot be empty")
    @Size(min = 1, max = 4096, message = "Content should be at least 1 symbol long and max 4096 symbols")
    private String content;
    @NotNull(message = "Content can't be cannot be empty")
    private Long authorId;
    private Long likesNum;
    @NotNull(message = "Content can't be cannot be empty")
    private Long postId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
