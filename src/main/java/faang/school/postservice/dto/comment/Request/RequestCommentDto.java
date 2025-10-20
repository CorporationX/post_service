package faang.school.postservice.dto.comment.Request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RequestCommentDto {
    @NotBlank(message = "Content cannot be empty")
    @Size(max = 4096, message = "Content cannot exceed 4096 characters")
    private String content;

    @NotNull(message = "Author ID cannot be null")
    private Long authorId;

    private String largeImageFileKey;
    private String smallImageFileKey;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
