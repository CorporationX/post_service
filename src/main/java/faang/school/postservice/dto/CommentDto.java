package faang.school.postservice.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class CommentDto {
    private Long id;
    private Long postId;
    @NotBlank(message = "Comment content cannot be empty")
    @Size(max = 4096, message = "Comment content is too long (max 4096)")
    private String content;
    private Long authorId;
    private LocalDateTime createdAt; // Readonly
}
