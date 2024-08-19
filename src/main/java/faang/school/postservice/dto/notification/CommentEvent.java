package faang.school.postservice.dto.notification;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CommentEvent {
    @NotNull
    private Long id;
    @NotNull
    private Long authorId;
    @NotNull
    private Long authorPostId;
    @NotNull
    private Long postId;
    @NotBlank
    private String content;
}
