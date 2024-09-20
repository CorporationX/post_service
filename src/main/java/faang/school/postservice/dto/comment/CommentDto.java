package faang.school.postservice.dto.comment;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class CommentDto {
    private Long id;

    @NotBlank(message = "The content is empty")
    @Size(min = 1, max = 4096, message = "The content size should be between 1 and 4096 characters")
    private String content;

    @NotBlank(message = "The name is empty")
    private String authorName;

    @NotNull
    private Long authorId;

    @NotNull
    private Long postId;

    private LocalDateTime createdAt;
}
