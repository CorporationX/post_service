package faang.school.postservice.dto.comment;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Builder
@Data
public class CommentEventDto {
    @NotNull
    private Long postId;
    @NotNull
    private Long authorId;
    @NotNull
    private Long commentId;
    @NotNull
    private LocalDateTime commentedAt;
}
