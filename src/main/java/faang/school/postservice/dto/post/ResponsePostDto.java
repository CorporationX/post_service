package faang.school.postservice.dto.post;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
@Builder
public class ResponsePostDto {
    private Long postId;
    private String content;
    @NotNull
    private Long authorId;
    private Long projectId;
    private LocalDateTime scheduledAt;
}
