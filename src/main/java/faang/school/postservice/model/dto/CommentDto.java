package faang.school.postservice.model.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class CommentDto {
    private Long id;
    private Long postId;
    @NotNull(message = "content shouldn't be null")
    @NotBlank(message = "description should not be blank")
    @Size(max = 4096)
    private String content;
    @NotNull(message = "content shouldn't be null")
    private Long authorId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
