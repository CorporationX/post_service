package faang.school.postservice.dto.comment;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
@Builder
@AllArgsConstructor
public class CommentEvent {
    @NotNull
    private Long commentId;
    @NotNull
    private Long authorId;
    @NotNull
    private String content;
    @NotNull
    private Long postId;
}
