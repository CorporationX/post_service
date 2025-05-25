package faang.school.postservice.dto.post;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
@Builder
public class PostDto {
    @NotNull
    private Long postId;
    @NotBlank
    private String content;
    @NotNull
    private Long authorId;
    private Long projectId;
    private LocalDateTime scheduledAt;
}
