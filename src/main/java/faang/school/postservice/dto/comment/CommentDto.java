package faang.school.postservice.dto.comment;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CommentDto {
    private Long id;

    @NotEmpty(message = "Content must not be empty")
    @Size(max = 4096, message = "Content must contain fewer than 4096 characters")
    private String content;

    private Long authorId;
    private Long postId;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Boolean verified = false;
}