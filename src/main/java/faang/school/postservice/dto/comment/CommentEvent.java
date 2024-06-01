package faang.school.postservice.dto.comment;

import jakarta.validation.constraints.NotNull;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.LocalDateTime;
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
    @NotNull
    private Long authorOfPostId;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private LocalDateTime createdAt;
}
