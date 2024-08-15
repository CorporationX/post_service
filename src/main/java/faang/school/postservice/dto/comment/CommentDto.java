package faang.school.postservice.dto.comment;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CommentDto {
    private Long id;

    @NotBlank(message = "Content cannot be blank.")
    @Size(max = 4096, message = "Content cannot exceed 4096 characters.")
    private String content;

    @Positive(message = "Author ID must be a positive number.")
    @NotNull(message = "Author ID cannot be null.")
    private long authorId;

    private List<Long> likeIds;

    @Positive(message = "Post ID must be a positive number.")
    @NotNull(message = "Post ID cannot be null.")
    private Long postId;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
